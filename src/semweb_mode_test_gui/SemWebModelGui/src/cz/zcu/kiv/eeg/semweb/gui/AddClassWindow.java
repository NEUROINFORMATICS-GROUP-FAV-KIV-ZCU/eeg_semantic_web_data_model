package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.gui.util.ComponentWrapper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Adding new classes to data model
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class AddClassWindow extends JFrame {

    private JButton addButton;
    private JButton cancelButton;

    private JCheckBox inheritedChckBox; //To permit inharitnace of superClass or not
    private JLabel superClassLabel; //Name of available superclass (by selected class in ClassSelector Jtree)

    private JTextField name; //class name
    private JTextField description; //class description (optional)

    private ClassTreePanel rootClassTree; //ClassTreePanel root Panel
    
    public AddClassWindow (ClassTreePanel root) {

        this.rootClassTree = root;

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

    /**
     * Create button panel to create new class or cancel operation
     *
     * @return Panel
     */
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


    /**
     * Create panel containing TextFields to set new class Name specification
     *
     * @return Panel
     */
    private JPanel createPropertyPanel() {

        inheritedChckBox = new JCheckBox();
        name = new JTextField(rootClassTree.getModel().getNamespace());
        description = new JTextField();

        JPanel propertyPanel = new JPanel(new GridLayout(3, 1, 0, 5));

        if (rootClassTree.getSelectedNode() != null) {
            superClassLabel = new JLabel("SuperClass:  " + rootClassTree.getSelectedNode());
        }else {
            superClassLabel = new JLabel("SuperClass:");
            inheritedChckBox.setEnabled(false);
        }

        propertyPanel.add(ComponentWrapper.wrapComponent(superClassLabel, inheritedChckBox));
        propertyPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Name          "), name));
        propertyPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Description"), description));

        return propertyPanel;
    }

    /**
     * Set cancel action
     */
    private void setCancelButton() {

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                rootClassTree.setMainWidnow(true);
                dispose();
            }
        });
    }

    /**
     * Set addClass action
     */
    private void setAddButton() {
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                
                if (inheritedChckBox.isSelected() && rootClassTree.getSelectedNode() != null) {
                    rootClassTree.getModel().createClass(name.getText().trim(), rootClassTree.getSelectedNode());
                }else {
                    rootClassTree.getModel().createClass(name.getText().trim(), null);
                }

                rootClassTree.getModel().updateClassDescription(name.getText().trim(), description.getText().trim());
                
                rootClassTree.updateTree();
                rootClassTree.setMainWidnow(true);
                dispose();
            }
        });
    }

 
}
