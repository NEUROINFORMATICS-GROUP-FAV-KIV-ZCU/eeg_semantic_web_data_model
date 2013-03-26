package cz.zcu.kiv.eeg.semweb.model.creator.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PropertyDataItem extends DataItem{

    private String range;
    private String domain;
    private List<PropertyDataItem> childNodes;

    public PropertyDataItem(String name, String range, String domain, String description) {
        super(name, description);
        childNodes = new ArrayList<PropertyDataItem>();
        this.range = range;
        this.domain = domain;
    }

    public void addChildNode(String name) {
        childNodes.add(new PropertyDataItem(name));
        hasChildNodes = true;
    }

    public List<PropertyDataItem> getChildNodes() {
        return this.childNodes;
    }

    public PropertyDataItem(String name) {
        super(name, null);
        childNodes = new ArrayList<PropertyDataItem>();
    }

    public String getDomain() {
        return domain;
    }

    public String getRange() {
        return range;
    }
}
