package cz.zcu.kiv.eeg.semweb.model.api.utils;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DataConverter {

    public static Object convertObject(Literal literal) {

        if (literal.getDatatypeURI().equals(XSDDatatype.XSDstring.getURI())) {
            return new String(literal.getString());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDinteger.getURI())) {
            return new Integer(literal.getInt());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDfloat.getURI())) {
            return new Float(literal.getFloat());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDdouble.getURI())) {
            return new Double(literal.getDouble());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDdate.getURI())) { //TODO dateTime and Time
            return new String("Some date"); //TODO complete
        }else {
            return null; //TODO complete
        }

    }


}
