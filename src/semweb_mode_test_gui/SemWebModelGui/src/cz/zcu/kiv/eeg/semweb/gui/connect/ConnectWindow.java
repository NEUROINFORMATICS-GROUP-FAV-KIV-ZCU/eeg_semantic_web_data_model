package cz.zcu.kiv.eeg.semweb.gui.connect;

import cz.zcu.kiv.eeg.semweb.gui.util.ComponentWrapper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConnectWindow extends JFrame {

    private JButton connectButton;
    private JButton cancelButton;

    private JComboBox connectionType; //Oracle or Virtuoso DB connection
    private JComboBox reasonerType; //Type of reasoner

    private JTextField dbUri;
    private JTextField modelName;

    private JTextField username;
    private JPasswordField password;

    private JTextField defaultPrefix;
    private JTextField defaultTablePrefix;

    

    public ConnectWindow () {

        setTitle("EEG/ERP portal model connector");
	setSize(320, 340 );
        setLocation(740, 300);
	setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel textFiledsPanel = new JPanel();
        setTextFieldPanel(textFiledsPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        connectButton = new JButton("Connect");
        cancelButton = new JButton("Cancel");

        setCancelButton();
        setConnectButton();

        buttonPanel.add(connectButton);
        buttonPanel.add(new JLabel("       "));
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(textFiledsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setTextFieldPanel(JPanel textFiledsPanel) {

         connectionType = new JComboBox(new String [] {"Virtuoso", "Oracle"});
         reasonerType = new JComboBox(new String[] {"Shallow", "Deep"});
         dbUri = new JTextField("jdbc:virtuoso://localhost:1111");
         modelName = new JTextField("model_semweb_a");

         username = new JTextField("dba");
         password = new JPasswordField("dba");
         defaultPrefix = new JTextField("http://cz.zcu.kiv.eeg#");
         defaultTablePrefix = new JTextField("EEG_");


         textFiledsPanel.setLayout(new GridLayout(8, 1, 0, 5));

         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("Connection type"), connectionType));
         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("Reasoner type   "), reasonerType));

         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Database URI  "), dbUri));
         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Model name     "), modelName));
         
         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Username        "), username));
         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Password         "), password));

         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Namespace     "), defaultPrefix));
         textFiledsPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Table prefix       "), defaultTablePrefix));
    }

    private void setCancelButton() {

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void setConnectButton() {
        connectButton.addActionListener(new ConnectProgressWindow(connectionType, reasonerType, dbUri,
                modelName, username, password, defaultPrefix, defaultTablePrefix, this));
        }


}
