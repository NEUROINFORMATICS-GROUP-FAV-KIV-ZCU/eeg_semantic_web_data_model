package cz.zcu.kiv.eeg.semweb.gui.propertywindow;
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
public class NodeSelectionListener implements TreeSelectionListener {

    private PortalModel model;
    private PropertyTreePanel treePanel;
    private ContentPanel dataPanel;

    private String selectedNode;

    public NodeSelectionListener(PropertyTreePanel treePanel, PortalModel model, ContentPanel dataPanel) {

        this.model = model;
        this.treePanel = treePanel;
        this.dataPanel = dataPanel;

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
            } catch (Exception ex) {
                System.out.println("Error when node selection. " + ex.toString());
            }
            treePanel.setDescription(model.getPropertyDescription(node));
            treePanel.setUpdateDescrBt(true);
            selectedNode = node;
            dataPanel.nodeSelected(node);
        }else {
            treePanel.setUpdateDescrBt(false);
        }
    }

    public String getSelectedNode() {
        return selectedNode;
    }

}

