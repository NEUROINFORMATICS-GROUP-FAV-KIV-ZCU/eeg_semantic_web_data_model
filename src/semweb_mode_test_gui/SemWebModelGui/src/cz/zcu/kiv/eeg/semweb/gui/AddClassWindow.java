package cz.zcu.kiv.eeg.semweb.gui;

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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class AddClassWindow extends JFrame implements ActionListener {

    private JButton addButton;
    private JButton cancelButton;

    private JCheckBox inherited; //has superClass
    private JLabel superClassLabel;

    private JTextField name;
    private JTextField description;


    private ClassTreePanel root;
    
    public AddClassWindow (ClassTreePanel root) {

        this.root = root;

        setTitle("Add class");
	setSize(420, 160 );
        setLocation(740, 300);
	setBackground(Color.gray);
        setResizable(false);


        setLayout(new BorderLayout());
        add(createPropertyPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        this.setVisible(true);
        root.setMainWidnow(false);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");

        setCancelButton();
        setAddButton();

        buttonPanel.add(addButton);
        buttonPanel.add(new JLabel("       "));
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }


    private JPanel createPropertyPanel() {

        inherited = new JCheckBox();
        name = new JTextField(root.getModel().getNamespace());
        description = new JTextField();

        JPanel propertyPanel = new JPanel(new GridLayout(3, 1, 0, 5));

        if (root.getSelectedNode() != null) {
            superClassLabel = new JLabel("SuperClass:  " + root.getSelectedNode());
        }else {
            superClassLabel = new JLabel("SuperClass:");
            inherited.setEnabled(false);
        }


        propertyPanel.add(wrapComponent(superClassLabel, inherited));
        propertyPanel.add(wrapComponent(new JLabel("  Name          "), name));
        propertyPanel.add(wrapComponent(new JLabel("  Description"), description));

        return propertyPanel;
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
                root.setMainWidnow(true);
                dispose();
            }
        });
    }

    private void setAddButton() {
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                
                if (inherited.isSelected() && root.getSelectedNode() != null) {
                    root.getModel().createClass(name.getText().trim(), root.getSelectedNode());
                }else {
                    root.getModel().createClass(name.getText().trim(), null);
                }

                root.getModel().updateClassDescription(name.getText().trim(), description.getText().trim());
                
                root.updateTree();
                root.setMainWidnow(true);
                dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {

        
    }
}
