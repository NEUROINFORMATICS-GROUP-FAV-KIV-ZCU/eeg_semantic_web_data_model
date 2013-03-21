package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.RDFNode;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import java.text.ParseException;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class UriItem extends Item {

    private PortalModel model;

    private String namespace;
    private String localName;

    public UriItem(String namespace, String value, PortalModel model) {
        this.namespace = namespace;
        this.localName = value;
        this.model = model;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String value) {
        this.localName = value;
    }

    @Override
    public boolean isUri() {
        return true;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    public String getUri() {
        return namespace + localName;
    }

    @Override
    public String toString() {
        return namespace + localName;
    }

    /**
     * List available(by triples containig this Uri as Object) properties of this URI node
     * @return
     */
    public List<Item> listProperties() throws NonExistingUriNodeException {
        return model.listProperties(getUri());
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

    public void addPropertyValue (String propertyUri, Object value) throws NonExistingUriNodeException {

        Individual indv = model.getOntModel().getIndividual(namespace + localName);
        OntProperty prop = model.getOntModel().getOntProperty(propertyUri);

        RDFNode targetObject;

        if (model.getOntModel().getOntProperty(propertyUri).getRange().getURI().startsWith(XSDDatatype.XSDstring.XSD)) { //literal
            targetObject = model.getOntModel().createTypedLiteral(value, prop.getRange().getURI());

        }else {

            targetObject = model.getOntModel().getIndividual(value.toString());
        }
            model.getOntModel().add(indv, prop, targetObject);
    }

}
