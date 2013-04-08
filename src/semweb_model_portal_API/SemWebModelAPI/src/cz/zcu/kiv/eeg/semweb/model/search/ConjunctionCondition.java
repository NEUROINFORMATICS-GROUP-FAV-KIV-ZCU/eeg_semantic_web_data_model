package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 *  Filter condition container for AND condition statement
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConjunctionCondition extends ConditionList {

    /**
     * Returns true only if ALL subconditions are true
     *
     * @param predicate
     * @param object
     * @return
     */
    @Override
    public boolean getResult(Property predicate, Resource object) {

        if (conds.isEmpty()) {
            return true;
        }

        for (Condition subCond : conds) {
            if (!subCond.getResult(predicate, object)) {
                return false;
            }
        }
        return true;
    }
}
