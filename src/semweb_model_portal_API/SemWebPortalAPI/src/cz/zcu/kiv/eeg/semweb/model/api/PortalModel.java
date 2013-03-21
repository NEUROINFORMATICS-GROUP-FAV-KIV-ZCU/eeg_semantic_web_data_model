package cz.zcu.kiv.eeg.semweb.model.api;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
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
import cz.zcu.kiv.eeg.semweb.model.search.Condition;
import cz.zcu.kiv.eeg.semweb.model.search.PortalClassInstanceSelector;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import oracle.spatial.rdf.client.jena.ModelOracleSem;

/**
 *  Main class of EEG/ERP portal semantic web model API
 *  All communication with database provided via this class
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PortalModel {

    private ModelOracleSem basicModel; //Oracle semWeb model (basic)
    private OntModel ontologyModel; //Jena ontology model
    private String defNamespace; //default namespace for EEG/ERP


    public PortalModel(ModelOracleSem model, String namespace) {
        this.basicModel = model;
        this.ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF, model); //create Ontology model based on Oracle semWeb model
        this.defNamespace = namespace;
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
                    instList.add(new UriItem(res.getNameSpace(), res.getLocalName(), this));
                }
            }
        }
        return instList;
    }

    /**
     * Return list of specified class instances matching specified conditions
     * 
     */
    public Item getInstance(String parentClass, Condition cond) throws NonExistingUriNodeException {
        return listInstance(parentClass, cond).get(0);
    }

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
                instList.add(new UriItem(res.getSubject().asResource().getNameSpace(), res.getSubject().asResource().getLocalName(), this));
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
                result.add(new UriItem(propItem.getNameSpace(), propItem.getLocalName(), this));
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
                return new UriItem(result.asResource().getNameSpace(), result.asResource().getLocalName(), this);
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
                    propValList.add(new UriItem(node.asResource().getNameSpace(), node.asResource().getLocalName(), this));
                }
            }
        }
        return propValList;
    }

   public Property getPropertyByUri(String uri) throws NonExistingUriNodeException {

       Property prop = ontologyModel.getProperty(uri);

       if (prop == null) {
            throw new NonExistingUriNodeException("Node with URI " + uri + " does not exists.");
       } else {
           return prop;
       }
   }

    public OntModel getOntModel() {
        return ontologyModel;
    }
   

}
