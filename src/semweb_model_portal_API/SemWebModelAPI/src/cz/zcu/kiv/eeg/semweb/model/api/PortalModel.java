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
import org.apache.log4j.Logger;

/**
 *  Main class of EEG/ERP portal semantic web model API
 *  All communication with semantic database provided via this class
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PortalModel {

    private DbConnector dbConn; //database connection
    private Model basicModel; //basic semantic web model (nonOntological)
    private OntModel ontologyModel; //Jena ontology model
    private java.sql.Statement relDbStatement; //realational database connection
    private String defNamespace; //default namespace for model
    private String tblPrefix; //deafult table prefix - all model defined tables begins with this prefix
    private OntModelSpec reasoner; //Model reasoner type
    private static final String W3_RDF_OBJECT = "http://www.w3.org"; //identification key for RDF definitions
    private static final String RDF_CLASS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"; //RDF class URI
    private static final String TABLE_URI_COLUMN = "URI"; //Table ID column
    private static final String TABLE_DATA_COLUMN = "DATA"; //Table dataFile column
    private static final Logger logger = Logger.getLogger(PortalModel.class);

    /**
     * Create new model API connector
     *
     * @param connector Database connection
     * @param namespace Default model namespace
     * @param tblPrefix Default model table prefix
     * @param reasoner Selected reasoner for ontology model
     */
    public PortalModel(DbConnector connector, String namespace, String tblPrefix, OntModelSpec reasoner) {
        this.dbConn = connector;
        this.defNamespace = namespace;
        this.tblPrefix = tblPrefix;
        this.reasoner = reasoner;
    }

    /**
     * Connect to database model (semWeb database and relational database too)
     *
     * @return true if connection was successfull
     */
    public boolean connect() {
        logger.info("Connecting database");
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
        logger.info("Closing model");
        ontologyModel.close();
    }

    /**
     * Return instance of specified class (return random instance if more present
     * 
     * @param parentClass Parent class URI
     * @return found instance item or NULL if no mach
     */
    public Item geClasstInstance(String parentClass, boolean direct) throws NonExistingUriNodeException {
        logger.info("Searching instace of class " + parentClass);
        return listClassInstance(parentClass, direct).get(0);
    }

    /**
     * Return list of specified class instances (all of them)
     * @param parentClass
     * @param direct true if only direct class instances should be listed (no subclasses)
     * @return
     */
    public List<Item> listClassInstance(String parentClass, boolean direct) throws NonExistingUriNodeException {

        logger.info("Listing instance of class " + parentClass);
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
    public Item getClassInstance(String parentClass, Condition cond) throws NonExistingUriNodeException {
        logger.info("Get first instance of class " + parentClass + " with set filter");
        return listClassInstances(parentClass, cond).get(0);
    }

    /**
     * Get individual by its URI
     *
     * @param indvUri URI of individual
     * @return Item wrapping target individual
     */
    public Item getIndividualByUri(String indvUri) {
        logger.info("Get individual by URI " + indvUri);
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
    public List<Item> listClassInstances(String parentClass, Condition cond) throws NonExistingUriNodeException {
        logger.info("Listing instance of class " + parentClass + " with set filter");
        Resource parent = ontologyModel.getOntClass(parentClass);
        List<Item> instList;

        if (parent == null) {
            throw new NonExistingUriNodeException("Parent class with URI " + parentClass + " does not exist.");
        } else {

            StmtIterator condIterator = ontologyModel.listStatements(new PortalClassInstanceSelector(ontologyModel.getProperty(RDF_CLASS), (RDFNode) parent, cond));

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
    public List<Item> listIndividualProperties(String parentUri) throws NonExistingUriNodeException {
        logger.info("Listing properties of individual " + parentUri);
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
        logger.info("Get value of individuals (" + parentUri + ") property (" + property + ")");
        Individual parent = ontologyModel.getIndividual(parentUri);
        OntProperty predicate = ontologyModel.getOntProperty(property);

        if (parent == null) {
            throw new NonExistingUriNodeException("Node with URI " + parentUri + " does not exists.");
        } else if (predicate == null) {
            throw new NonExistingUriNodeException("Property with URI " + parentUri + " does not exists.");
        } else {
            RDFNode result = parent.getPropertyValue(predicate);

            if (result == null) { //no record
                return null;
            } else if (result.isLiteral()) { //literal record
                return new LiteralItem(result.asLiteral(), predicate, parent, this);
            } else { //URI
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
        logger.info("Listing all values of individuals " + parentUri + " property " + propertyUri);
        List<Item> propValList;

        Individual parent = ontologyModel.getIndividual(parentUri);
        OntProperty predicate = ontologyModel.getOntProperty(propertyUri);

        if (parent == null) {
            throw new NonExistingUriNodeException("Node with URI " + parentUri + " does not exists.");
        } else if (predicate == null) {
            throw new NonExistingUriNodeException("Property with URI " + parentUri + " does not exists.");
        } else {
            NodeIterator result = parent.listPropertyValues(predicate);

            propValList = new ArrayList<Item>();

            while (result.hasNext()) {
                RDFNode node = result.nextNode();

                if (node.isLiteral()) { //literal record
                    propValList.add(new LiteralItem(node.asLiteral(), predicate, parent, this));
                } else { //URI
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
        logger.info("Creating new instance of class " + parentClassUri);
        OntClass parent = ontologyModel.getOntClass(parentClassUri);

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
        logger.info("Creating new instance of class " + parentClassUri + " inherited from " + parentClassUri);
        OntClass oc = ontologyModel.createClass(name);

        if (parentClassUri != null) {

            oc.setSuperClass(ontologyModel.getOntClass(parentClassUri));
        }
        return new UriItem(oc.getURI(), this);
    }

    /**
     * Create new property in model
     *
     * @param name Property name
     * @param domain Property domain
     * @param range Property range
     * @param parentProperty Parent property URI (optional)
     * @return created property URI
     * @throws NonExistingUriNodeException
     */
    public String createProperty(String name, String domain, String range, String parentProperty) throws NonExistingUriNodeException {
        return createProperty(name, domain, range, parentProperty, null);
    }

    /**
     * Create new model property
     *
     * @param name Property name
     * @param domain Property domain
     * @param range Property range
     * @param parentProperty Parent property URI (optional)
     * @param description Property description (optional)
     * @return Created property URI
     * @throws NonExistingUriNodeException
     */
    public String createProperty(String name, String domain, String range, String parentProperty, String description) throws NonExistingUriNodeException {
        logger.info("Creating new property " + name);
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
        logger.info("Listing model NON-inherited classes");
        List<String> parents = new ArrayList<String>();

        ExtendedIterator<OntClass> ex = ontologyModel.listClasses();

        OntClass oc = null;

        while (ex.hasNext()) {

            oc = ex.next();

            List<OntClass> nodeParList = oc.listSuperClasses().toList();

            if (!oc.getURI().contains(W3_RDF_OBJECT)) {
                if (nodeParList.isEmpty()) {
                    parents.add(oc.getURI());
                } else if (nodeParList.size() == 1 && nodeParList.get(0).getLocalName().endsWith("Resource")) {
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
        logger.info("Listing subclasses of " + parentURI);
        List<String> childClasses = new ArrayList<String>();

        OntClass parent = ontologyModel.getOntClass(parentURI);

        if (parent == null) {
            throw new NonExistingUriNodeException("Class with URI " + parentURI + " does not exists.");
        }

        ExtendedIterator<OntClass> ex = parent.listSubClasses(true);

        OntClass oc = null;

        while (ex.hasNext()) {

            oc = ex.next();

            childClasses.add(oc.getURI());
        }
        return childClasses;

    }

    /**
     * Get individual parent class URI
     * @param uri individual URI
     * @return parent class individual URI
     */
    public String getIndividualParentClass(String uri) {
        logger.info("Get parent class of " + uri);
        return ontologyModel.getIndividual(uri).getRDFType().toString();
    }

    /**
     * Test if specified class has subclasses
     *
     * @param parentURI Parent class
     * @return true if class has subclasses
     *
     * @throws NonExistingUriNodeException
     */
    public boolean hasSubClasses(String parentURI) throws NonExistingUriNodeException {
        logger.info("Searching subclasses of " + parentURI);
        OntClass parent = ontologyModel.getOntClass(parentURI);

        if (parent == null) {
            throw new NonExistingUriNodeException("Class with URI " + parentURI + " does not exists.");
        }

        return parent.hasSubClass();
    }

    /**
     * Get class description note
     *
     * @param classUri Target class
     * @return Description
     */
    public String getClassDescription(String classUri) {
        logger.info("Get description of class " + classUri);
        OntClass oc = ontologyModel.getOntClass(classUri);

        if (oc == null) {
            return "";
        } else {
            return oc.getComment(null);
        }
    }

    /**
     * Get property description
     *
     * @param property Target description
     * @return description
     */
    public String getPropertyDescription(String property) {
        logger.info("Get description of " + property);
        OntProperty op = ontologyModel.getOntProperty(property);

        if (op == null) {
            return "";
        } else {
            return op.getComment(null);
        }
    }

    /**
     * Update class description
     *
     * @param classUri Target class
     * @param value Description
     */
    public void updateClassDescription(String classUri, String value) {
        logger.info("Updating description of class " + classUri);
        OntClass oc = ontologyModel.getOntClass(classUri);

        if (oc == null) {
            return;
        }
        oc.setComment(value, null);
    }

    /**
     * Test if specified class has bounded table in relational database
     * (matching by same localName with default tablePrefix)
     *
     * @param classUri Source class
     *
     * @return true if table exists
     */
    public boolean testTableForClassExists(String classUri) {
        logger.info("Searching table of class " + classUri);
        String tblName = tblPrefix + classUri.replace(defNamespace, "");

        try {
            relDbStatement.executeQuery("SELECT URI FROM " + tblName);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Get data file from table specified Individual by URI
     *
     * @param individualUri Target individual URI
     * @param target target file to store data
     *
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void getIndividualDataFile(String individualUri, File target) throws SQLException, FileNotFoundException, IOException {
        logger.info("Loading data file of individual " + individualUri);
        String parentClass = getIndividualParentClass(individualUri);

        if (parentClass == null) {
            return;
        }

        String tblName = tblPrefix + parentClass.replace(defNamespace, "");

        String sql_stmnt = "SELECT " + TABLE_DATA_COLUMN + " FROM " + tblName + " WHERE " + TABLE_URI_COLUMN + " = ?";
        PreparedStatement prepStmnt = relDbStatement.getConnection().prepareStatement(sql_stmnt);

        prepStmnt.setString(1, individualUri);

        ResultSet resData = prepStmnt.executeQuery();
        resData.next();

        byte[] storedData = resData.getBytes(TABLE_DATA_COLUMN);

        OutputStream os = new FileOutputStream(target);

        os.write(storedData);
        os.close();
    }

    /**
     * Upload data file to specified individual
     * @param individualUri Individual URI
     * @param target targe file
     *
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean uploadIndividualDataFile(String individualUri, File target) throws SQLException, FileNotFoundException, IOException {
        logger.info("Uploading data file for individual " + individualUri);
        String parentClass = getIndividualParentClass(individualUri);

        if (parentClass == null) {
            return false;
        }

        String tblName = tblPrefix + parentClass.replace(defNamespace, "");


        String sql_stmnt = "INSERT INTO " + tblName + " VALUES(?,?)";
        PreparedStatement prepStmnt = relDbStatement.getConnection().prepareStatement(sql_stmnt);

        FileInputStream dataStream = new FileInputStream(target);

        prepStmnt.setString(1, individualUri);
        prepStmnt.setBinaryStream(2, dataStream, (int) target.length());


        prepStmnt.executeUpdate();
        dataStream.close();
        return true;
    }

    /**
     * Update data file of specified individual
     * @param individualUri Individual URI
     * @param target target file to upload
     *
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean updateIndividualDataFile(String individualUri, File target) throws SQLException, FileNotFoundException, IOException {
        logger.info("Updating data file for individual " + individualUri);
        String parentClass = getIndividualParentClass(individualUri);

        if (parentClass == null) {
            return false;
        }

        String tblName = tblPrefix + parentClass.replace(defNamespace, "");


        String sql_stmnt = "UPDATE " + tblName + " SET " + TABLE_DATA_COLUMN + " = ? WHERE " + TABLE_URI_COLUMN + " = ?";
        PreparedStatement prepStmnt = relDbStatement.getConnection().prepareStatement(sql_stmnt);

        FileInputStream dataStream = new FileInputStream(target);

        prepStmnt.setBinaryStream(1, dataStream, (int) target.length());
        prepStmnt.setString(2, individualUri);


        prepStmnt.executeUpdate();
        dataStream.close();
        return true;
    }

    /**
     * Test if Individual has relational table
     *
     * @param individualUri Target individual
     * @return true if table exists
     */
    public boolean hasIndividualTable(String individualUri) {
        logger.info("Searching table for individual " + individualUri);
        String parentClass = getIndividualParentClass(individualUri);

        return testTableForClassExists(parentClass);
    }

    /**
     * Test if individual has DataFile record in own relational table
     *
     * @param individualUri Target individual
     * @return true if file has some file
     * @throws SQLException
     */
    public boolean hasIndividualFile(String individualUri) throws SQLException {
        logger.info("Test table file record existence for individual " + individualUri);
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

        ResultSet resData = prepStmnt.executeQuery();
        return resData.next();
    }

    /**
     * Update description of proerty
     *
     * @param propertyUri Target property
     * @param value Description
     */
    public void updatePropertyDescription(String propertyUri, String value) {
        logger.info("Updating description of property " + propertyUri);
        OntProperty op = ontologyModel.getOntProperty(propertyUri);

        if (op == null) {
            return;
        }
        op.setComment(value, null);
    }

    /**
     * List all model defined properties that has specified domain
     *
     * @return list of properties
     */
    public List<String> listPropertiesByDomain(String domainUri) {
        logger.info("Listing properties with domain " + domainUri);
        List<String> parents = new ArrayList<String>();

        ExtendedIterator<OntProperty> ex = ontologyModel.listOntProperties();

        OntProperty op;

        while (ex.hasNext()) {

            op = ex.next();

            if (!op.getURI().contains("w3.org") && op.getDomain() != null) {

                if (op.getDomain().getURI().equals(domainUri)) {

                    parents.add(op.getURI());
                }
            }
        }
        return parents;
    }

    /**
     * List all model defined properties that has no superProperties
     * @return list of properties
     */
    public List<String> listPropertiesByRange(String rangeUri) {
        logger.info("Listing properties with range " + rangeUri);
        List<String> parents = new ArrayList<String>();

        ExtendedIterator<OntProperty> ex = ontologyModel.listOntProperties();

        OntProperty op;

        while (ex.hasNext()) {

            op = ex.next();

            if (!op.getURI().contains("w3.org") && op.getDomain() != null) {

                if (op.getRange().getURI().equals(rangeUri)) {

                    parents.add(op.getURI());
                }
            }
        }
        return parents;
    }

    /**
     * Test if specified property has subProperties
     *
     * @param parentURI source property
     * @return true if any subproperties exists
     *
     * @throws NonExistingUriNodeException
     */
    public boolean hasSubProperties(String parentURI) throws NonExistingUriNodeException {
        logger.info("Test existence of subproperties of " + parentURI);
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
        logger.info("Listing subProperties of " + parentURI);
        List<String> childProperties = new ArrayList<String>();

        OntProperty parent = ontologyModel.getOntProperty(parentURI);

        if (parent == null) {
            throw new NonExistingUriNodeException("Property with URI " + parentURI + " does not exists.");
        }

        ExtendedIterator<? extends OntProperty> ex = parent.listSubProperties(true);

        OntProperty op = null;

        while (ex.hasNext()) {
            op = ex.next();
            childProperties.add(op.getURI());
        }
        return childProperties;
    }

    /**
     * Get range of property as DataType
     * @param propUri Target property
     * @return
     */
    public DataType getPropertyRange(String propUri) {
        logger.info("Getting range of property " + propUri);
        OntProperty op = ontologyModel.getOntProperty(propUri);

        if (op != null) {
            OntResource range = op.getRange();

            if (range != null) {
                return DataConverter.getTypeByUri(range.getURI());
            }
        }
        return null;
    }

    /**
     * Get property rage as URI
     * @param property
     * @return
     */
    public String getPropertyRangeUri(String property) {
        logger.info("Getting range of property " + property);
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
        logger.info("Listing model available data ranges");
        List<String> ranges = new ArrayList<String>();

        ExtendedIterator<OntClass> ex = ontologyModel.listClasses();
        OntClass oc = null;

        while (ex.hasNext()) {

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
     * Remove specified property from model - it removes all bounded property values
     * 
     * @param uri Property URI
     */
    public void removeProperty(String uri) {
        logger.info("Removing property " + uri);

        OntProperty op = ontologyModel.getOntProperty(uri);

        if (op != null) {
            List<Resource> resources = ontologyModel.listSubjectsWithProperty(op).toList();

            for (Resource item : resources) {
                item.removeAll(op);
            }
            op.remove();
        }
    }

    /**
     * Remove specified individual from model - it removes all bounded property values
     *
     * @param uri Individual URI
     */
    public void removeIndividual(String uri) {
        logger.info("Removing individual " + uri);

        Individual ind = ontologyModel.getIndividual(uri);

        if (ind != null) {
            ind.remove();
        }
    }

    /**
     * Remove specified class from model - it removes all bounded individual values
     * and properties with target range
     *
     * @param uri Class URI
     */
    public void removeClass(String uri) {
        logger.info("Removing class " + uri);

        OntClass oc = ontologyModel.getOntClass(uri);

        if (oc != null) {
            List<? extends OntResource> indvs = oc.listInstances().toList();

            for (OntResource or : indvs) {
                or.remove();
            }

            List<String> rangeProps = listPropertiesByRange(uri);

            for (String prop : rangeProps) {
                removeProperty(prop);
            }
            oc.remove();
        }
    }

    /**
     * Return property by its URI string value
     * @param uri
     * @return
     * @throws NonExistingUriNodeException
     */
    public Property getPropertyByUri(String uri) throws NonExistingUriNodeException {
        logger.info("Get propety " + uri);
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
        logger.info("Exporting model");
        ontologyModel.write(new FileOutputStream(targetFile));
    }
}
