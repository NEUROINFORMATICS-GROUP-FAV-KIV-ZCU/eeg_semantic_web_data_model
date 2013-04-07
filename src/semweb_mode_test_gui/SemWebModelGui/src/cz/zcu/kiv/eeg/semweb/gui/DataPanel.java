package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.gui.filter.MainOrListFilterWindow;
import cz.zcu.kiv.eeg.semweb.gui.propertywindow.AddPropertyWindow;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import cz.zcu.kiv.eeg.semweb.model.search.DisjunctionCondition;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 * Right part of main window - canations ClassInstances (Individuals) lister with filter setter
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DataPanel extends JPanel {

    private PortalModel model;
    private MainWindow mw;
    private PropertyListPanel propPanel; //Properties lister - lists all available selected individual properties

    private JComboBox individualSelectorComboBox; //selector for classInstance - Individual
    private String actualSelectedClassNode; //Actual selected class in Jtree ClassLister
    private DisjunctionCondition filterCond; //Filter condition to filter individuals

    private JButton addInstBt; //Add class instance (Individual) button
    private JButton addPropBt; //Add new property (property-value) to selected individual

    private String actualSelectedIndividual; //Actual selected individual


    private static final Logger logger = Logger.getLogger(DataPanel.class);

    public DataPanel(PortalModel model, MainWindow mw) {

        this.model = model;
        this.mw = mw;
        this.propPanel = new PropertyListPanel(model, this);

        actualSelectedIndividual = null;

        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(propPanel, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        filterCond = new DisjunctionCondition();
    }

    /**
     * Top panel caontaining individuals selector and filter setter
     *
     * @return JPanel
     */
    private JPanel createTopPanel() {

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel instLabel = new JLabel("Instances");

        individualSelectorComboBox = new JComboBox();

        individualSelectorComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updateSelectedIndividual();
            }
        });

        JButton updateBt = new JButton("Update");
        updateBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                nodeSelected(actualSelectedClassNode);
            }
        });


        JButton filterBt = new JButton("Set filter");
        filterBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new MainOrListFilterWindow(model, mw, filterCond);
            }
        });

        topPanel.add(instLabel);
        topPanel.add(individualSelectorComboBox);
        topPanel.add(updateBt);
        topPanel.add(filterBt);

        return topPanel;
    }

    /**
     * Bottom panel with addInstance and addProperty buttons
     *
     * @return JPanel
     */
    private JPanel createBottomPanel() {

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        addInstBt = new JButton("Add instance");
        addPropBt = new JButton("Add property");

        addInstBt.setEnabled(false);
        addPropBt.setEnabled(false);

        addInstBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createNewInstance();
            }
        });

        addPropBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addProperty();
            }
        });

        bottomPanel.add(addInstBt);
        bottomPanel.add(addPropBt);

        return bottomPanel;
    }

    /**
     * List instances of selected class filered by actual filter
     *
     * @param parentClass ParentClass URI
     * @return list of individuals (URIs)
     */
    private List<String> getInstancesList(String parentClass) {
        try {
            List<Item> inst = model.listInstance(parentClass, filterCond);

            List<String> indvdNames = new ArrayList<String>();
            
            for (Item ind: inst) {
                indvdNames.add(ind.getAsUri().getUri());
            }
            
            return indvdNames;

        } catch (Exception ex) {
            logger.error("Individual selection error", ex);
            return null;
        }
    }

    /**
     * Update list of individuals by JTree ClassSelector actual selected node (and filter)
     *
     * @param node selected node - Class
     */
    public void nodeSelected(String node) {
       
        if (node != null) {
            actualSelectedClassNode = node;
            individualSelectorComboBox.removeAllItems();

            for (String item: getInstancesList(node)) {
                individualSelectorComboBox.addItem(item);
            }
            updateSelectedIndividual();
        }
    }

    /**
     * Update property value lister - PropertyDataPanel to selected indvidual values
     */
    private void updateSelectedIndividual() {

        if (individualSelectorComboBox.getSelectedItem() == null) {
            actualSelectedIndividual = null;
            addPropBt.setEnabled(false);
        } else {
            actualSelectedIndividual = individualSelectorComboBox.getSelectedItem().toString();
            addPropBt.setEnabled(true);
        }

        propPanel.updateData(actualSelectedIndividual);
        addInstBt.setEnabled(true);
    }


    /**
     * Set selected individual when GoTo operation on URI node raised
     *
     * @param node IndividualUri
     */
    public void setSelectedNode(String node) {
        individualSelectorComboBox.removeAllItems();
        individualSelectorComboBox.addItem(node);

        updateSelectedIndividual();
    }

    /**
     * Create new class instance (Individual)
     */
    private void createNewInstance() {
        try {
            UriItem newInd = model.createClassInstance(actualSelectedClassNode);

            nodeSelected(actualSelectedClassNode);
            selectIndividual(newInd.getUri());

        } catch (NonExistingUriNodeException ex) {
            logger.error("Can not find parent class.", ex);
        }
    }

    private void selectIndividual(String indUri) {
        individualSelectorComboBox.setSelectedItem(indUri);
    }

    /**
     * Add new property-value to selected instance
     */
    private void addProperty() {
        new AddPropertyWindow(model, this, actualSelectedIndividual);
        mw.setEnabled(false);
    }

    /**
     * Close PropertyAdd window and update individual property data
     */
    public void closePropertyAdderAndUpdate() {
        mw.setEnabled(true);
        updateSelectedIndividual();
    }

    /**
     * Return root frame component
     *
     * @return root Frame
     */
    public JFrame getRootFrame() {
        return mw;
    }

}
