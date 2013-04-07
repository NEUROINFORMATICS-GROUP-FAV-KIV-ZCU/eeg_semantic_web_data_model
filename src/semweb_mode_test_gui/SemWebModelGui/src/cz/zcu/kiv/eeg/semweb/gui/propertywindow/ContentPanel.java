package cz.zcu.kiv.eeg.semweb.gui.propertywindow;

import com.hp.hpl.jena.shared.AddDeniedException;
import cz.zcu.kiv.eeg.semweb.model.api.DataType;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

    private ActionListener addBtnListener;
    private Component setterComponent;
    private JLabel setterDescription;


    private String individualNode;

    private static final Logger logger = Logger.getLogger(ContentPanel.class);

    public ContentPanel(PortalModel model, AddPropertyWindow mw, String individualNode) {

        this.model = model;
        this.mw = mw;
        this.actualNode = null;
        this.individualNode = individualNode;


        addBtnListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        };

        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel label = new JLabel("Set value");
        label.setFont(new Font("Arial", Font.BOLD, 15));

        topPanel.add(label);
        return topPanel;
    }

    private JPanel createCenterPanel() {

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(240, 240, 240));

        if (actualNode != null) {
            setSetterComponent();
        } else {
            setterComponent = new JLabel();
            setterDescription = new JLabel();
        }

        JPanel setterComponentWrapper = new JPanel(new FlowLayout());
        setterComponentWrapper.setBackground(new Color(240, 240, 240));
        setterComponentWrapper.add(setterDescription);
        setterComponentWrapper.add(setterComponent);

        topPanel.add(setterComponentWrapper);

        return topPanel;
    }

    /**
     * Bottom panel with Add and Cancel buttons
     *
     * @return JPanel
     */
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

        bottomPanel.add(addPropBt);
        bottomPanel.add(cancelBt);

        return bottomPanel;
    }


    /**
     * New property node is selected
     *
     * @param node Node URI
     */
    public void nodeSelected(String node) {

        if (node != null) {
            actualNode = node;

            this.removeAll();

            add(createTopPanel(), BorderLayout.NORTH);
            add(createBottomPanel(), BorderLayout.SOUTH);
            add(createCenterPanel(), BorderLayout.CENTER);

            validate();
            repaint();
        }
    }

    /**
     * Set input component type by range of selected property
     */
    public void setSetterComponent() {

        setterDescription = new JLabel(model.getPropertyRange(actualNode).name());

        if (model.getPropertyRange(actualNode).equals(DataType.BOOLEAN_TYPE)) {
            setterComponent = new JCheckBox();
            setAction(DataType.BOOLEAN_TYPE);

        } else if (model.getPropertyRange(actualNode).equals(DataType.DATE_TIME_TYPE)) {
            setterComponent = new JTextField("2000-01-01T00:00:00", 15);
            setAction(DataType.DATE_TIME_TYPE);

        } else if (model.getPropertyRange(actualNode).equals(DataType.DATE_TYPE)) {
            setterComponent = new JTextField("2000-01-01", 15);
            setAction(DataType.DATE_TYPE);

        } else if (model.getPropertyRange(actualNode).equals(DataType.DOUBLE_TYPE)) {
            setterComponent = new JTextField(10);
            setAction(DataType.DOUBLE_TYPE);

        } else if (model.getPropertyRange(actualNode).equals(DataType.FLOAT_TYPE)) {
            setterComponent = new JTextField(10);
            setAction(DataType.FLOAT_TYPE);
        } else if (model.getPropertyRange(actualNode).equals(DataType.INTEGER_TYPE)) {
            setterComponent = new JTextField(10);
            setAction(DataType.INTEGER_TYPE);
        } else if (model.getPropertyRange(actualNode).equals(DataType.LONG_TYPE)) {
            setterComponent = new JTextField(10);
            setAction(DataType.LONG_TYPE);

        } else if (model.getPropertyRange(actualNode).equals(DataType.STRING_TYPE)) {    
            setterComponent = new JTextField(20);
            setAction(DataType.STRING_TYPE);

        } else if (model.getPropertyRange(actualNode).equals(DataType.TIME_TYPE)) {
            setterComponent = new JTextField("00:00:00", 15);
            setAction(DataType.TIME_TYPE);

        } else if (model.getPropertyRange(actualNode).equals(DataType.URI_TYPE)) {
            JComboBox individualsList = new JComboBox();
            try {
                List<Item> rangeIndividuals = model.listInstance(model.getPropertyRangeUri(actualNode), null);

                for (Item item: rangeIndividuals) {
                    individualsList.addItem(item.getAsUri().getUri());
                }
                setterComponent = individualsList;
            } catch (Exception ex) {
                logger.error("None existing node", ex);
            }
            setAction(DataType.URI_TYPE);
        }
    }

    private void setAction(final DataType dt) {

        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    UriItem individual = model.getIndividual(individualNode).getAsUri();

                    if (dt.equals(DataType.BOOLEAN_TYPE)) {
                        JCheckBox cb = (JCheckBox) setterComponent;
                        individual.addPropertyValue(actualNode, cb.isSelected());

                    } else if (dt.equals(DataType.DATE_TIME_TYPE) || dt.equals(DataType.DATE_TYPE) || dt.equals(DataType.TIME_TYPE)) {
                        JTextField tf = (JTextField) setterComponent;

                        if (!parseDateTime(tf.getText(), dt, individual)) {
                            return;
                        }

                    } else if (dt.equals(DataType.FLOAT_TYPE)) {
                        JTextField tf = (JTextField) setterComponent;
                        try {
                            Float dblVal = Float.valueOf(tf.getText());
                            individual.addPropertyValue(actualNode, dblVal);
                        } catch (NumberFormatException ex) {
                            logger.error("Invalid data format, can not parse input as " + dt.name(), ex);
                            JOptionPane.showMessageDialog(mw, "Invalid data format, must be real number");
                            return;
                        }

                    } else if (dt.equals(DataType.DOUBLE_TYPE)) {
                        JTextField tf = (JTextField) setterComponent;
                        try {
                            Double dblVal = Double.valueOf(tf.getText());
                            individual.addPropertyValue(actualNode, dblVal);
                        } catch (NumberFormatException ex) {
                            logger.error("Invalid data format, can not parse input as " + dt.name(), ex);
                            JOptionPane.showMessageDialog(mw, "Invalid data format, must be real number");
                            return;
                        }

                    } else if (dt.equals(DataType.LONG_TYPE)) {
                        JTextField tf = (JTextField) setterComponent;
                        try {
                            Long longVal = Long.valueOf(tf.getText());
                            individual.addPropertyValue(actualNode, longVal);
                        } catch (NumberFormatException ex) {
                            logger.error("Invalid data format, can not parse input as " + dt.name(), ex);
                            JOptionPane.showMessageDialog(mw, "Invalid data format, must be integer number");
                            return;
                        }

                    } else if (dt.equals(DataType.INTEGER_TYPE)) {
                        JTextField tf = (JTextField) setterComponent;
                        try {
                            Integer longVal = Integer.valueOf(tf.getText());
                            individual.addPropertyValue(actualNode, longVal);
                        } catch (NumberFormatException ex) {
                            logger.error("Invalid data format, can not parse input as " + dt.name(), ex);
                            JOptionPane.showMessageDialog(mw, "Invalid data format, must be integer number");
                            return;
                        }

                    } else if (dt.equals(DataType.STRING_TYPE)) {
                        JTextField tf = (JTextField) setterComponent;
                        individual.addPropertyValue(actualNode, tf.getText());

                    } else if (dt.equals(DataType.URI_TYPE)) {
                        JComboBox cmbBox = (JComboBox) setterComponent;
                        individual.addPropertyValue(actualNode, cmbBox.getSelectedItem());
                    }
                    mw.closeAdder();
                } catch (Exception ex) {
                    logger.error("Invalid individual node", ex);
                }
            }
        };
        updateAddAction(al);
    }

    private boolean parseDateTime(String data, DataType type, UriItem individual) throws NonExistingUriNodeException {

        try {
            individual.addPropertyValue(actualNode, data);
            return true;
        }catch (AddDeniedException ex) {
            logger.error("Invalid data format, can not parse input as " + type.name(), ex);
            JOptionPane.showMessageDialog(mw, "Invalid data format, must be " + type.name());
            return false;
        }
    }


    private void updateAddAction(ActionListener al) {
        addPropBt.removeActionListener(addBtnListener);
        addPropBt.addActionListener(al);
        addBtnListener = al;
    }
}
