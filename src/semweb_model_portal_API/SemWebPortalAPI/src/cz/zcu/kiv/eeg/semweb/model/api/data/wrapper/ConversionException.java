package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

/**
 * Literal dataType conversion exception
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConversionException extends Exception {

    public ConversionException(String msg) {
        super(msg);
    }
}
