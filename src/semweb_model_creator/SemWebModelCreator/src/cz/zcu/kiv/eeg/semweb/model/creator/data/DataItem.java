package cz.zcu.kiv.eeg.semweb.model.creator.data;

import java.util.ArrayList;
import java.util.List;

/**
 *  Object representing one SemWeb node - class/property containing its child nodes
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class DataItem {

    private String nodeName;
    protected List<DataItem> childNodes;

    public DataItem(String name) {
        childNodes = new ArrayList<DataItem>();
        this.nodeName = name;
    }

    public void addChildNode(String name) {
    }

    public String getName() {
        return this.nodeName;
    }

    public List<DataItem> getChildNodes() {
        return this.childNodes;
    }
}
