package cz.zcu.kiv.eeg.semweb.model.creator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;
import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.TableItem;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.testdata.Triple;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Model Creator uses JENA API to create ontology model 
 * by requested object model loaded from XML document
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ModelCreator {

    private String prefixURI; //default URI prefix
    private String tablePrefix; //default table prefix
    private OntModel jenaModel; //jena model API
    private Model basicModel; // basic sem-web model ()
    private DbConnector dbConnector; //database connection
    private final String TABLE_DATA_TYPE_TEXT = "text"; //Table with data file large text object model dataType name
    private final String TABLE_DATA_TYPE_BINARY = "binary"; //Table with data file large binary object model dataType name
    private static final Logger logger = Logger.getLogger(ModelCreator.class);

    public ModelCreator(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    /**
     * Database model connect
     *
     * @return
     */
    public boolean connect() {
        basicModel = dbConnector.connect();

        return basicModel != null;
    }

    /**
     * Disconnect database model
     */
    public void disconnect() {
        dbConnector.disconnect();
    }

    /**
     * Create onotology JENA model and inser all classes, properties and tables to relational database
     *
     * @param prefixURI default URI prefix
     * @param tablePrefix default table prefix
     * @param classes model classes
     * @param properties model properties
     * @param tables model(relational) tables
     * @return true if creating successfull, otherwise false
     */
    public boolean createModel(String prefixURI, String tablePrefix, List<ClassDataItem> classes, List<PropertyDataItem> properties, List<TableItem> tables) {
        try {
            this.prefixURI = prefixURI;
            this.tablePrefix = tablePrefix;

            jenaModel = ModelFactory.createOntologyModel();

            createClasses(classes);
            createProperties(properties);
            createTables(tables);

            basicModel.add(jenaModel);

        } catch (Exception ex) {
            logger.error("Model creating error:", ex);
            return false;
        }
        return true;
    }

    /**
     * Inser simple model data wrapped as Triple statement
     *
     * @param modelName name of model
     * @param prefixURI dafault prefix URI
     * @param triples list of inserted triples
     *
     * @return true if insert successfull
     */
    public boolean insertData(String modelName, String prefixURI, List<Triple> triples) {

        try {
            jenaModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM, basicModel);
            this.prefixURI = prefixURI;

            OntResource subject = null;
            OntProperty predicate = null;
            RDFNode object = null; //OntResource when URI, Literal when literal

            for (Triple item : triples) { //iterate over all triplets

                predicate = jenaModel.createOntProperty(prefixURI + item.getPredicate()); //predicate is existing URI with set range and domain (should)

                if ((predicate.getRange() != null) && (predicate.getDomain() != null)) { // avoid nonExisting predicates

                    subject = jenaModel.createIndividual(prefixURI + item.getSubject(), predicate.getDomain()); //Set subject as class instance by predicate domain

                    if (predicate.getRange().getNameSpace().equals(XSD.getURI())) { //literal object
                        object = jenaModel.createTypedLiteral(item.getObject(), predicate.getRange().getURI()); //create typed literal
                    } else { //URI object
                        object = jenaModel.createIndividual(prefixURI + item.getObject(), predicate.getRange()); //Set object as class instance by predicate range
                    }

                    jenaModel.add(subject, predicate, object); //add new triple
                } else {
                    throw new Exception("Invalid predicate, no range or domain set for " + item.getPredicate());
                }
            }
        } catch (Exception ex) {
            logger.error("Model creating error:", ex);
            return false;
        }
        return true;
    }

    /**
     * Remove data model
     */
    public void removeModel() {
        dbConnector.removeModel();
    }

    /**
     * Remove tables from relational database
     *
     * @param prefix default table prefix
     * @param tables list of tables to remove
     * @return true if remove successfull, otherwise false
     */
    public boolean removeTables(String prefix, List<TableItem> tables) {

        logger.info("Removing tables...");
        try {
            Statement st = dbConnector.getRelationConn();

            for (TableItem table : tables) {
                st.execute("DROP TABLE " + prefix + table.getName());
            }

        } catch (Exception ex) {
            logger.error("Removing tables error:", ex);
            return false;
        }
        logger.info("Removing tables done.");
        return true;
    }

    /**
     * Create tables in relational model
     *
     * @param tables Tables to create
     *
     * @throws SQLException
     */
    private void createTables(List<TableItem> tables) throws SQLException {

        logger.info("Creating tables...");

        for (TableItem table : tables) {

            Statement st = dbConnector.getRelationConn();
            st.execute("CREATE TABLE " + tablePrefix + table.getName() + " (\"URI\" " + dbConnector.getVarcharType() + ", \"DATA\" " + getSqlDataType(table.getType()) + ")");
        }

        logger.info("Creating tables done.");
    }

    /**
     * Create model classes
     *
     * @param classes list with classes to create
     */
    private void createClasses(List<ClassDataItem> classes) {

        logger.info("Creating classes...");

        for (ClassDataItem classItem : classes) {

            OntClass parentNode = jenaModel.getOntClass(prefixURI + classItem.getName());

            if (parentNode == null) {
                parentNode = jenaModel.createClass(prefixURI + classItem.getName());
            }

            if (classItem.getDescription() != null) {
                parentNode.setComment(classItem.getDescription(), null);
            }

            if (classItem.hasChildNodes()) {

                for (ClassDataItem childNode : classItem.getChildNodes()) {
                    OntClass child = jenaModel.createClass(prefixURI + childNode.getName());

                    if (childNode.getDescription() != null) {
                        child.setComment(childNode.getDescription(), null);
                    }
                    parentNode.addSubClass(child);
                }
            }
        }
        logger.info("Creating classes done.");
    }

    /**
     * Create properties in model
     *
     * @param properties list with properties to create
     */
    private void createProperties(List<PropertyDataItem> properties) {

        logger.info("Creating properties...");

        for (PropertyDataItem propertyItem : properties) {

            if (propertyItem.hasChildNodes()) {

                OntProperty parent = jenaModel.createOntProperty(prefixURI + propertyItem.getName());

                for (PropertyDataItem childNode : propertyItem.getChildNodes()) {
                    parent.addSubProperty(jenaModel.createOntProperty(prefixURI + childNode.getName()));
                }

            } else {
                OntProperty prop = jenaModel.createOntProperty(prefixURI + propertyItem.getName());
                prop.setDomain(jenaModel.createClass(prefixURI + propertyItem.getDomain()));
                prop.setRange(getRange(propertyItem.getRange()));

                if (propertyItem.getDescription() != null) {
                    prop.setComment(propertyItem.getDescription(), null);
                }
            }
        }
        logger.info("Creating properties done.");
    }

    /**
     * Get property range
     *
     * @param range range URI
     * @return JENA Resource object range
     */
    private Resource getRange(String range) {

        if (range.startsWith("xsd")) {
            return jenaModel.createResource(range.replace("xsd:", XSD.getURI()));
        } else {
            return jenaModel.createResource(prefixURI + range);
        }
    }

    /**
     * Return name database system specific datatype name
     *
     * @param type selected type
     * @return converted type name
     */
    private String getSqlDataType(String type) {
        if (type.equals(TABLE_DATA_TYPE_TEXT)) {
            return dbConnector.getLargeTextType();
        } else {
            return dbConnector.getLargeBinaryType();
        }
    }
}
