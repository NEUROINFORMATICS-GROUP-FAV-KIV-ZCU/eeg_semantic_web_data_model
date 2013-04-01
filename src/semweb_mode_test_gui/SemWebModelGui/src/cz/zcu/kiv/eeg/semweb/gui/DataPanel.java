package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.gui.filter.MainOrListFilterWindow;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.search.DisjunctionCondition;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DataPanel extends JPanel {

    private PortalModel model;
    private MainWindow mw;
    private PropertyListPanel propPanel;


    private JComboBox selectBox;
    private String actualNode;
    private DisjunctionCondition filterCond;

    private String selectedItem;


    private static final Logger logger = Logger.getLogger(DataPanel.class);

    public DataPanel(PortalModel model, MainWindow mw) {

        this.model = model;
        this.mw = mw;
        this.propPanel = new PropertyListPanel(model, this);


        selectedItem = null;

        setBackground(Color.GREEN);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(propPanel, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        filterCond = new DisjunctionCondition();
    }

    private JPanel createTopPanel() {

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel instLabel = new JLabel("Instances");

        selectBox = new JComboBox();

        selectBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updateSelectedIndividual();
            }
        });

        JButton updateBt = new JButton("Update");
        updateBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                nodeSelected(actualNode);
            }
        });


        JButton filterBt = new JButton("Set filter");
        filterBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new MainOrListFilterWindow(model, mw, filterCond);
            }
        });

        topPanel.add(instLabel);
        topPanel.add(selectBox);
        topPanel.add(updateBt);
        topPanel.add(filterBt);

        return topPanel;
    }

    private JPanel createBottomPanel() {

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.YELLOW);

        JButton addInstBt = new JButton("Add instance");
        JButton addPropBt = new JButton("Add property");

        bottomPanel.add(addInstBt);
        bottomPanel.add(addPropBt);

        return bottomPanel;
    }

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

    public void nodeSelected(String node) {
       
        if (node != null) {
            actualNode = node;
            selectBox.removeAllItems();

            for (String item: getInstancesList(node)) {
                selectBox.addItem(item);
            }
            updateSelectedIndividual();
        }
    }

    private void updateSelectedIndividual() {

        if (selectBox.getSelectedItem() == null) {
            selectedItem = null;
        } else {
            selectedItem = selectBox.getSelectedItem().toString();
        }

        propPanel.updateData(selectedItem);
    }


    public void setSelectedNode(String node) {
        selectBox.removeAllItems();
        selectBox.addItem(node);
    }

}
