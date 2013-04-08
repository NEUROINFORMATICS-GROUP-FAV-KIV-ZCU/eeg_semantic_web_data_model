package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Abstract filter condition
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class Condition {

    private boolean negation = false;

    public boolean getResult(Property predicate, Resource object) {
        if (negation){
            return !getResult(predicate, object);
        }
        else {
            return getResult(predicate, object);
        }
    }

    public void negate() {
        negation = true;
    }


}
