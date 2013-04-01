package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class HasPropertyCondition extends Condition {

    private UriItem property;

    public HasPropertyCondition(UriItem predicate) {
        this.property = predicate;
    }

    public String getPredicate() {
        return property.getUri();
    }


    @Override
    public boolean getResult(Property predicate, Resource object) {

        StmtIterator it = object.listProperties();

        while (it.hasNext()) {
            Property propItem = it.nextStatement().getPredicate();
            if (propItem.getURI().toString().equals(property.getUri())) {
                return true;
            }

        }
        return false;
    }



}
