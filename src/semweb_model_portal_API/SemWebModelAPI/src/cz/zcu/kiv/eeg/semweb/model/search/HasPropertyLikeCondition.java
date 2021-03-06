package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Filter condition individual has property with name containing specified value
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class HasPropertyLikeCondition extends Condition {

    private String property;

    public HasPropertyLikeCondition(String text) {
        this.property = text;
    }

    public String getPredicate() {
        return property;
    }

    @Override
    public boolean getResult(Property predicate, Resource object) {

        StmtIterator it = object.listProperties();

        while (it.hasNext()) {
            Property propItem = it.nextStatement().getPredicate();
            if (propItem.getURI().toString().contains(property)) {
                return true;
            }

        }
        return false;
    }
}
