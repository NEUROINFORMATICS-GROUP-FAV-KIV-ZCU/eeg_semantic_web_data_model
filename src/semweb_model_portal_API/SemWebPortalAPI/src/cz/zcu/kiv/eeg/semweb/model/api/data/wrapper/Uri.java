package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Uri implements Item {

    private String namespace;
    private String localName;

    public Uri(String namespace, String value) {
        this.namespace = namespace;
        this.localName = value;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String value) {
        this.localName = value;
    }

    public ItemType getType() {
        return ItemType.URI;
    }



}
