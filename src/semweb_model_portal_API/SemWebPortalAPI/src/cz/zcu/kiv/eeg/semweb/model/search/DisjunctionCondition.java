package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DisjunctionCondition extends ConditionList {

    /**
     * Returns true if AT LEAST one subconditions is true
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

        for (Condition subCond: conds) {
            if (subCond.getResult(predicate, object)) {
                return true;
            }
        }
        return false;
    }

}
