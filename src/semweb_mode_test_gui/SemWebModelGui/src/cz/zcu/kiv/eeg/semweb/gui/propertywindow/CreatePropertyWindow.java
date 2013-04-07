package cz.zcu.kiv.eeg.semweb.gui.propertywindow;

import cz.zcu.kiv.eeg.semweb.gui.WindowClosingListener;
import cz.zcu.kiv.eeg.semweb.gui.util.ComponentWrapper;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/**
 * Adding new classes to data model
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class CreatePropertyWindow extends JFrame {

    private JButton addButton;
    private JButton cancelButton;

    private JTextField name; //property name
    private JComboBox range; //property range
    private JTextField description; //class description (optional)

    private String domainUri;

    private PropertyTreePanel rootPropertyTree; //ClassTreePanel root Panel

    private static final Logger logger = Logger.getLogger(CreatePropertyWindow.class);

    public CreatePropertyWindow (PropertyTreePanel root, String domainUri) {

        this.rootPropertyTree = root;
        this.domainUri = domainUri;

        setTitle("Add Property");
	setSize(420, 170 );
        setLocation(740, 300);
	setBackground(Color.gray);
        setResizable(false);


        setLayout(new BorderLayout());
        add(createSettingPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        this.setVisible(true);
        root.setMainWidnow(false);

        addWindowListener(new WindowClosingListener() {
            @Override
            public void closeWindow() {
                rootPropertyTree.setMainWidnow(true);
                dispose();
            }
        });
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
    private JPanel createSettingPanel() {

        
        name = new JTextField(rootPropertyTree.getModel().getNamespace());
        description = new JTextField();

        range =  new JComboBox();
         
        List<String> ranges = rootPropertyTree.getModel().listAvailableRanges();

        for (String rangeItem: ranges) {
            range.addItem(rangeItem);
        }

        JPanel propertyPanel = new JPanel(new GridLayout(3, 1, 0, 5));


        propertyPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Name          "), name));
        propertyPanel.add(ComponentWrapper.wrapComponent(new JLabel(" Range         "), range));
        propertyPanel.add(ComponentWrapper.wrapComponent(new JLabel("  Description"), description));

        return propertyPanel;
    }

    /**
     * Set cancel action
     */
    private void setCancelButton() {

        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                rootPropertyTree.setMainWidnow(true);
                dispose();
            }
        });
    }

    /**
     * Set addProperty action
     */
    private void setAddButton() {
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    rootPropertyTree.getModel().createProperty(name.getText(), domainUri, range.getSelectedItem().toString(), null, description.getText());
                } catch (NonExistingUriNodeException ex) {
                    logger.error("Can not create property.", ex);
                    JOptionPane.showMessageDialog(rootPropertyTree, "Can not create new property");
                }

                rootPropertyTree.updateTree();
                rootPropertyTree.setMainWidnow(true);
                dispose();
            }
        });
    }


}
