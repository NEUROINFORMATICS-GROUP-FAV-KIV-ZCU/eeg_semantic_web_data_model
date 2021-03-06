package cz.zcu.kiv.eeg.semweb.gui.connect;

import com.hp.hpl.jena.ontology.OntModelSpec;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.OracleDbConnector;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.VirtuosoDbConnector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 * Progress window dialog show actual status of database connecting and model loading
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConnectProgressWindow extends JFrame implements ActionListener {

    private JComboBox connectionType; //Oracle or Virtuoso DB connection
    private JComboBox reasonerType;
    private JTextField dbUri;
    private JTextField modelName;
    private JTextField username;
    private JPasswordField password;
    private JTextField defaultPrefix;
    private JTextField defaultTablePrefix;
    private JFrame main;
    private JProgressBar progress;
    private JButton closeButton;

    /**
     * Constructor init progress window data
     *
     * @param conn Connection type
     * @param reas reasoner type
     * @param db databse URL
     * @param model model name
     * @param user username
     * @param pwd password
     * @param modPref semWeb model default URI prefix
     * @param tblPref default table prefix
     * @param main parent window
     */
    public ConnectProgressWindow(JComboBox conn, JComboBox reas, JTextField db, JTextField model,
            JTextField user, JPasswordField pwd, JTextField modPref, JTextField tblPref, JFrame main) {

        this.connectionType = conn;
        this.reasonerType = reas;
        this.dbUri = db;
        this.modelName = model;

        this.username = user;
        this.password = pwd;
        this.defaultPrefix = modPref;
        this.defaultTablePrefix = tblPref;

        this.main = main;
    }

    /**
     * Start database and model connecting
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        openWindow();

        Thread t = new Thread(new ConnectorThread(this));
        t.start();
    }

    /**
     * Show progress disalog
     */
    private void openWindow() {

        setTitle("Model opening progress");
        setSize(270, 140);
        setLocation(740, 300);
        setBackground(Color.gray);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        progress = new JProgressBar(0, 10);
        progress.setPreferredSize(new Dimension(220, 25));
        progress.setStringPainted(true);

        closeButton = new JButton("Cancel");
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        add(progress);
        add(closeButton);

        setVisible(true);

        main.setEnabled(false);
    }

    /**
     * Update progress status
     *
     * @param text status text
     * @param value status percentil status
     */
    public void updateStatus(String text, int value) {
        progress.setString(text);
        progress.setValue(value);
    }

    /**
     * Get database connector
     *
     * @return connector
     */
    public DbConnector getConnection() {

        if (connectionType.getSelectedItem().toString().equals("Virtuoso")) {
            return new VirtuosoDbConnector(modelName.getText(), dbUri.getText(), username.getText(), password.getText());
        } else {
            return new OracleDbConnector(modelName.getText(), dbUri.getText(), username.getText(), password.getText());
        }
    }

    /**
     * Get model reasoner
     *
     * @return reasoner
     */
    public OntModelSpec getReasoner() {

        if (reasonerType.getSelectedItem().toString().equals("Shallow")) {
            return OntModelSpec.RDFS_MEM;
        } else {
            return OntModelSpec.OWL_MEM_RDFS_INF;
        }
    }

    public String getDefaultPrefix() {
        return defaultPrefix.getText();
    }

    public String getDefaultTablePrefix() {
        return defaultTablePrefix.getText();
    }

    public String getModelName() {
        return modelName.getText();
    }

    public void closeFrames() {
        main.dispose();
        this.dispose();
    }

    /**
     * Reconnect model when previous connection was not successfull
     */
    public void reconnect() {
        main.setEnabled(true);
        this.dispose();
    }
}
