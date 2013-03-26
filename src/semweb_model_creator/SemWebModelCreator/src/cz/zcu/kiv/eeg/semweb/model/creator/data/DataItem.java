package cz.zcu.kiv.eeg.semweb.model.creator.data;

/**
 *  Object representing one SemWeb node - class/property containing its child nodes
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class DataItem {

    private String nodeName;
    protected boolean hasChildNodes;
    protected String description;

    public DataItem(String name, String description) {
        this.nodeName = name;
        this.description = description;
        hasChildNodes = false;
    }

    public String getName() {
        return this.nodeName;
    }

     public String getDescription() {
        return description;
    }

    public boolean hasChildNodes() {
        return hasChildNodes;
    }


}
