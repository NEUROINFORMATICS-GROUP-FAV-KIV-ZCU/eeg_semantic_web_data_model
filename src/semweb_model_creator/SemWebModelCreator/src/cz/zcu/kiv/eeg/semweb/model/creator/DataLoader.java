package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.TableItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * DataLoader reads XML input file containing complete model hierarchy of classes and properties
 * and return that as object model
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DataLoader {

    private final String CLASS_NODE = "class"; //CLASS node name
    private final String PROPERTY_NODE = "property"; //PROPERTY node name
    private final String CLASS_PARENT_NODE = "parent_class"; //PARENT CLASS node name
    private final String PROPERTY_PARENT_NODE = "parent_property"; //PARENT PROPERTY node name
    private final String RANGE_ATTRIBUTE = "range"; //PROPERTY RANGE attribute name
    private final String DOMAIN_ATTRIBUTE = "domain"; //PROPERTY DOMAIN attribute name
    private final String DESCRIPTION_ATTRIBUTE = "description"; //PROPERTY and CLASS DESCRITPION attribute
    private final String TABLE_NODE = "data_table"; //TABLE NAME node name
    private static final Logger logger = Logger.getLogger(DataLoader.class);
    File xmlFile; //data model XML file
    List<ClassDataItem> classes; //model classes
    List<PropertyDataItem> properties; //model properties
    List<TableItem> tables; //model tables

    public DataLoader(File sourceDataFile) {
        classes = new ArrayList<ClassDataItem>();
        properties = new ArrayList<PropertyDataItem>();
        tables = new ArrayList<TableItem>();

        this.xmlFile = sourceDataFile;
    }

    /**
     * Load data from XML file
     * @return
     */
    public boolean loadData() {
        try {
            SAXReader reader = new SAXReader();
            Document xmlDoc = reader.read(xmlFile);

            Element rootNode = xmlDoc.getRootElement();

            rootTreeIterate(rootNode);

        } catch (DocumentException ex) {
            logger.error("Document loading error:", ex);
            return false;
        }

        return true;
    }

    public List<ClassDataItem> getClasses() {
        return this.classes;
    }

    public List<PropertyDataItem> getProperties() {
        return this.properties;
    }

    public List<TableItem> getTables() {
        return tables;
    }

    /**
     * Iterate over all XML file top level nodes and process them
     *
     * @param rootNode XML document root node
     *
     * @throws DocumentException
     */
    private void rootTreeIterate(Element rootNode) throws DocumentException {

        Iterator rootIterator = rootNode.elementIterator();

        while (rootIterator.hasNext()) {

            Element node = (Element) rootIterator.next();

            if (node.getName().equals(TABLE_NODE)) {
                processTableNode(node);
            } else if (node.getName().equals(CLASS_NODE)) {
                processClassNode(node);
            } else if (node.getName().equals(CLASS_PARENT_NODE)) {
                processClassParentNode(node);
            } else if (node.getName().equals(PROPERTY_NODE)) {
                processPropertyNode(node);
            } else if (node.getName().equals(PROPERTY_PARENT_NODE)) {
                processPropertyParentNode(node);
            } else {
                throw new DocumentException("Node with illegal name \"" + node.getName() + "\" found");
            }
        }
    }

    /**
     * Process class node
     *
     * @param node class node
     */
    private void processClassNode(Element node) {
        classes.add(new ClassDataItem(node.attributeValue("name"), node.attributeValue(DESCRIPTION_ATTRIBUTE)));
    }

    /**
     * Process property node
     *
     * @param node property node
     */
    private void processPropertyNode(Element node) {
        properties.add(new PropertyDataItem(node.attributeValue("name"), node.attributeValue(RANGE_ATTRIBUTE), node.attributeValue(DOMAIN_ATTRIBUTE), node.attributeValue(DESCRIPTION_ATTRIBUTE)));
    }

    /**
     * Process class inheritance nodes
     *
     * @param node parent class node
     */
    private void processClassParentNode(Element node) {
        ClassDataItem cls = new ClassDataItem(node.attributeValue("name"), node.attributeValue(DESCRIPTION_ATTRIBUTE));

        Iterator childNodes = node.elementIterator();

        while (childNodes.hasNext()) {
            cls.addChildNode(((Element) childNodes.next()).attributeValue("name"));
        }

        classes.add(cls);
    }

    /**
     * Process property inheritance node
     *
     * @param node parent property node
     */
    private void processPropertyParentNode(Element node) {
        PropertyDataItem prp = new PropertyDataItem(node.attributeValue("name"));

        Iterator childNodes = node.elementIterator();

        while (childNodes.hasNext()) {

            prp.addChildNode(((Element) childNodes.next()).attributeValue("name"));
        }

        properties.add(prp);
    }

    /**
     * Process table node
     *
     * @param node table node
     *
     * @throws DocumentException
     */
    private void processTableNode(Element node) throws DocumentException {

        TableItem table = new TableItem(node.attributeValue("name"));
        table.setDataType(node.attributeValue("type"));

        tables.add(table);
    }
}
