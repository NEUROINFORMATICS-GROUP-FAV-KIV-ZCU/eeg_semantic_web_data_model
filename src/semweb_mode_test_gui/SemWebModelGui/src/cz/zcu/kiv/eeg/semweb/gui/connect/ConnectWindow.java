package cz.zcu.kiv.eeg.semweb.gui.connect;

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
         reasonerType = new JComboBox(new String[] {"Deep", "Shallow"});
         dbUri = new JTextField("jdbc:virtuoso://localhost:1111");
         modelName = new JTextField("model_semweb_a");

         username = new JTextField("dba");
         password = new JPasswordField("dba");
         defaultPrefix = new JTextField("http://cz.zcu.kiv.eeg#");
         defaultTablePrefix = new JTextField("EEG_");


         textFiledsPanel.setLayout(new GridLayout(8, 1, 0, 5));

         textFiledsPanel.add(wrapComponent(new JLabel("Connection type"), connectionType));
         textFiledsPanel.add(wrapComponent(new JLabel("Reasoner type   "), reasonerType));

         textFiledsPanel.add(wrapComponent(new JLabel("  Database URI   "), dbUri));
         textFiledsPanel.add(wrapComponent(new JLabel("  Model name     "), modelName));
         
         textFiledsPanel.add(wrapComponent(new JLabel("  Username        "), username));
         textFiledsPanel.add(wrapComponent(new JLabel("  Password        "), password));

         textFiledsPanel.add(wrapComponent(new JLabel("  Namespace     "), defaultPrefix));
         textFiledsPanel.add(wrapComponent(new JLabel("  Table prefix     "), defaultTablePrefix));
    }

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
