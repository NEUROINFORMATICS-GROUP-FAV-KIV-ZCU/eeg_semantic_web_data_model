package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.util.List;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TreeNodeSelectionListener implements TreeSelectionListener {

    private PortalModel model;
    private ClassTreePanel treePanel;

    private String selectedNode;

    public TreeNodeSelectionListener(ClassTreePanel treePanel, PortalModel model) {
        
        this.model = model;
        this.treePanel = treePanel;

        this.selectedNode = null;
    }

    public void valueChanged(TreeSelectionEvent e) {

        if (e.getNewLeadSelectionPath() == null) {
            treePanel.setUpdateDescrBt(false);
            return;
        }

        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

        String node = selected.getUserObject().toString();

        if (node != null && !selected.isRoot()) {
            //box.removeAllItems();
            try {
                addInstances(node);
            } catch (Exception ex) {
                System.out.println("Error when node selection. " + ex.toString());
            }
            treePanel.setDescription(model.getClassDescription(node));
            treePanel.setUpdateDescrBt(true);
            selectedNode = node;
        }else {
            treePanel.setUpdateDescrBt(false);
        }
    }

    private void addInstances(String node) throws NonExistingUriNodeException, ConversionException {

        List<Item> items = model.listInstance(node, false);

        for (Item individual: items) {
            //box.addItem(individual.getAsUri().toString());
        }
        //label.setText(model.getClassDescription(node));

    }

    public String getSelectedNode() {
        return selectedNode;
    }

}
