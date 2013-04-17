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
import org.apache.log4j.Logger;

/**
 * Panel with treeNode selector uses to list and select model defined classes and organise it
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ClassTreePanel extends JPanel {

    private PortalModel model; //Portal data model API connector
    private MainWindow mw; //Main application window
    private JTree classListerTree; //Jtree containing data model classes
    private DefaultMutableTreeNode rootNode; //root node of classLister Jtree
    private ClassTreePanel self;
    private JTextArea description; //Class description panel
    private JButton updDescrBt; //update description button
    private JButton removeClassBt; //removeClass button
    private TreeClassNodeSelectionListener listener; //listener of selected node of ClassLister JTree
    private DataPanel dataPanel; //tree data panel

    private static final Logger logger = Logger.getLogger(ClassTreePanel.class);

    public ClassTreePanel(PortalModel model, DataPanel dataPanel, MainWindow mw) throws NonExistingUriNodeException {

        this.model = model;
        this.mw = mw;
        this.dataPanel = dataPanel;
        this.self = this;

        listener = new TreeClassNodeSelectionListener(this, model, dataPanel);

        setLayout(new BorderLayout());
        add(createTree(listener), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    /**
     * Create tree panel
     *
     * @param listener nodeSelection listener
     * @return create panel with Jtree ClassSelector
     */
    private Component createTree(TreeClassNodeSelectionListener listener) {

        rootNode = new DefaultMutableTreeNode("Portal model");
        addNodes(rootNode);

        classListerTree = new JTree(rootNode);
        classListerTree.addTreeSelectionListener(listener);


        DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
        rend.setOpenIcon(null);
        rend.setClosedIcon(null);
        rend.setLeafIcon(null);
        classListerTree.setCellRenderer(rend);

        JScrollPane scrollPanel = new JScrollPane();
        scrollPanel.getViewport().add(classListerTree);

        return scrollPanel;
    }

    /**
     * Create panel with description TextArea to enable view and edit class description annotation
     *
     * @return Created panel with ClassDescription textArea
     */
    private Component createDecriptionPanel() {

        description = new JTextArea(4, 25);
        description.setFont(new Font("Arial", Font.BOLD, 12));
        description.setBackground(new Color(245, 245, 245));

        JScrollPane descriptionPanel = new JScrollPane();
        descriptionPanel.getViewport().add(description);

        return descriptionPanel;
    }

    /**
     * Creates button panel that allows to add new classes and set description of present classes
     *
     * @return Button panel
     */
    private Component createButtonPanel() {


        updDescrBt = new JButton("Set description");
        updDescrBt.setEnabled(false);

        updDescrBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                model.updateClassDescription(classListerTree.getSelectionPath().getLastPathComponent().toString(), description.getText().trim());
            }
        });

        JButton addClassBt = new JButton("Add class");

        addClassBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new AddClassWindow(self);
            }
        });

        removeClassBt = new JButton("Remove class");
        removeClassBt.setEnabled(false);

        removeClassBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                model.removeClass(getSelectedNode());
                removeClassBt.setEnabled(false);
                updateTree();
                dataPanel.nodeSelected(null);
                setDescription("");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(addClassBt);
        buttonPanel.add(updDescrBt);
        buttonPanel.add(removeClassBt);

        return buttonPanel;
    }

    /**
     * Wrappes description panel and Button panel to one panel (due the layout reason)
     *
     * @return Buttons and Description panel
     */
    private JPanel createBottomPanel() {

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(createDecriptionPanel(), BorderLayout.CENTER);
        bottomPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        return bottomPanel;
    }

    /**
     * Initialize ClassSelector tree to insert actual model class nodes (hierarchicaly)
     *
     * @param root Root node of JTree
     */
    private void addNodes(DefaultMutableTreeNode root) {

        DefaultMutableTreeNode node;

        List<String> cls = model.listParentClasses();

        for (String item : cls) {

            node = new DefaultMutableTreeNode(item);
            listSubClasses(node, item);
            root.add(node);
        }
    }

    /**
     * List model available subclasses of selected class and add them to parent JTree node
     *
     * @param parent Parent classNode
     * @param nodeName Parent class URI
     */
    private void listSubClasses(DefaultMutableTreeNode parent, String nodeName) {

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
        removeClassBt.setEnabled(enabled);
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

    /**
     * Update model method called outside of ClassTreePanel by AddClassWidnwow - when new class is created
     * Update Jtree ClassSelector to present state
     * 
     */
    public void updateTree() {

        rootNode.removeAllChildren();
        addNodes(rootNode);

        DefaultTreeModel mainTree = (DefaultTreeModel) classListerTree.getModel();

        mainTree.reload(rootNode);
    }
}
