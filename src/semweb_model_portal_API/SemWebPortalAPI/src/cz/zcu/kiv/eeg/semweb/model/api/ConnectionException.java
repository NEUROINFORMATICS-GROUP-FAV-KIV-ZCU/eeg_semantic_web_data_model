package cz.zcu.kiv.eeg.semweb.model.api;

/**
 * Connection exception raised when connecting to Oracle semWeb database failed
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConnectionException extends Exception {

    public ConnectionException(String message, Throwable ex) {
        super(message, ex);
    }
}
