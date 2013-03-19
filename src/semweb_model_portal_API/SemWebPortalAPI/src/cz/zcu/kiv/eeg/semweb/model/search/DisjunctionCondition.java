package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DisjunctionCondition extends Condition {

    private List<Condition> conds;

    public DisjunctionCondition() {
        conds = new ArrayList<Condition>();
    }

    public void addCondition(Condition c) {
        conds.add(c);
    }

    /**
     * Returns true if AT LEAST one subconditions is true
     *
     * @param predicate
     * @param object
     * @return
     */
    @Override
    public boolean getResult(Property predicate, Resource object) {

        for (Condition subCond: conds) {
            if (subCond.getResult(predicate, object)) {
                return true;
            }
        }
        return false;
    }

}
