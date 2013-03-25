package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TreeNodeSelectionListener implements TreeSelectionListener {

    private JComboBox box;
    private PortalModel model;

    public TreeNodeSelectionListener(JComboBox combo, PortalModel model) throws ConversionException, NonExistingUriNodeException {
        this.box = combo;
        this.model = model;
        setDefaultSelection();
    }

    public void valueChanged(TreeSelectionEvent e) {

        String node = ((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject().toString();

        if (node != null) {
            box.removeAllItems();
            try {
                addInstances(node);
            } catch (Exception ex) {
                System.out.println("Error when node selection. " + ex.toString());
            }
        }
    }

    private void addInstances(String node) throws NonExistingUriNodeException, ConversionException {

        List<Item> items = model.listInstance(node, false);

        for (Item individual: items) {
            box.addItem(individual.getAsUri().toString());
        }
    }

    private void setDefaultSelection() throws ConversionException, NonExistingUriNodeException {
        List<Item> items = model.listInstance("http://cz.zcu.kiv.eeg#group", false);

        for (Item individual: items) {
            box.addItem(individual.getAsUri().toString());
        }
    }

}
