package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.gui.filter.MainOrListFilterWindow;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import cz.zcu.kiv.eeg.semweb.model.search.ConjunctionCondition;
import cz.zcu.kiv.eeg.semweb.model.search.DisjunctionCondition;
import cz.zcu.kiv.eeg.semweb.model.search.PropertyValLikeCondition;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

    private JComboBox selectBox;
    private String actualNode;
    private DisjunctionCondition filterCond;



    private static final Logger logger = Logger.getLogger(DataPanel.class);

    public DataPanel(PortalModel model, MainWindow mw) {

        this.model = model;
        this.mw = mw;

        setBackground(Color.GREEN);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        filterCond = new DisjunctionCondition();

        //TODO remove
            
            ConjunctionCondition c1 = new ConjunctionCondition();
            c1.addCondition(new PropertyValLikeCondition(new UriItem(model.getNamespace() + "person/given_name", model), "e"));
            c1.addCondition(new PropertyValLikeCondition(new UriItem(model.getNamespace() + "person/given_name", model), "a"));
            
            filterCond.addCondition(c1);
    }

    private JPanel createTopPanel() {

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel instLabel = new JLabel("Instances");

        selectBox = new JComboBox();

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

    private Component createCenterPanel() {

        JScrollPane centerScrollPane = new JScrollPane();
        centerScrollPane.setBackground(Color.pink);

        JPanel centerPanel = new JPanel(new GridLayout(28, 2, 2, 25));
        centerPanel.setBackground(Color.green);

        for (int i = 0; i < 28; i++ ) {

            centerPanel.add(new JLabel("Right" + i));
            centerPanel.add(new JLabel("Left" + i));
        }

        centerScrollPane.getViewport().add(centerPanel);

        return centerScrollPane;
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
        }
    }


}
