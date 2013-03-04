package cz.zcu.kiv.eeg.semweb.model.creator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import oracle.spatial.rdf.client.jena.ModelOracleSem;
import oracle.spatial.rdf.client.jena.Oracle;
import oracle.spatial.rdf.client.jena.OracleUtils;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ModelCreator {

    private final String modelPrefix = "eeg:";

    private Oracle oracleConnection;
    private ModelOracleSem oracleModel;
    private String prefixURI;
    private OntModel jenaModel;

    public boolean connect(String dbUrl, String username, String password) {

        try {
            oracleConnection = new Oracle(dbUrl, username, password);
        }catch (Exception ex) {

            return false;
        }
        return true;
    }

    public boolean disconnect() {
        try {
            if (oracleModel != null) {
                oracleModel.close();
            }
            oracleConnection.dispose();
        } catch (SQLException ex) {
            // Logger.getLogger(ModelCreator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean createModel(String modelName, String prefixURI, List<ClassDataItem> classes, List<PropertyDataItem> properties) {
        try {
            oracleModel = ModelOracleSem.createOracleSemModel(oracleConnection, modelName);
            this.prefixURI = prefixURI;

            jenaModel = ModelFactory.createOntologyModel();
            oracleModel.setNsPrefix(modelPrefix.replace(":", ""), this.prefixURI); //namespace prefix do not contain separator

            createClasses(classes);
            createProperties(properties);

            oracleModel.add(jenaModel, true);

        } catch (Exception ex) {
            // Logger.getLogger(ModelCreator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean removeModel(String modelName) {
        try {
            OracleUtils.dropSemanticModel(oracleConnection, modelName);
        } catch (SQLException ex) {
            // Logger.getLogger(ModelCreator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private void createClasses(List<ClassDataItem> classes) {

        for (ClassDataItem classItem: classes) {

            if (classItem.hasChildNodes()) {

                OntClass parent = jenaModel.createClass(modelPrefix + classItem.getName());

                for (ClassDataItem childNode: classItem.getChildNodes()) {
                    parent.addSubClass(jenaModel.createClass(modelPrefix + childNode.getName()));
                }

            }else {
                jenaModel.createClass(modelPrefix + classItem.getName());
            }
        }

    }

    private void createProperties(List<PropertyDataItem> properties) {

        for (PropertyDataItem propertyItem: properties) {

            if (propertyItem.hasChildNodes()) {

                OntProperty parent = jenaModel.createOntProperty(modelPrefix + propertyItem.getName());

                for (PropertyDataItem childNode: propertyItem.getChildNodes()) {
                    parent.addSubProperty(jenaModel.createOntProperty(modelPrefix + childNode.getName()));
                }

            }else {
                OntProperty prop = jenaModel.createOntProperty(modelPrefix + propertyItem.getName());
                prop.setDomain(jenaModel.createClass(modelPrefix + propertyItem.getDomain()));
                prop.setRange(getRange(propertyItem.getRange()));
            }

        }
    }

    private Resource getRange(String range) {

        if (range.startsWith("xsd")) {
            return jenaModel.createResource(range);
        }else {
            return jenaModel.createResource(modelPrefix + range);
        }

    }
}
