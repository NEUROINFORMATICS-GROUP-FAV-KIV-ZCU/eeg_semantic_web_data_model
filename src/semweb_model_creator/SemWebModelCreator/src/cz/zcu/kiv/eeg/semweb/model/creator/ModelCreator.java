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
import cz.zcu.kiv.eeg.semweb.model.testdata.Triple;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ModelCreator {

    private final String prefixMod = "eeg";

    //private Oracle oracleConnection;
    //private ModelOracleSem oracleModel;
    //VirtGraph virtuosoGraph;

    private String prefixURI;
    private String tablePrefix;
    private OntModel jenaModel;
    private Model basicModel;

    private static final Logger logger = Logger.getLogger(ModelCreator.class);

    public boolean connect(String dbUrl, String username, String password) {
        Locale.setDefault(Locale.US);   //avoi different national dateTime and number formats

        try {
            //oracleConnection = new Oracle(dbUrl, username, password);
            //virtuosoGraph = new VirtGraph (dbUrl, "dba", "dba");
            //basicModel = new VirtModel(virtuosoGraph);
            basicModel = VirtModel.openDatabaseModel("mujModel", dbUrl, username, username);
        }catch (Exception ex) {
            logger.error("Connecting error:", ex);
            return false;
        }
        return true;
    }

    public boolean disconnect() {
        try {
            if (basicModel != null) {
                basicModel.close();
            }
        } catch (Exception ex) {
             logger.error("Disconnecting error:", ex);
            return false;
        }
        return true;
    }

    public boolean createModel(String modelName, String prefixURI, String tablePrefix, List<ClassDataItem> classes, List<PropertyDataItem> properties, List<TableItem> tables) {
        try {
            //oracleModel = ModelOracleSem.createOracleSemModel(oracleConnection, modelName);
            this.prefixURI = prefixURI;
            this.tablePrefix = tablePrefix;

            jenaModel = ModelFactory.createOntologyModel();


            //jenaModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, basicModel);
            //oracleModel.setNsPrefix(prefixMod, this.prefixURI);

            createClasses(classes);
            createProperties(properties);
            //createTables(tables);         //TODO CREATE TABLES

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


    public boolean removeModel(String modelName) {
        
        logger.info("Removing model...");

        try {
            //OracleUtils.dropSemanticModel(oracleConnection, modelName);
        } catch (Exception ex) {
            logger.error("Removing model error:", ex);
            return false;
        }
        logger.info("Removing model done.");
        return true;
    }

    public boolean removeTables(String prefix, List<TableItem> tables) {

        logger.info("Removing tables...");

        try {

            for (TableItem table: tables) {

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

            if (classItem.hasChildNodes()) {

                for (ClassDataItem childNode: classItem.getChildNodes()) {
                    //jenaModel.createClass(prefixURI + childNode.getName()).addSuperClass(parentNode);
                    OntClass child = jenaModel.createClass(prefixURI + childNode.getName());
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
}
