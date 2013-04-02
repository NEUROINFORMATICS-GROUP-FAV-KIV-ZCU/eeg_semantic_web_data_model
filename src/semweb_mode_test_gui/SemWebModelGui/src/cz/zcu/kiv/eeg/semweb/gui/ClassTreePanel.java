package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ClassTreePanel extends JPanel {

    private PortalModel model;
    private MainWindow mw;
    private JTree tree;
    private DefaultMutableTreeNode root;
    private ClassTreePanel self;

    private JTextArea description;
    private JButton updDescrBt;

    private TreeNodeSelectionListener listener;

    private static final Logger logger = Logger.getLogger(TreeNodeSelectionListener.class);

    public ClassTreePanel(PortalModel model, DataPanel dataPanel, MainWindow mw) throws NonExistingUriNodeException {

        this.model = model;
        this.mw = mw;
        this.self = this;

        
        setLayout(new BorderLayout());

        listener = new TreeNodeSelectionListener(this, model, dataPanel);

        add(createTree(listener), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private Component createTree(TreeNodeSelectionListener listener) {

        root = new DefaultMutableTreeNode("Portal model");
        addNodes(root);

        tree = new JTree(root);
        tree.addTreeSelectionListener(listener);


        DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
        rend.setOpenIcon(null);
        rend.setClosedIcon(null);
        rend.setLeafIcon(null);
        tree.setCellRenderer(rend);

        JScrollPane scrollPanel = new JScrollPane();
        scrollPanel.getViewport().add(tree);

        return scrollPanel;
    }

    private Component createDecriptionPanel() {

        description = new JTextArea(4, 25);
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBackground(new Color(245, 245, 245));

        JScrollPane descriptionPanel = new JScrollPane();
        descriptionPanel.getViewport().add(description);

        return descriptionPanel;
    }

    private Component createButtonPanel() {

        
        updDescrBt = new JButton("Set description");
        updDescrBt.setEnabled(false);

        updDescrBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                model.updateClassDescription(tree.getSelectionPath().getLastPathComponent().toString(), description.getText().trim());
            }
        });

        JButton addClassBt = new JButton("Add class");

        addClassBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new AddClassWindow(self);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addClassBt);
        buttonPanel.add(updDescrBt);

        return buttonPanel;
    }

    private JPanel createBottomPanel() {

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(createDecriptionPanel(), BorderLayout.CENTER);
        bottomPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        return bottomPanel;
    }

    private void addNodes(DefaultMutableTreeNode root) {

        DefaultMutableTreeNode node;

        List<String> cls = model.listParentClasses();

        for (String item: cls) {

            node = new DefaultMutableTreeNode(item);
            listSubClasses(node, item);
            root.add(node);
        }
    }


    private void listSubClasses(DefaultMutableTreeNode parent, String nodeName)  {

        DefaultMutableTreeNode node;
        try {
            if (model.hasSubClasses(nodeName)) {
                List<String> cls = model.listSubClasses(nodeName);
                for (String subClass : cls) {
                    node = new DefaultMutableTreeNode(subClass);
                    listSubClasses(node, subClass);
                    parent.add(node);
                }
            }
        } catch (NonExistingUriNodeException ex) {
            logger.error("Node " + nodeName + " does not exists in model.", ex);
        }
    }

    public void setDescription(String text) {
        description.setText(text);
    }

    public void setUpdateDescrBt(boolean enabled) {
        updDescrBt.setEnabled(enabled);
    }

    public PortalModel getModel() {
        return model;
    }

    public String getSelectedNode() {

        return listener.getSelectedNode();
    }

    public void setMainWidnow(boolean enabled) {
        mw.setEnabled(enabled);
    }

    public void updateTree() {

        root.removeAllChildren();
        addNodes(root);

        DefaultTreeModel mainTree = (DefaultTreeModel) tree.getModel();

        mainTree.reload(root);
    }
}
