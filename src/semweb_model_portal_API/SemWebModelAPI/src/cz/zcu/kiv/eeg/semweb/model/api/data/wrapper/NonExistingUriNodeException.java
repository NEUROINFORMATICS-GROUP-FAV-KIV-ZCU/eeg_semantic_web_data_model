package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

/**
 * Non existing node exception
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class NonExistingUriNodeException extends Exception {

    public NonExistingUriNodeException(String msg) {
        super(msg);
    }
}
