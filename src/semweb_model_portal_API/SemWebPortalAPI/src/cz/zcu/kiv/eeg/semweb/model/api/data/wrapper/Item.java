package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class Item {

    public abstract boolean isUri();
    public abstract boolean isLiteral();

    public LiteralItem getAsLiteral() throws ConversionException {

        if (isLiteral()) {
            return ((LiteralItem) this);
        }else {
            throw new ConversionException("Item is not Literal");
        }
    }

    public UriItem getAsUri() throws ConversionException {

        if (isUri()) {
            return ((UriItem) this);
        }else {
            throw new ConversionException("Item is not Literal");
        }
    }


}
