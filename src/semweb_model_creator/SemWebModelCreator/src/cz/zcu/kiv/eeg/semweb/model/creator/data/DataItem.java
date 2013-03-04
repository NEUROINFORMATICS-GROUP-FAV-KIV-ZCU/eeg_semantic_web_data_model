package cz.zcu.kiv.eeg.semweb.model.creator.data;

import java.util.ArrayList;
import java.util.List;

/**
 *  Object representing one SemWeb node - class/property containing its child nodes
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class DataItem {

    private String nodeName;
    protected boolean hasChildNodes;

    public DataItem(String name) {
        this.nodeName = name;
        hasChildNodes = false;
    }

    public String getName() {
        return this.nodeName;
    }

    public boolean hasChildNodes() {
        return hasChildNodes;
    }
}
