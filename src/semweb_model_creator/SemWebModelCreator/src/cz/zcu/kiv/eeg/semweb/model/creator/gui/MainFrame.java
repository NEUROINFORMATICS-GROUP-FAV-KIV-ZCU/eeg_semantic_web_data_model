package cz.zcu.kiv.eeg.semweb.model.creator.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Main frame window contains input fields to specify connection and model creating params
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class MainFrame extends JFrame {

    private JButton createButton;
    private JButton cancelButton;
    private JComboBox connectionType; //Oracle or Virtuoso DB connection
    private JTextField dbUri;
    private JTextField modelName;
    private JTextField fileName;
    private JTextField username;
    private JPasswordField password;
    private JTextField defaultPrefix;
    private JTextField defaultTablePrefix;
    private JCheckBox simpleData;
    private JSpinner size;
    private JCheckBox removeModel;

    public MainFrame() {

        setTitle("EEG/ERP portal model creator");
        setSize(300, 450);
        setLocation(740, 300);
        setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel textFiledsPanel = new JPanel();
        setTextFieldPanel(textFiledsPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");

        setCancelButton();
        setOkButton();

        buttonPanel.add(createButton);
        buttonPanel.add(new JLabel("       "));
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(textFiledsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Set input components
     *
     * @param textFiledsPanel
     */
    private void setTextFieldPanel(JPanel textFiledsPanel) {

        connectionType = new JComboBox(new String[]{"Virtuoso", "Oracle"});
        dbUri = new JTextField("jdbc:virtuoso://localhost:1111");
        modelName = new JTextField("model_semweb_a");

        fileName = new JTextField("portalModel.xml");
        fileName.addMouseListener(new InputFileFieldListener(fileName));

        username = new JTextField("dba");
        password = new JPasswordField("dba");
        defaultPrefix = new JTextField("http://cz.zcu.kiv.eeg#");
        defaultTablePrefix = new JTextField("EEG_");

        size = new JSpinner(new SpinnerNumberModel(5, 1, 8, 1));
        size.setEnabled(false);

        simpleData = new JCheckBox();
        simpleData.addActionListener(new TestDataCheckListener(simpleData, size));

        removeModel = new JCheckBox();

        textFiledsPanel.setLayout(new GridLayout(11, 1, 0, 5));

        textFiledsPanel.add(wrapComponent(new JLabel("Connection type"), connectionType));
        textFiledsPanel.add(wrapComponent(new JLabel("  Database URI   "), dbUri));

        textFiledsPanel.add(wrapComponent(new JLabel("  Model name     "), modelName));
        textFiledsPanel.add(wrapComponent(new JLabel("  Model file          "), fileName));
        textFiledsPanel.add(wrapComponent(new JLabel("  Username        "), username));
        textFiledsPanel.add(wrapComponent(new JLabel("  Password        "), password));

        textFiledsPanel.add(wrapComponent(new JLabel("  Namespace     "), defaultPrefix));
        textFiledsPanel.add(wrapComponent(new JLabel("  Table prefix     "), defaultTablePrefix));

        textFiledsPanel.add(wrapComponent(new JLabel("Create simple data"), simpleData));

        textFiledsPanel.add(wrapComponent(new JLabel("Simple data size"), size));

        textFiledsPanel.add(wrapComponent(new JLabel("Remove model"), removeModel));
    }

    /**
     * Wrap iput component with JLabel to get simple selfDescribing panel
     *
     * @param label Component label
     * @param c
     * @return
     */
    private Component wrapComponent(JLabel label, Component c) {

        if (c.getClass().equals(JTextField.class) || c.getClass().equals(JPasswordField.class)) {
            JPanel wrapper = new JPanel(new BorderLayout(10, 0));

            wrapper.add(label, BorderLayout.WEST);
            wrapper.add(c, BorderLayout.CENTER);
            return wrapper;
        } else {
            JPanel wrapper = new JPanel(new FlowLayout());

            wrapper.add(label, BorderLayout.WEST);
            wrapper.add(c, BorderLayout.EAST);

            JPanel p2 = new JPanel(new BorderLayout());

            p2.add(wrapper, BorderLayout.WEST);
            return p2;
        }
    }

    /**
     * Close model creator
     */
    private void setCancelButton() {

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Starts model creating
     */
    private void setOkButton() {
        createButton.addActionListener(new CreateProgressWindow(connectionType, dbUri,
                modelName, fileName, username, password, defaultPrefix, defaultTablePrefix, simpleData, size, removeModel, this));
    }
}
