package cz.zcu.kiv.eeg.semweb.model.api.utils;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DataConverter {


    public static Object convertObject(Literal literal) throws ParseException {

        if (literal.getDatatypeURI().equals(XSDDatatype.XSDstring.getURI())) {
            return literal.getString();
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDinteger.getURI())) {
            return new Integer(literal.getInt());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDfloat.getURI())) {
            return new Float(literal.getFloat());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDdouble.getURI())) {
            return new Double(literal.getDouble());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDboolean.getURI())) {
            return new Boolean(literal.getBoolean());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDlong.getURI())) {
            return new Long(literal.getLong());
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDdate.getURI())) { //TODO dateTime and Time
            SimpleDateFormat sd = new SimpleDateFormat("YYYY-MM-dd");
            return sd.parse(literal.getString().split("T")[0]);
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDtime.getURI())) {
            SimpleDateFormat sd = new SimpleDateFormat("hh:mm:ss");
            return sd.parse(literal.getString().split("T")[1]);
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDdateTime.getURI())) {
            SimpleDateFormat sd = new SimpleDateFormat("YYYY-MM-ddThh:mm:ss");
            return sd.parse(literal.getString());
        }else {
            return null; //TODO complete
        }

    }


}
