package cz.zcu.kiv.eeg.semweb.model.creator.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class item wrapper (wrapped XML document loaded classes)
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ClassDataItem extends DataItem {

    private List<ClassDataItem> childNodes;

    public ClassDataItem(String name, String description) {
        super(name, description);
        childNodes = new ArrayList<ClassDataItem>();
    }

    public void addChildNode(String name) {
        hasChildNodes = true;
        childNodes.add(new ClassDataItem(name, null));
    }

    public List<ClassDataItem> getChildNodes() {
        return this.childNodes;
    }
}
