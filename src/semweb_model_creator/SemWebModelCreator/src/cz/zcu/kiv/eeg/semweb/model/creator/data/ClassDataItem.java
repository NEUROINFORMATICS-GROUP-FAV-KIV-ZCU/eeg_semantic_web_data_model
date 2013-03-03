package cz.zcu.kiv.eeg.semweb.model.creator.data;


/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ClassDataItem extends DataItem {

    public ClassDataItem(String name) {
        super(name);
    }

    @Override
    public void addChildNode(String name) {
        childNodes.add(new ClassDataItem(name));
    }
}
