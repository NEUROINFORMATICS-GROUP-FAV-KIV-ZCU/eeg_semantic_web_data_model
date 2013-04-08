package cz.zcu.kiv.eeg.semweb.model.search;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Statement filter selector inherited from JENA selector
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PortalClassInstanceSelector extends SimpleSelector {

    private Condition cond;

    public PortalClassInstanceSelector(Property predicate, RDFNode parentClass, Condition cond) {
        super(null, predicate, parentClass);
        this.cond = cond;
    }

    @Override
    public boolean selects(Statement s) {

        if (cond != null) {
            return cond.getResult(s.getPredicate(), s.getSubject());
        } else {
            return true;
        }
    }
}
