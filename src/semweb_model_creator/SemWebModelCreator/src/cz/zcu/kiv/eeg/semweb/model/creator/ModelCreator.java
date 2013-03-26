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
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ModelCreator {

    private String prefixURI;
    private String tablePrefix;
    private OntModel jenaModel;
    private Model basicModel;
    
    private DbConnector dbConnector;

    private final String TABLE_DATA_TYPE_TEXT = "text";
    private final String TABLE_DATA_TYPE_BINARY = "binary";

    private static final Logger logger = Logger.getLogger(ModelCreator.class);

    public ModelCreator(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public void connect() {
        basicModel = dbConnector.connect();
    }

    public void disconnect() {
        dbConnector.disconnect();
    }

    public boolean createModel(String prefixURI, String tablePrefix, List<ClassDataItem> classes, List<PropertyDataItem> properties, List<TableItem> tables) {
        try {
            this.prefixURI = prefixURI;
            this.tablePrefix = tablePrefix;

            jenaModel = ModelFactory.createOntologyModel();

            createClasses(classes);
            createProperties(properties);
            createTables(tables);         

            //oracleModel.add(jenaModel, true);
            basicModel.add(jenaModel);

        } catch (Exception ex) {
            logger.error("Model creating error:", ex);
            return false;
        }
        return true;
    }

    public boolean insertData(String modelName, String prefixURI, List<Triple> triples) {

        try {

            //oracleModel = ModelOracleSem.createOracleSemModel(oracleConnection, modelName);
            jenaModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, basicModel);
            this.prefixURI = prefixURI;

            OntResource subject = null;
            OntProperty predicate = null;
            RDFNode object = null; //OntResource when URI, Literal when literal

            for (Triple item: triples) { //iterate over all triplets

                predicate = jenaModel.createOntProperty(prefixURI + item.getPredicate()); //predicate is existing URI with set range and domain (should)

                if ((predicate.getRange() != null) && (predicate.getDomain() != null)) { // avoid nonExisting predicates

                    subject = jenaModel.createIndividual(prefixURI + item.getSubject(), predicate.getDomain()); //Set subject as class instance by predicate domain

                    if (predicate.getRange().getNameSpace().equals(XSD.getURI())) { //literal object

                        //if (predicate.getRange().getURI().equals(XSD.dateTime.getURI())) {
                          //  object = jenaModel.createTypedLiteral(DatatypeConverter.parseDateTime(item.getObject())); //create typed literal as dateTime
                        //}else {
                            object = jenaModel.createTypedLiteral(item.getObject(), predicate.getRange().getURI()); //create typed literal
                        //}


                    }else { //URI object
                        object = jenaModel.createIndividual(prefixURI + item.getObject(), predicate.getRange()); //Set object as class instance by predicate range

                    }

                    jenaModel.add(subject, predicate, object); //add new triple


                }else {
                    throw new Exception("Invalid predicate, no range or domain set for " + item.getPredicate());
                }
            }

            //oracleModel.add(jenaModel, true);

        } catch (Exception ex) {
            logger.error("Model creating error:", ex);
            return false;
        }
        return true;
    }


    public void removeModel() {
        dbConnector.removeModel();
    }

    public boolean removeTables(String prefix, List<TableItem> tables) { //TODO

        logger.info("Removing tables...");
        try {
            Statement st = dbConnector.getRelationConn();

            for (TableItem table: tables) {
                st.execute("DROP TABLE " + prefix + table.getName());
                //oracleConnection.getConnection().createStatement().execute("DROP TABLE " + prefix + table.getName());
            }

        } catch (Exception ex) {
            logger.error("Removing tables error:", ex);
            return false;
        }
        logger.info("Removing tables done.");
        return true;
    }

    private void createTables(List<TableItem> tables) throws SQLException {

        logger.info("Creating tables...");

        for (TableItem table: tables) {

            Statement st = dbConnector.getRelationConn();
            st.execute("CREATE TABLE " + tablePrefix + table.getName() + " (\"URI\" " + dbConnector.getVarcharType()+ ", \"DATA\" " + getSqlDataType(table.getType()) + ")");
            //oracleConnection.getConnection().createStatement().execute("CREATE TABLE " + tablePrefix + table.getName() + "(URI VARCHAR2(256), DATA " + table.getType().name() + ")");
        }

        logger.info("Creating tables done.");
    }


    private void createClasses(List<ClassDataItem> classes) {

        logger.info("Creating classes...");

        for (ClassDataItem classItem: classes) {

            OntClass parentNode = jenaModel.getOntClass(prefixURI + classItem.getName());

            if (parentNode == null) {
                parentNode = jenaModel.createClass(prefixURI + classItem.getName());
            }

            if (classItem.getDescription() != null) {
                parentNode.setComment(classItem.getDescription(), null);
            }

            if (classItem.hasChildNodes()) {

                for (ClassDataItem childNode: classItem.getChildNodes()) {
                    //jenaModel.createClass(prefixURI + childNode.getName()).addSuperClass(parentNode);
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

    private void createProperties(List<PropertyDataItem> properties) {

        logger.info("Creating properties...");

        for (PropertyDataItem propertyItem: properties) {

            if (propertyItem.hasChildNodes()) {

                OntProperty parent = jenaModel.createOntProperty(prefixURI + propertyItem.getName());

                for (PropertyDataItem childNode: propertyItem.getChildNodes()) {
                    parent.addSubProperty(jenaModel.createOntProperty(prefixURI + childNode.getName()));
                }

            }else {
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

    private Resource getRange(String range) {

        if (range.startsWith("xsd")) {
            return jenaModel.createResource(range.replace("xsd:", XSD.getURI()));
        }else {
            return jenaModel.createResource(prefixURI + range);
        }
    }

    private String getSqlDataType(String type) {
        if (type.equals(TABLE_DATA_TYPE_TEXT)) {
            return dbConnector.getLargeTextType();
        }else {
            return dbConnector.getLargeBinaryType();
        }
    }

}
