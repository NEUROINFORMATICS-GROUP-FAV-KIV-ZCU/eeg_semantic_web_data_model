package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public final class Literal extends Item {

    private Object value;
    private DataTypes type;

    public Literal(Object value) {
       setValue(value);
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

    @Override
    public String toString() {
        return value.toString();
    }
}
