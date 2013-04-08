package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

/**
 *  Model object item abstract wrapper for URI nodes and literats
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class Item {

    /**
     * Test item is URI
     * @return
     */
    public abstract boolean isUri();

    /**
     * Test item is Literal
     * @return
     */
    public abstract boolean isLiteral();

    /**
     * Get item as literal object
     *
     * @return Literalobjext
     * @throws ConversionException
     */
    public LiteralItem getAsLiteral() throws ConversionException {

        if (isLiteral()) {
            return ((LiteralItem) this);
        } else {
            throw new ConversionException("Item is not Literal");
        }
    }

    /**
     * Get item as Uri object
     *
     * @return URI object
     * @throws ConversionException
     */
    public UriItem getAsUri() throws ConversionException {

        if (isUri()) {
            return ((UriItem) this);
        } else {
            throw new ConversionException("Item is not Literal");
        }
    }
}
