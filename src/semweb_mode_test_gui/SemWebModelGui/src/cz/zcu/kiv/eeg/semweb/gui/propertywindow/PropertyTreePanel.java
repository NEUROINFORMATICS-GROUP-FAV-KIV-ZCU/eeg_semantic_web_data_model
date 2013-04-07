package cz.zcu.kiv.eeg.semweb.gui.propertywindow;
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
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PropertyTreePanel extends JPanel {

    private PortalModel model;
    private AddPropertyWindow mw;
    private JTree tree;
    private DefaultMutableTreeNode root;
    private PropertyTreePanel self;
    private ContentPanel contPanel;

    private String selectedClass;

    private JTextArea description;
    private JButton updDescrBt;

    private PropertyNodeSelectionListener listener;

    private static final Logger logger = Logger.getLogger(PropertyTreePanel.class);

    public PropertyTreePanel(PortalModel model, ContentPanel dataPanel, AddPropertyWindow mw, String selClass) {

        this.model = model;
        this.mw = mw;
        this.self = this;
        this.contPanel = dataPanel;
        this.selectedClass = selClass;

        setLayout(new BorderLayout());

        listener = new PropertyNodeSelectionListener(this, model, dataPanel);

        add(createTree(listener), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);


        System.out.println("Gathered Class: " + selClass);
    }

    private Component createTree(PropertyNodeSelectionListener listener) {

        root = new DefaultMutableTreeNode("Portal property");
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

                model.updatePropertyDescription(tree.getSelectionPath().getLastPathComponent().toString(), description.getText().trim());
            }
        });

        JButton addClassBt = new JButton("Add property");

        addClassBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new CreatePropertyWindow(self, selectedClass);
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

        List<String> props = model.listPropertiesByDomain(selectedClass);

        for (String item: props) {

            node = new DefaultMutableTreeNode(item);
            listSubProperties(node, item);
            root.add(node);
        }
    }


    private void listSubProperties(DefaultMutableTreeNode parent, String nodeName)  {

        DefaultMutableTreeNode node;
        try {
            if (model.hasSubProperties(nodeName)) {
                List<String> cls = model.listSubProperties(nodeName);
                for (String subClass : cls) {
                    node = new DefaultMutableTreeNode(subClass);
                    listSubProperties(node, subClass);
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

