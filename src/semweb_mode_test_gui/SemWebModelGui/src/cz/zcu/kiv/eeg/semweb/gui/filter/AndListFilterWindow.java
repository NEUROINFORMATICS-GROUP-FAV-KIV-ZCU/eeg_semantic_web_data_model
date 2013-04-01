package cz.zcu.kiv.eeg.semweb.gui.filter;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.search.ConjunctionCondition;
import cz.zcu.kiv.eeg.semweb.model.search.HasPropertyLikeCondition;
import javax.swing.JFrame;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class AndListFilterWindow extends FilterWindow {

    public AndListFilterWindow(PortalModel model, JFrame root, ConjunctionCondition c) {

        super(model, root, c, "Set filter AND conditions", true);
    }

    @Override
    protected void updateCondition(int index) {
        new ConditionSelector(model, this, cond, index);
    }

    @Override
    protected void addCondition() {
        cond.addCondition(new HasPropertyLikeCondition(""));
        updateView();
        new ConditionSelector(model, this, cond, cond.getConditionList().size() - 1);
    }

}
