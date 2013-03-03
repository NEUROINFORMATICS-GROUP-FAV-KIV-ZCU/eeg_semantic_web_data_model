package cz.zcu.kiv.eeg.semweb.model.creator.data;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PropertyDataItem extends DataItem{

    private String range;
    private String domain;
    private boolean parent;

    public PropertyDataItem(String name, String range, String domain) {
        super(name);
        this.range = range;
        this.domain = domain;
        parent = false;
    }

    @Override
    public void addChildNode(String name) {
        childNodes.add(new PropertyDataItem(name));
    }

    public PropertyDataItem(String name) {
        super(name);
        parent = true;
    }

    public String getDomain() {
        return domain;
    }

    public String getRange() {
        return range;
    }
}
