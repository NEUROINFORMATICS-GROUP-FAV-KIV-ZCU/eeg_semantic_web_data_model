package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConjunctionCondition extends Condition {

    private List<Condition> conds;

    public ConjunctionCondition() {
        conds = new ArrayList<Condition>();
    }

    public void addCondition(Condition c) {
        conds.add(c);
    }

    /**
     * Returns true only if ALL subconditions are true
     *
     * @param predicate
     * @param object
     * @return
     */
    @Override
    public boolean getResult(Property predicate, Resource object) {

        for (Condition subCond: conds) {
            if (!subCond.getResult(predicate, object)) {
                return false;
            }
        }
        return true;
    }



}
