package cz.zcu.kiv.eeg.semweb.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class DataPanel extends JPanel {


    public DataPanel() {

        setBackground(Color.GREEN);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(Color.pink);

        JLabel instLabel = new JLabel("Instances");

        String [] values = {"http://cz.zcu.kiv.eeg#person/researcher/default_group", "B"};
        JComboBox selectBox = new JComboBox(values);

        JButton filterBt = new JButton("Set filter");

        topPanel.add(instLabel);
        topPanel.add(selectBox);
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


}
