package cz.zcu.kiv.eeg.semweb.model.api.utils;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;

/**
 * Instace URI generator creates URIs for class instances
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class InstanceUriGen {


    private static final String counterPropertyUri = "classInstanceCounter";

    public static String generateInstanceUri(String classNameUri, PortalModel model, String ns) {

        OntClass parent = model.getOntModel().getOntClass(classNameUri);

        Property counterProp = model.getOntModel().createProperty(ns + counterPropertyUri);

        RDFNode counterPropValue = parent.getPropertyValue(counterProp);

        if (counterPropValue == null) { //no counter set
            Literal counter = model.getOntModel().createTypedLiteral((int)0);
            model.getOntModel().add(parent, counterProp, counter);
            return classNameUri + "/inst_0";

        }else {
            int countVal = counterPropValue.asLiteral().getInt() + 1;
            parent.removeProperty(counterProp, counterPropValue);//remove
            parent.addLiteral(counterProp, model.getOntModel().createTypedLiteral(countVal)); //add new
            return classNameUri + "/inst_" + countVal;
        }
    }


}
