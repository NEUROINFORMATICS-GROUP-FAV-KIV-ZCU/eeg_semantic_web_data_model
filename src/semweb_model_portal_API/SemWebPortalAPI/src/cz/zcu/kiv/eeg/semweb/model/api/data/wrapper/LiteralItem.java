package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.utils.DataConverter;
import java.sql.Time;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public final class LiteralItem extends Item {

    private Object value;
    private DataTypes type;
    private Literal original;
    private OntProperty chainProperty;
    private Individual parentInd;

    private PortalModel model;

    public LiteralItem(Literal orginalLiteral, OntProperty chainedProperty, Individual parentIndividual, PortalModel model) throws ParseException {
       setValue(DataConverter.convertObject(orginalLiteral));

       this.model = model;
       this.chainProperty = chainedProperty;
       this.original = orginalLiteral;
       this.parentInd = parentIndividual;
    }

    public DataTypes getDataType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;

        if (value instanceof Integer) {
            type = DataTypes.INTEGER;
        } else if (value instanceof Float) {
            type = DataTypes.FLOAT;
        } else if (value instanceof Double) {
            type = DataTypes.DOUBLE;
        } else if (value instanceof String) {
            type = DataTypes.STRING;
        } else if (value instanceof Date) {
            type = DataTypes.DATE;
        } else if (value instanceof Time) {
            type = DataTypes.TIME;
        } else if (value instanceof Calendar) {
            type = DataTypes.DATE_TIME;
        }
    }

    @Override
    public boolean isUri() {
        return false;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    public void updateValue(Object newValue) {
        
        parentInd.removeProperty(chainProperty, original);//remove
        parentInd.addLiteral(chainProperty, model.getOntModel().createTypedLiteral(newValue, chainProperty.getRange().getURI())); //add new
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
