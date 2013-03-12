package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.toplevel;

import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Single {

    private Item value;

    public Single(Item value) {
        this.value = value;
    }

    public Item getValue() {
        return value;
    }

    public void setValue(Item value) {
        this.value = value;
    }
}
