package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
    private JLabel label;

    public TreeNodeSelectionListener(JComboBox combo, JLabel label, PortalModel model) throws ConversionException, NonExistingUriNodeException {
        this.box = combo;
        this.model = model;
        this.label = label;
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
        label.setText(model.getClassDescription(node));

    }

    private void setDefaultSelection() throws ConversionException, NonExistingUriNodeException {
        List<Item> items = model.listInstance("http://cz.zcu.kiv.eeg#group", false);

        for (Item individual: items) {
            box.addItem(individual.getAsUri().toString());
        }
    }

}
