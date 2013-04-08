package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Class tree panel JTree node selection listener implementation
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TreeClassNodeSelectionListener implements TreeSelectionListener {

    private PortalModel model; //portal model API connector
    private ClassTreePanel treePanel; // JTree ClassLister panel
    private DataPanel dataPanel; //Individual lister panel
    private String selectedClassNode; //actual selected class node

    public TreeClassNodeSelectionListener(ClassTreePanel treePanel, PortalModel model, DataPanel dataPanel) {

        this.model = model;
        this.treePanel = treePanel;
        this.dataPanel = dataPanel;

        this.selectedClassNode = null;
    }

    /**
     * Selecting new class node
     * @param e
     */
    public void valueChanged(TreeSelectionEvent e) {

        if (e.getNewLeadSelectionPath() == null) {
            treePanel.setUpdateDescrBt(false);
            return;
        }

        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

        String node = selected.getUserObject().toString();

        if (node != null && !selected.isRoot()) { //selected node is valid

            treePanel.setDescription(model.getClassDescription(node));
            treePanel.setUpdateDescrBt(true);
            selectedClassNode = node;
            dataPanel.nodeSelected(node);
        } else {
            treePanel.setUpdateDescrBt(false);
        }
    }

    public String getSelectedNode() {
        return selectedClassNode;
    }
}
