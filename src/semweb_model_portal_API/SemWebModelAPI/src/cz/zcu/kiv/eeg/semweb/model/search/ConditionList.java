package cz.zcu.kiv.eeg.semweb.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 *  Condition container wrapper for OR or AND conditions list
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class ConditionList extends Condition {

    protected List<Condition> conds;

    public ConditionList() {
        conds = new ArrayList<Condition>();
    }

    public void addCondition(Condition c) {
        conds.add(c);
    }

    public void replaceCondition(Condition c, int index) {
        conds.remove(index);
        conds.add(index, c);
    }

    public List<Condition> getConditionList() {
        return conds;
    }

    public Condition getCond(int index) {
        return conds.get(index);
    }

    public void removeCondition(int index) {
        conds.remove(index);
    }

    public void removeAll() {
        conds.clear();
    }



}
