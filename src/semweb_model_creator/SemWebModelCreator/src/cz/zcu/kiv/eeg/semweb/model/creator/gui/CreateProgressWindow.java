package cz.zcu.kiv.eeg.semweb.model.creator.gui;

import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.OracleDbConnector;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.VirtuosoDbConnector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 * Show model creating progress window
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class CreateProgressWindow extends JFrame implements ActionListener {

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
    private JFrame main;
    private JProgressBar progress;
    private JButton closeButton;

    public CreateProgressWindow(JComboBox conn, JTextField db, JTextField model, JTextField file,
            JTextField user, JPasswordField pwd, JTextField modPref, JTextField tblPref, JCheckBox simpleData, JSpinner size, JCheckBox remove, JFrame main) {

        this.connectionType = conn;
        this.dbUri = db;
        this.modelName = model;
        this.fileName = file;
        this.username = user;
        this.password = pwd;
        this.defaultPrefix = modPref;
        this.defaultTablePrefix = tblPref;
        this.simpleData = simpleData;
        this.size = size;
        this.removeModel = remove;

        this.main = main;
    }

    /**
     * Starts new thred wiht model creator
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        openWindow();

        Thread t = new Thread(new CreatorThread(this));
        t.start();
    }

    /**
     * Show progress dialog
     */
    private void openWindow() {

        setTitle("Data creating progress");
        setSize(270, 140);
        setLocation(740, 300);
        setBackground(Color.gray);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        progress = new JProgressBar(0, 10);
        progress.setPreferredSize(new Dimension(220, 25));
        progress.setStringPainted(true);

        closeButton = new JButton("Close");
        closeButton.setEnabled(false);
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                main.dispose();
                dispose();
            }
        });

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        add(progress);
        add(closeButton);

        setVisible(true);

        main.setEnabled(false);
    }

    /**
     *
     * Update actual progress
     *
     * @param text progress text
     * @param value progress percent value
     * @param closeB true if process finished
     */
    public void updateStatus(String text, int value, boolean closeB) {
        progress.setString(text);
        progress.setValue(value);
        closeButton.setEnabled(closeB);
    }

    /**
     * Get databse connector
     *
     * @return databse connector
     */
    public DbConnector getConnection() {

        if (connectionType.getSelectedItem().toString().equals("Virtuoso")) {
            return new VirtuosoDbConnector(modelName.getText(), dbUri.getText(), username.getText(), password.getText());
        } else {
            return new OracleDbConnector(modelName.getText(), dbUri.getText(), username.getText(), password.getText());
        }
    }

    public String getDefaultPrefix() {
        return defaultPrefix.getText();
    }

    public String getDefaultTablePrefix() {
        return defaultTablePrefix.getText();
    }

    public String getFileName() {
        return fileName.getText();
    }

    public String getModelName() {
        return modelName.getText();
    }

    public boolean getRemoveModel() {
        return removeModel.isSelected();
    }

    public boolean getSimpleData() {
        return simpleData.isSelected();
    }

    public int getDataSize() {
        return (Integer) size.getValue();
    }
}
