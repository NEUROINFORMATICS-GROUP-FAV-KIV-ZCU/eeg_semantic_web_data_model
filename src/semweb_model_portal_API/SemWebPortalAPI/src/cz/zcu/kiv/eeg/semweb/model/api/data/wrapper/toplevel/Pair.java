package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.toplevel;

import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;


/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Pair {

    private Item property;
    private Item value;

    public Pair(Item property, Item value) {
        this.property = property;
        this.value = value;
    }

    public Item getProperty() {
        return property;
    }

    public void setProperty(Item property) {
        this.property = property;
    }

    public Item getValue() {
        return value;
    }

    public void setValue(Item value) {
        this.value = value;
    }
}
