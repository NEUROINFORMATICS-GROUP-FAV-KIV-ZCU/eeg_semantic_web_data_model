package cz.zcu.kiv.eeg.semweb.gui.propertywindow;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;

/**
 * Listener of actual selected node in JTree PropertyLister
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PropertyNodeSelectionListener implements TreeSelectionListener {

    private PortalModel model; //Portal data model API connector
    private PropertyTreePanel treePanel;  //Wrapping Jtree PropertySelector panel
    private ContentPanel dataPanel; //Right side window panel with Property value setter
    private String selectedPropertyNode; //Actual selected property
    private static final Logger logger = Logger.getLogger(PropertyNodeSelectionListener.class);

    public PropertyNodeSelectionListener(PropertyTreePanel treePanel, PortalModel model, ContentPanel dataPanel) {

        this.model = model;
        this.treePanel = treePanel;
        this.dataPanel = dataPanel;

        this.selectedPropertyNode = null;
    }

    /**
     * Select node of JTree PropertyLister
     * @param e
     */
    public void valueChanged(TreeSelectionEvent e) {

        if (e.getNewLeadSelectionPath() == null) {
            treePanel.setUpdateDescrBt(false);
            return;
        }

        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

        String node = selected.getUserObject().toString();

        if (node != null && !selected.isRoot()) { //Valid node selected
            try {
            } catch (Exception ex) {
                logger.error("Node selectiong error", ex);
            }
            treePanel.setDescription(model.getPropertyDescription(node));
            treePanel.setUpdateDescrBt(true);
            selectedPropertyNode = node;
            dataPanel.nodeSelected(node);
        } else {
            treePanel.setUpdateDescrBt(false);
        }
    }

    public String getSelectedNode() {
        return selectedPropertyNode;
    }
}
