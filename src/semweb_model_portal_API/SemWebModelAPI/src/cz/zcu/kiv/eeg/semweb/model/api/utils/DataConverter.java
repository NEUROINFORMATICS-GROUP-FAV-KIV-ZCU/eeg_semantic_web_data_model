package cz.zcu.kiv.eeg.semweb.model.api.utils;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import cz.zcu.kiv.eeg.semweb.model.api.DataType;
import java.text.ParseException;

/**
 * Data convertor utility
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DataConverter {


    /**
     * Converts literal to simple Java object
     * @param literal
     * @return
     * @throws ParseException
     */
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
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDdate.getURI())) { 
            return literal.toString().split("\\^")[0];
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDtime.getURI())) {
            return literal.toString().split("\\^")[0];
        } else if (literal.getDatatypeURI().equals(XSDDatatype.XSDdateTime.getURI())) {
            return literal.toString().split("\\^")[0];
        }else {
            return null; 
        }
    }

    /**
     * Convert URI datatype to ENUM value
     * @param uri
     * @return
     */
    public static DataType getTypeByUri(String uri) {

        if (uri.equals(XSDDatatype.XSDstring.getURI())) {
            return DataType.STRING_TYPE;
        } else if (uri.equals(XSDDatatype.XSDinteger.getURI())) {
            return DataType.INTEGER_TYPE;
        } else if (uri.equals(XSDDatatype.XSDdouble.getURI())) {
            return DataType.DOUBLE_TYPE;
        } else if (uri.equals(XSDDatatype.XSDfloat.getURI())) {
            return DataType.FLOAT_TYPE;
        }else if (uri.equals(XSDDatatype.XSDboolean.getURI())) {
            return DataType.BOOLEAN_TYPE;
        }else if (uri.equals(XSDDatatype.XSDlong.getURI())) {
            return DataType.LONG_TYPE;
        }else if (uri.equals(XSDDatatype.XSDdate.getURI())) {
            return DataType.DATE_TYPE;
        }else if (uri.equals(XSDDatatype.XSDtime.getURI())) {
            return DataType.TIME_TYPE;
        }else if (uri.equals(XSDDatatype.XSDdateTime.getURI())) {
            return DataType.DATE_TIME_TYPE;
        }else {
            return DataType.URI_TYPE;
        }

    }


}
