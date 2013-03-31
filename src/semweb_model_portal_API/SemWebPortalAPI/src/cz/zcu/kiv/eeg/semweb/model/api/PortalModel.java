package cz.zcu.kiv.eeg.semweb.model.api;

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
import cz.zcu.kiv.eeg.semweb.model.api.utils.InstanceUriGen;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.search.Condition;
import cz.zcu.kiv.eeg.semweb.model.search.PortalClassInstanceSelector;
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

    private String defNamespace; //default namespace for EEG/ERP
    private String tblPrefix;
    private OntModelSpec reasoner;

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
                result.add(new UriItem(propItem.getURI(), this));
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

        while(ex.hasNext()) {

            OntClass oc = ex.next();

            List<OntClass> nodeParList = oc.listSuperClasses().toList();

            if (nodeParList.isEmpty()) {
                parents.add(oc.getURI());
            }else if (nodeParList.size() == 1 && nodeParList.get(0).getLocalName().endsWith("Resource")){
                parents.add(oc.getURI());
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
        
        while(ex.hasNext()) {

            OntClass oc = ex.next();
            
            childClasses.add(oc.getURI());
        }
        return childClasses;
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


    public void updateClassDescription(String classUri, String value) {

        OntClass oc = ontologyModel.getOntClass(classUri);

        if (oc == null) {
         return;
        }
        oc.setComment(value, null);
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

}
