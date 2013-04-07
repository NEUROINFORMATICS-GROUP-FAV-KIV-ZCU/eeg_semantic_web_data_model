package cz.zcu.kiv.eeg.semweb.model.api;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.LiteralItem;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import cz.zcu.kiv.eeg.semweb.model.api.utils.DataConverter;
import cz.zcu.kiv.eeg.semweb.model.api.utils.InstanceUriGen;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.search.Condition;
import cz.zcu.kiv.eeg.semweb.model.search.PortalClassInstanceSelector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Main class of EEG/ERP portal semantic web model API
 *  All communication with database provided via this class
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PortalModel {

    private DbConnector dbConn;

    private Model basicModel; //Oracle semWeb model (basic)
    private OntModel ontologyModel; //Jena ontology model

    private java.sql.Statement relDbStatement;

    private String defNamespace; //default namespace for EEG/ERP
    private String tblPrefix;
    private OntModelSpec reasoner;

    private static final String W3_RDF_OBJECT = "http://www.w3.org";
    private static final String TABLE_URI_COLUMN = "URI";
    private static final String TABLE_DATA_COLUMN = "DATA";


    public PortalModel(DbConnector connector, String namespace, String tblPrefix, OntModelSpec reasoner) {
        this.dbConn = connector;
        this.defNamespace = namespace;
        this.tblPrefix = tblPrefix;
        this.reasoner = reasoner;

        //this.basicModel = model;
        // -EE-   this.ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.DAML_MEM_RDFS_INF, model); //create Ontology model based on Oracle semWeb model


        //this.ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF, model); //create Ontology model based on Oracle semWeb model
        //this.ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, model);
    }

    public boolean connect() {

        basicModel = dbConn.connect();
        if (basicModel == null) {
            return false;
        }

        ontologyModel = ModelFactory.createOntologyModel(reasoner, basicModel);
        this.relDbStatement = dbConn.getRelationConn();
        if (ontologyModel == null) {
            return false;
        }

        return true;
    }


    /**
     * Close model and commit all changes.
     */
    public void close() {
        ontologyModel.close();
    }

    /**
     * Return instance of specified class (return random instance if more present
     * )
     * @param parentClass
     * @return
     */
    @Deprecated
    public Item getInstance(String parentClass, boolean direct) throws NonExistingUriNodeException {
        return listInstance(parentClass, direct).get(0);
    }

    /**
     * Return list of specified class instances (all of them)
     * @param parentClass
     * @param direct true if only direct class instances should be listed (no subclasses)
     * @return
     */
    public List<Item> listInstance(String parentClass, boolean direct) throws NonExistingUriNodeException {

        OntClass parent = ontologyModel.getOntClass(parentClass);
        List<Item> instList;


        if (parent == null) {
            throw new NonExistingUriNodeException("Parent class with URI " + parentClass + " does not exist.");
        } else {

            instList = new ArrayList<Item>();
            ExtendedIterator<? extends OntResource> instances = parent.listInstances(direct);

            while (instances.hasNext()) {
                OntResource res = instances.next();

                if (res.isIndividual()) {
                    instList.add(new UriItem(res.getURI(), this));
                }
            }
        }
        return instList;
    }

    /**
     * Return instance of specified class matching specified conditions
     *
     * @param parentClass Parent class that instances are listed from
     * @param cond restriction conditions
     * @return matching instance
     *
     * @throws NonExistingUriNodeException
     */
    public Item getInstance(String parentClass, Condition cond) throws NonExistingUriNodeException {
        return listInstance(parentClass, cond).get(0);
    }

    public Item getIndividual (String indvUri) {
        return new UriItem(indvUri, this);
    }

    /**
     * Return list of instances of specified class matching specified conditions
     *
     * @param parentClass Parent class that instances are listed from
     * @param cond restriction conditions
     * @return list of matching instances
     *
     * @throws NonExistingUriNodeException
     */
    public List<Item> listInstance(String parentClass, Condition cond) throws NonExistingUriNodeException {

        Resource parent = ontologyModel.getOntClass(parentClass);
        List<Item> instList;

        if (parent == null) {
            throw new NonExistingUriNodeException("Parent class with URI " + parentClass + " does not exist.");
        } else {
                                                                                                                                // TODO: USE WRAPPER
            StmtIterator condIterator = ontologyModel.listStatements(new PortalClassInstanceSelector(ontologyModel.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), (RDFNode) parent, cond));

            instList = new ArrayList<Item>();

            while (condIterator.hasNext()) {
                Statement res = condIterator.next();
                instList.add(new UriItem(res.getSubject().asResource().getURI(), this));
            }
        }
        return instList;
    }


    /**
     * List all available properties of specified Individual
     *
     * @param parentUri Individual URI
     * @return list of properties
     *
     * @throws NonExistingUriNodeException
     */
    public List<Item> listProperties(String parentUri) throws NonExistingUriNodeException {

        List<Item> result;

        Individual parent = ontologyModel.getIndividual(parentUri);

        if (parent == null) {
            throw new NonExistingUriNodeException("Object instance with URI " + parentUri + " does not exists.");
        } else {
            StmtIterator it = parent.listProperties();
            result = new ArrayList<Item>();

            while (it.hasNext()) {
                Property propItem = it.nextStatement().getPredicate();

                if (propItem.getURI().startsWith(W3_RDF_OBJECT)) {
                    continue;
                }

                UriItem ni = new UriItem(propItem.getURI(), this);

                if (!result.contains(ni)) {
                    result.add(ni);
                }
            }
        }
        return result;
    }

    /**
     * Get property value (Literal or URI) for specified literal and property
     *
     * @param parentUri target individual
     * @param property asked property
     *
     * @return asked property value
     *
     * @throws NonExistingUriNodeException
     * @throws ParseException
     */
    public Item getSubjectPropertyVal(String parentUri, String property) throws NonExistingUriNodeException, ParseException {

        Individual parent = ontologyModel.getIndividual(parentUri);
        OntProperty predicate = ontologyModel.getOntProperty(property);

        if (parent == null) {
            throw new NonExistingUriNodeException("Node with URI " + parentUri + " does not exists.");
        } else if (predicate == null){
            throw new NonExistingUriNodeException("Property with URI " + parentUri + " does not exists.");
        } else {
            RDFNode result = parent.getPropertyValue(predicate);

            if (result == null) { //no record
                return null;
            } else if (result.isLiteral()) { //literal record
                return new LiteralItem(result.asLiteral(), predicate, parent, this);
            }else { //URI
                return new UriItem(result.asResource().getURI(), this);
            }
        }
    }

    /**
     * Get list of all property values of specified individual and property
     *
     * @param parentUri target individual
     * @param propertyUri asked property
     * @return list of property values
     *
     * @throws NonExistingUriNodeException
     */
    public List<Item> listSubjectPropertyVal(String parentUri, String propertyUri) throws NonExistingUriNodeException, ParseException {

        List<Item> propValList;

        Individual parent = ontologyModel.getIndividual(parentUri);
        OntProperty predicate = ontologyModel.getOntProperty(propertyUri);

        if (parent == null) {
            throw new NonExistingUriNodeException("Node with URI " + parentUri + " does not exists.");
        } else if (predicate == null){
            throw new NonExistingUriNodeException("Property with URI " + parentUri + " does not exists.");
        } else {
            NodeIterator result = parent.listPropertyValues(predicate);

            propValList = new ArrayList<Item>();

            while (result.hasNext()) {
                RDFNode node = result.nextNode();

                if (node.isLiteral()) { //literal record
                    propValList.add(new LiteralItem(node.asLiteral(), predicate, parent, this));
                }else { //URI
                    propValList.add(new UriItem(node.asResource().getURI(), this));
                }
            }
        }
        return propValList;
    }

    /**
     * Add new instance to specified class
     *
     * @param parentClassUri Parent class that should be this instance of
     * @return created class as UriItem instance
     *
     * @throws NonExistingUriNodeException
     */
    public UriItem createClassInstance(String parentClassUri) throws NonExistingUriNodeException {

        OntClass parent  = ontologyModel.getOntClass(parentClassUri);

        if (parent == null) {
            throw new NonExistingUriNodeException("Class with URI " + parentClassUri + " does not exists.");
        } else {
            Individual newInd = ontologyModel.createIndividual(InstanceUriGen.generateInstanceUri(parentClassUri, this, defNamespace), ontologyModel.getOntClass(parentClassUri));

            return new UriItem(newInd.getURI(), this);
        }
    }

    /**
     * Add new class to model
     *
     * @param name Name of new class
     * @param parentClassUri Parent class name (optional)
     * @return
     */
    public UriItem createClass(String name, String parentClassUri) {

        OntClass oc = ontologyModel.createClass(name);

        if (parentClassUri != null) {

            oc.setSuperClass(ontologyModel.getOntClass(parentClassUri));
        }
        return new UriItem(oc.getURI(), this);
    }

    public String createProperty(String name, String domain, String range, String parentProperty) throws NonExistingUriNodeException {

        return createProperty(name, domain, range, parentProperty, null);
    }


    public String createProperty(String name, String domain, String range, String parentProperty, String description) throws NonExistingUriNodeException {

        Resource domainRes = ontologyModel.getResource(domain);

        if (domainRes == null) {
            throw new NonExistingUriNodeException("Property domain with URI " + domain + " does not exists.");
        }

        Resource rangeRes = ontologyModel.createResource(range);

        OntProperty prop = ontologyModel.createOntProperty(name);
        prop.setDomain(domainRes);
        prop.setRange(rangeRes);

        if (parentProperty != null) {
            prop.setSuperProperty(ontologyModel.getProperty(parentProperty));
        }
        
        if (description != null) {
            updatePropertyDescription(name, description);
        }

        return prop.getURI();
    }

    /**
     * List all model defined classes, that has no parent superClass
     *
     * @return list of defined classes
     */
    public List<String> listParentClasses() {

        List<String> parents = new ArrayList<String>();

        ExtendedIterator<OntClass> ex = ontologyModel.listClasses();

        OntClass oc = null;

        while(ex.hasNext()) {

            oc = ex.next();

            List<OntClass> nodeParList = oc.listSuperClasses().toList();

            if (!oc.getURI().contains("w3.org")) {
                if (nodeParList.isEmpty() ) {
                    parents.add(oc.getURI());
                }else if (nodeParList.size() == 1 && nodeParList.get(0).getLocalName().endsWith("Resource")){
                    parents.add(oc.getURI());
                }
            }
        }
        return parents;
    }

    /**
     * List all defined classes that hes specified class as superClass parent
     *
     * @param parentURI Parent Class URI
     * @return list of defined classes
     *
     * @throws NonExistingUriNodeException
     */
    public List<String> listSubClasses(String parentURI) throws NonExistingUriNodeException {
    
        List<String> childClasses = new ArrayList<String>();

        OntClass parent = ontologyModel.getOntClass(parentURI);

        if (parent == null) {
            throw new NonExistingUriNodeException("Class with URI " + parentURI + " does not exists.");
        }

        ExtendedIterator<OntClass> ex = parent.listSubClasses(true);

        OntClass oc = null;

        while(ex.hasNext()) {

            oc = ex.next();

            childClasses.add(oc.getURI());
        }
        return childClasses;

    }

    public String getIndividualParentClass(String uri) {

        return ontologyModel.getIndividual(uri).getRDFType().toString();

    }

    public boolean hasSubClasses(String parentURI) throws NonExistingUriNodeException {

        OntClass parent = ontologyModel.getOntClass(parentURI);

        if (parent == null) {
            throw new NonExistingUriNodeException("Class with URI " + parentURI + " does not exists.");
        }

        return parent.hasSubClass();
    }

    public String getClassDescription(String classUri) {
        
        OntClass oc = ontologyModel.getOntClass(classUri);

        if (oc == null) {
            return "";
        }else {
            return oc.getComment(null);
        }
    }

    public String getPropertyDescription(String property) {

        OntProperty op = ontologyModel.getOntProperty(property);

        if (op == null) {
            return "";
        }else {
            return op.getComment(null);
        }
    }


    public void updateClassDescription(String classUri, String value) {

        OntClass oc = ontologyModel.getOntClass(classUri);

        if (oc == null) {
         return;
        }
        oc.setComment(value, null);
    }

    public boolean testTableForClassExists(String classUri) {
        String tblName = tblPrefix + classUri.replace(defNamespace, "");

        try {
            relDbStatement.executeQuery("SELECT URI FROM " + tblName);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void getIndividualDataFile(String individualUri, File target) throws SQLException, FileNotFoundException, IOException {

        String parentClass = getIndividualParentClass(individualUri);

        if (parentClass == null) {
            return;
        }

        String tblName = tblPrefix + parentClass.replace(defNamespace, "");


        String sql_stmnt = "SELECT " + TABLE_DATA_COLUMN + " FROM " + tblName + " WHERE " + TABLE_URI_COLUMN + " = ?";
        PreparedStatement prepStmnt = relDbStatement.getConnection().prepareStatement(sql_stmnt);

        prepStmnt.setString(1, individualUri);

        ResultSet resData =  prepStmnt.executeQuery();
        resData.next();

        byte [] storedData = resData.getBytes(TABLE_DATA_COLUMN);

        OutputStream os = new FileOutputStream(target);

        os.write(storedData);
        os.close();
    }

    public void uploadIndividualDataFile(String individualUri, File target) throws SQLException, FileNotFoundException, IOException {

        String parentClass = getIndividualParentClass(individualUri);

        if (parentClass == null) {
            return;
        }

        String tblName = tblPrefix + parentClass.replace(defNamespace, "");


        String sql_stmnt = "INSERT INTO " + tblName + " VALUES(?,?)";
        PreparedStatement prepStmnt = relDbStatement.getConnection().prepareStatement(sql_stmnt);

        FileInputStream dataStream = new FileInputStream(target);

        prepStmnt.setString(1, individualUri);
        prepStmnt.setBinaryStream(2, dataStream, (int) target.length());


        int res = prepStmnt.executeUpdate();
        //TODO result?
        dataStream.close();
    }

    public void updateIndividualDataFile(String individualUri, File target) throws SQLException, FileNotFoundException, IOException {

        String parentClass = getIndividualParentClass(individualUri);

        if (parentClass == null) {
            return;
        }

        String tblName = tblPrefix + parentClass.replace(defNamespace, "");


        String sql_stmnt = "UPDATE " + tblName + " SET " + TABLE_DATA_COLUMN + " = ? WHERE " + TABLE_URI_COLUMN + " = ?";
        PreparedStatement prepStmnt = relDbStatement.getConnection().prepareStatement(sql_stmnt);

        FileInputStream dataStream = new FileInputStream(target);

        prepStmnt.setBinaryStream(1, dataStream, (int) target.length());
        prepStmnt.setString(2, individualUri);


        int res = prepStmnt.executeUpdate();
        //TODO result?
        dataStream.close();
    }

    public boolean hasIndividualTable(String individualUri) {

        String parentClass = getIndividualParentClass(individualUri);

        return testTableForClassExists(parentClass);
    }

    public boolean hasIndividualFile(String individualUri) throws SQLException {

        String parentClass = getIndividualParentClass(individualUri);

        if (parentClass == null) {
            return false;
        }

        String tblName = tblPrefix + parentClass.replace(defNamespace, "");

        if (!testTableForClassExists(parentClass)) {
            return false;
        }


        String sql_stmnt = "SELECT " + TABLE_URI_COLUMN + " FROM " + tblName + " WHERE " + TABLE_URI_COLUMN + " = ?";
        PreparedStatement prepStmnt = relDbStatement.getConnection().prepareStatement(sql_stmnt);

        prepStmnt.setString(1, individualUri);

        ResultSet resData =  prepStmnt.executeQuery();
        return resData.next();
    }

    public void updatePropertyDescription(String propertyUri, String value) {

        OntProperty op = ontologyModel.getOntProperty(propertyUri);

        if (op == null) {
         return;
        }
        op.setComment(value, null);
    }

    /**
     * List all model defined properties that has no superProperties
     * @return
     */
    public List<String> listPropertiesByDomain(String domainUri) {

        List<String> parents = new ArrayList<String>();

        ExtendedIterator<OntProperty> ex = ontologyModel.listOntProperties();

        OntProperty op;

        while(ex.hasNext()) {

            op = ex.next();

            List nodeParList = op.listSuperProperties().toList();

            if (!op.getURI().contains("w3.org") && op.getDomain() != null) {

                if (op.getDomain().getURI().equals(domainUri)) {
                    
//                    ExtendedIterator<? extends OntProperty> it = op.listSuperProperties(); //first property is Property (RDF definition)
//
//                    while(it.hasNext()) {
//                        System.out.println(it.next().getURI());
//                    }
//
//                    if (!it.hasNext()) {
                        parents.add(op.getURI());
                    //}
                }
            //}else if (nodeParList.size() == 0) { //TODO - avoid RDF statements??
            //    parents.add(op.getURI());
            }
        }
        return parents;
    }

    public boolean hasSubProperties(String parentURI) throws NonExistingUriNodeException {

        OntProperty parent = ontologyModel.getOntProperty(parentURI);

        if (parent == null) {
            throw new NonExistingUriNodeException("Property with URI " + parentURI + " does not exists.");
        }
        return parent.listSubProperties().hasNext();
    }

/**
     * List all direct subproperties of specified property
     *
     * @param parentURI root property
     * @return list of subproperties
     *
     * @throws NonExistingUriNodeException
     */
    public List<String> listSubProperties(String parentURI) throws NonExistingUriNodeException {

        List<String> childProperties = new ArrayList<String>();

        OntProperty parent = ontologyModel.getOntProperty(parentURI);

        if (parent == null) {
            throw new NonExistingUriNodeException("Property with URI " + parentURI + " does not exists.");
        }

        ExtendedIterator<? extends OntProperty> ex = parent.listSubProperties(true);

        OntProperty op = null;

        while(ex.hasNext()) {
            op = ex.next();
            childProperties.add(op.getURI());
        }
        return childProperties;
    }

    public DataType getPropertyRange(String propUri) {

        OntProperty op = ontologyModel.getOntProperty(propUri);

        if (op != null) {
            OntResource range = op.getRange();
            
            if (range != null) {
                return DataConverter.getTypeByUri(range.getURI());
            }
        }
        return null;
    }

    public String getPropertyRangeUri(String property) {
     OntProperty op = ontologyModel.getOntProperty(property);

        if (op != null) {
            OntResource range = op.getRange();

            if (range != null) {
                return range.getURI();
            }
        }
        return null;

    }

    /**
     * Returns a list of all classes and supported RDF DataTypes that can be used as property range
     * 
     * @return range list
     */
    public List<String> listAvailableRanges() {

        List<String> ranges = new ArrayList<String>();

        ExtendedIterator<OntClass> ex = ontologyModel.listClasses();
        OntClass oc = null;

        while(ex.hasNext()) {

            oc = ex.next();
            if (!oc.getURI().contains("w3.org")) {
                ranges.add(oc.getURI());
            }
        }

        ranges.add(XSDDatatype.XSDboolean.getURI());
        ranges.add(XSDDatatype.XSDdateTime.getURI());
        ranges.add(XSDDatatype.XSDdate.getURI());
        ranges.add(XSDDatatype.XSDtime.getURI());
        ranges.add(XSDDatatype.XSDfloat.getURI());
        ranges.add(XSDDatatype.XSDdouble.getURI());
        ranges.add(XSDDatatype.XSDlong.getURI());
        ranges.add(XSDDatatype.XSDinteger.getURI());
        ranges.add(XSDDatatype.XSDstring.getURI());

        return ranges;
    }



   /**
    * Return property by its URI string value
    * @param uri
    * @return
    * @throws NonExistingUriNodeException
    */
   public Property getPropertyByUri(String uri) throws NonExistingUriNodeException {

       Property prop = ontologyModel.getProperty(uri);

       if (prop == null) {
            throw new NonExistingUriNodeException("Node with URI " + uri + " does not exists.");
       } else {
           return prop;
       }
   }

   /**
    * Returns JENA ontology model
    * @return
    */
    public OntModel getOntModel() {
        return ontologyModel;
    }

    public String getNamespace() {
        return defNamespace;
    }

    public void exportModel(File targetFile) throws FileNotFoundException {

        ontologyModel.write(new FileOutputStream(targetFile));
    }

}