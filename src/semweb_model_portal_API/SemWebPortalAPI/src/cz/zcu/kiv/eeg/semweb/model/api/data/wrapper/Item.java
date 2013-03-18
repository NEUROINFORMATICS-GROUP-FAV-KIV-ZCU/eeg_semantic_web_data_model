package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class Item {

    public abstract boolean isUri();
    public abstract boolean isLiteral();

    public Literal getAsLiteral() throws ConversionException {

        if (isLiteral()) {
            return ((Literal) this);
        }else {
            throw new ConversionException("Item is not Literal");
        }
    }

    public Uri getAsUri() throws ConversionException {

        if (isUri()) {
            return ((Uri) this);
        }else {
            throw new ConversionException("Item is not Literal");
        }
    }


}
