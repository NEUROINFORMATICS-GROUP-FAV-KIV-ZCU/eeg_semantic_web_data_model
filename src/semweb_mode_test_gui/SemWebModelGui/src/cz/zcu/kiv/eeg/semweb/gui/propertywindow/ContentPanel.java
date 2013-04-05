package cz.zcu.kiv.eeg.semweb.gui.propertywindow;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import cz.zcu.kiv.eeg.semweb.gui.filter.MainOrListFilterWindow;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ContentPanel extends JPanel {

    private PortalModel model;
    private AddPropertyWindow mw;

    private String actualNode;

    private JButton addPropBt;
    private JButton cancelBt;

    private JLabel rangeLabel;


    private static final Logger logger = Logger.getLogger(ContentPanel.class);

    public ContentPanel(PortalModel model, AddPropertyWindow mw) {

        this.model = model;
        this.mw = mw;
        this.actualNode = null;

        setLayout(new BorderLayout());

        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createCenterPanel() {

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel instLabel = new JLabel("Value");
        System.out.println("Act node: " + actualNode);
        if (actualNode != null) {
            rangeLabel = new JLabel(model.getPropertyRange(actualNode).name());
        } else {
            rangeLabel = new JLabel();
        }

//
//        selectBox = new JComboBox();
//
//        selectBox.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                updateSelectedIndividual();
//            }
//        });
//
//        JButton updateBt = new JButton("Update");
//        updateBt.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                nodeSelected(actualNode);
//            }
//        });
//
//
//        JButton filterBt = new JButton("Set filter");
//        filterBt.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                new MainOrListFilterWindow(model, mw, filterCond);
//            }
//        });

        topPanel.add(instLabel);
        topPanel.add(rangeLabel);

        return topPanel;
    }

    private JPanel createBottomPanel() {

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.YELLOW);

        addPropBt = new JButton("Add");
        cancelBt = new JButton("Cancel");

        cancelBt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                mw.closeAdder();
            }
        });

//        addPropBt.setEnabled(false);
//        cancelBt.setEnabled(false);
//
//        addPropBt.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                createNewInstance();
//            }
//        });
//
//        cancelBt.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                addProperty();
//            }
//        });

        bottomPanel.add(addPropBt);
        bottomPanel.add(cancelBt);

        return bottomPanel;
    }


    public void nodeSelected(String node) {

        if (node != null) {
            actualNode = node;

            rangeLabel = new JLabel(model.getPropertyRange(actualNode).name());

            this.removeAll();

            add(createCenterPanel(), BorderLayout.CENTER);
            add(createBottomPanel(), BorderLayout.SOUTH);

            validate();
            repaint();

            //TODO update value target by range
        }
    }
}
