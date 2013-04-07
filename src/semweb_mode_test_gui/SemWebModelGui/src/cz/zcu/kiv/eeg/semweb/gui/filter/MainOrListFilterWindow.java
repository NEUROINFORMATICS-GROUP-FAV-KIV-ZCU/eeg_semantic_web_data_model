package cz.zcu.kiv.eeg.semweb.gui.filter;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.search.ConjunctionCondition;
import cz.zcu.kiv.eeg.semweb.model.search.DisjunctionCondition;
import javax.swing.JFrame;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class MainOrListFilterWindow extends FilterWindow {


    public MainOrListFilterWindow (PortalModel model, JFrame mw, DisjunctionCondition c) {

        super(model, mw, c, "Set filter OR conditions", false);
    }

    @Override
    protected void updateCondition(int index) {
       new AndListFilterWindow(model, this, ((ConjunctionCondition) cond.getCond(index)));
    }

    @Override
    protected void addCondition() {
        cond.addCondition(new ConjunctionCondition());
        updateView();
        updateCondition(cond.getConditionList().size() - 1);
    }

}
