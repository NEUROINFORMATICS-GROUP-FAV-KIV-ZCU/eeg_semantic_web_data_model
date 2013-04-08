package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import cz.zcu.kiv.eeg.semweb.model.api.utils.DataConverter;
import java.text.ParseException;

/**
 * Filter condition - specified property value must equals specfied value
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PropertyValEqCondition extends Condition {

    private UriItem property;
    private Object resource;

    public PropertyValEqCondition(UriItem predicate, Object object) {
        this.property = predicate;
        this.resource = object;
    }

    public String getPredicate() {
        return property.getUri();
    }

    public String getObject() {
        return resource.toString();
    }

    @Override
    public boolean getResult(Property predicate, Resource object) {

        if (object.isResource()) {
            StmtIterator it = object.asResource().listProperties(property.asProperty());

            while (it.hasNext()) {
                RDFNode objectNode = it.next().getObject();

                if (objectNode.isLiteral()) {
                    try {
                        return DataConverter.convertObject(objectNode.asLiteral()).toString().equals(resource.toString());
                    } catch (ParseException ex) {
                        return false;
                    }
                } else { //object is URI
                    return objectNode.asResource().getURI().equals(resource.toString());
                }

            }
            return false;

        } else {
            return false;
        }
    }
}
