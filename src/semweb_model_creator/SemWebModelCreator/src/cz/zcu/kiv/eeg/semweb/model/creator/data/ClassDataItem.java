package cz.zcu.kiv.eeg.semweb.model.creator.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ClassDataItem extends DataItem {

    private List<ClassDataItem> childNodes;

    public ClassDataItem(String name) {
        super(name);
        childNodes = new ArrayList<ClassDataItem>();
    }

    public void addChildNode(String name) {
        hasChildNodes = true;
        childNodes.add(new ClassDataItem(name));
    }

    public List<ClassDataItem> getChildNodes() {
        return this.childNodes;
    }
}
