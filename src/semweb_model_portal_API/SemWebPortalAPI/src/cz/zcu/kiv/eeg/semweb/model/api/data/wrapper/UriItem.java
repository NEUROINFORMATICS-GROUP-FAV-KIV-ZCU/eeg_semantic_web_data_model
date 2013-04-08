package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import java.text.ParseException;
import java.util.List;

/**
 * Wrapper for model URI node
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class UriItem extends Item {

    private PortalModel model;
    private String uri; //item URI

    public UriItem(String uri, PortalModel model) {
        this.uri = uri;
        this.model = model;
    }

    @Override
    public boolean isUri() {
        return true;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    /**
     * Get item URI
     * @return
     */
    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        return ((UriItem) o).getUri().equals(uri);
    }

    /**
     * List available(by triples containig this Uri as Object) properties of this URI node
     * @return
     */
    public List<Item> listProperties() throws NonExistingUriNodeException {
        return model.listIndividualProperties(getUri());
    }

    /**
     * Get value by triple specified as this Uri as object and property as predicate
     *
     * @param property
     * @return
     */
    public Item getPropertyVal(UriItem property) throws NonExistingUriNodeException, ParseException {
        return model.getSubjectPropertyVal(this.getUri(), property.getUri());
    }

    /**
     * Get value by triple specified as this Uri as object and property as predicate
     * @param propertyUri
     * @return
     */
    public Item getPropertyVal(String propertyUri) throws NonExistingUriNodeException, ParseException {
        return model.getSubjectPropertyVal(this.getUri(), propertyUri);
    }

    /**
     * List values (Uri or Literal) of objects of properties chained with this URI
     *
     * @param property
     * @return
     */
    public List<Item> listPropertyVal(UriItem property) throws NonExistingUriNodeException, ParseException {
        return model.listSubjectPropertyVal(this.getUri(), property.getUri());
    }

    public void addPropertyValue(String propertyUri, Object value) throws NonExistingUriNodeException {

        Individual indv = model.getOntModel().getIndividual(uri);
        OntProperty prop = model.getOntModel().getOntProperty(propertyUri);

        RDFNode targetObject;

        if (model.getOntModel().getOntProperty(propertyUri).getRange().getURI().startsWith(XSDDatatype.XSDstring.XSD)) { //literal

            targetObject = model.getOntModel().createTypedLiteral(value, prop.getRange().getURI());
        } else {

            targetObject = model.getOntModel().getIndividual(value.toString());
        }
        model.getOntModel().add(indv, prop, targetObject);
    }

    /**
     * Update value of specified property
     *
     * @param propertyUri Property uri
     * @param recentValue oldValue
     * @param newValue new Value
     */
    public void updatePropertyValue(String propertyUri, String recentValue, String newValue) {

        Resource subject = model.getOntModel().getResource(uri);

        Property predicate = model.getOntModel().getProperty(propertyUri);
        Resource oldObject = model.getOntModel().getResource(recentValue);
        Resource newObject = model.getOntModel().getResource(newValue);

        if (predicate == null || oldObject == null || newObject == null) {
            return;
        }

        Statement st = model.getOntModel().listStatements(subject, predicate, oldObject).nextStatement();

        if (st != null) {
            model.getOntModel().remove(st);

            st = model.getOntModel().createStatement(subject, predicate, newObject);
            model.getOntModel().add(st);
        }
    }

    /**
     * Return item as property model object
     * @return
     */
    public Property asProperty() {
        return model.getOntModel().getProperty(uri);
    }
}
