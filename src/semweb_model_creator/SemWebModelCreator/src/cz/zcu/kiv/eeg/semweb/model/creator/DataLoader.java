package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    private final String CLASS_NODE = "class";
    private final String PROPERTY_NODE = "property";
    private final String CLASS_PARENT_NODE = "parent_class";
    private final String PROPERTY_PARENT_NODE = "parent_property";
    private final String CHILD_NODE = "child";
    private final String RANGE_ATTRIBUTE = "range";
    private final String DOMAIN_ATTRIBUTE = "domain";


    File xmlFile;
    List<ClassDataItem> classes;
    List<PropertyDataItem> properties;


    public DataLoader(File sourceDataFile) {
        classes = new ArrayList<ClassDataItem>();
        properties = new ArrayList<PropertyDataItem>();
        
        this.xmlFile = sourceDataFile;
    }

    public boolean loadData() {
        try {
            SAXReader reader = new SAXReader();
            Document xmlDoc = reader.read(xmlFile);

            Element rootNode = xmlDoc.getRootElement();

            rootTreeIterate(rootNode);

        } catch (DocumentException ex) {
            //Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
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

    private void rootTreeIterate(Element rootNode) throws DocumentException {

        Iterator rootIterator = rootNode.elementIterator();

        while (rootIterator.hasNext()) {

            Element node = (Element) rootIterator.next();

            if (node.getName().equals(CLASS_NODE)) {
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

    private void processClassNode (Element node) {
        classes.add(new ClassDataItem(node.attributeValue("name")));
    }

    private void processPropertyNode (Element node) {
        properties.add(new PropertyDataItem(node.attributeValue("name"), node.attributeValue("range"), node.attributeValue("domain")));
    }

    private void processClassParentNode (Element node) {
        ClassDataItem cls = new ClassDataItem(node.attributeValue("name"));

        Iterator childNodes = node.elementIterator();

        while (childNodes.hasNext()) {
            cls.addChildNode(((Element) childNodes.next()).attributeValue("name"));
        }

        classes.add(cls);
    }

    private void processPropertyParentNode (Element node) {
        PropertyDataItem prp = new PropertyDataItem(node.attributeValue("name"));
        
        Iterator childNodes = node.elementIterator();

        while (childNodes.hasNext()) {
            
            prp.addChildNode(((Element) childNodes.next()).attributeValue("name"));
        }

        properties.add(prp);
    }
}
