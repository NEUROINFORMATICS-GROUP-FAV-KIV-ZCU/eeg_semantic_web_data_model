package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.awt.Color;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ClassTreePanel extends JPanel {

    private PortalModel model;

    public ClassTreePanel(PortalModel model, TreeNodeSelectionListener listener) throws NonExistingUriNodeException {

        this.model = model;

        setBackground(Color.YELLOW);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Portal model");
        addNodes(root);
        JTree tree = new JTree(root);
        tree.addTreeSelectionListener(listener);
        add(tree);
    }

    private void addNodes(DefaultMutableTreeNode root) throws NonExistingUriNodeException {

        DefaultMutableTreeNode node;

        List<String> cls = model.listParentClasses();

        for (String item: cls) {

            node = new DefaultMutableTreeNode(item);
            listSubClasses(node, item);
            root.add(node);
        }
    }


    private void listSubClasses(DefaultMutableTreeNode parent, String nodeName) throws NonExistingUriNodeException {

        DefaultMutableTreeNode node;

        if (model.hasSubClasses(nodeName)) {
            List<String> cls = model.listSubClasses(nodeName);

            for (String subClass: cls) {
                node = new DefaultMutableTreeNode(subClass);
                listSubClasses(node, subClass);
                parent.add(node);
            }
        }
    }




}