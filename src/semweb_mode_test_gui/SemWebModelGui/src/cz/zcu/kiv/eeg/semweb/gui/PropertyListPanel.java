package cz.zcu.kiv.eeg.semweb.gui;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.shared.AddDeniedException;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.LiteralItem;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/**
 * Panel containing all selected Individual set properties
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PropertyListPanel extends JScrollPane {

    private PortalModel model; //portal model API connector
    private JPanel centerPanel; //wrapping panel
    private DataPanel dtPanel; //parent wrapping component
    private static final Logger logger = Logger.getLogger(PropertyListPanel.class);

    public PropertyListPanel(PortalModel model, DataPanel dtPanel) {
        this.model = model;
        this.dtPanel = dtPanel;

        createCenterPanel();
    }

    /**
     * Create wrapping panel and set its size and layout
     */
    private void createCenterPanel() {
        centerPanel = new JPanel(new GridLayout(28, 2, 2, 25));
        this.getViewport().add(centerPanel);
    }

    /**
     * Update individual data when individual selected
     *
     * @param individualUri Individual URI
     */
    public void updateData(final String individualUri) {

        centerPanel.removeAll();
        JPanel linePanel;
        int propCount = 0; //found properties values

        // Set available properties
        if (individualUri != null) {
            try {
                Item parent = model.getIndividualByUri(individualUri);

                List<Item> props = parent.getAsUri().listProperties();

                for (Item property : props) {

                    for (Item propertyVal : parent.getAsUri().listPropertyVal(property.getAsUri())) {

                        propCount++;

                        linePanel = new JPanel();

                        linePanel.add(createUriLabel(property.getAsUri().getUri()));

                        if (propertyVal.isLiteral()) {
                            linePanel.add(getLiteralComp(propertyVal.getAsLiteral()));
                        } else {
                            linePanel.add(getUriComp(propertyVal.getAsUri(), parent.getAsUri(), property.getAsUri().getUri()));
                        }

                        centerPanel.add(linePanel);
                    }
                }

                //Set relational database table data
                if (model.hasIndividualTable(individualUri)) { //Table for file exists

                    try {
                        linePanel = new JPanel();
                        linePanel.add(new JLabel("Data file"));

                        if (model.hasIndividualFile(individualUri)) { //Individual already has a file

                            linePanel.add(getUpdDownFileComp(individualUri));

                        } else { //no file exists

                            JButton uploadBtn = new JButton("Upload");
                            uploadBtn.addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    uploadFile(individualUri);
                                }
                            });

                            linePanel.add(uploadBtn);
                        }
                        centerPanel.add(linePanel);

                    } catch (SQLException ex) {
                        logger.error("Table selecting error:", ex);
                    }

                    centerPanel.setLayout(new GridLayout(propCount + 1, 1, 2, 2));
                } else {
                    centerPanel.setLayout(new GridLayout(propCount, 1, 2, 2));
                }

            } catch (NonExistingUriNodeException ex) {
                logger.error("Can not find individual " + individualUri, ex);
            } catch (ConversionException ex2) {
                logger.error("Can not conver data", ex2);
            } catch (ParseException ex2) {
                logger.error("Can not parse data", ex2);
            }
        }
        validate();
        repaint();
    }

    /**
     * Property label component create
     *
     * @param uri Property URI
     * @return Property component
     */
    private JLabel createUriLabel(String uri) {

        JLabel label = new JLabel(uri);
        label.setToolTipText(model.getPropertyDescription(uri));

        return label;
    }

    /**
     * Literal component creator
     *
     * @param lit Source literal
     * @return Created component
     */
    private JPanel getLiteralComp(LiteralItem lit) {

        JComponent comp = null;

        if (lit.getXsdType().equals(XSDDatatype.XSDboolean.getURI())) {
            JCheckBox chckBox = new JCheckBox();
            Boolean value = (Boolean) lit.getValue();
            chckBox.setSelected(value);

            chckBox.addActionListener(new CheckBoxListenter(lit, chckBox));

            comp = chckBox;
        } else {
            JTextField litTf = new JTextField(lit.getValue().toString());
            litTf.setPreferredSize(new Dimension(250, 30));

            litTf.addKeyListener(new TextFiledListenter(lit, litTf));
            comp = litTf;
        }

        JPanel parent = new JPanel(new FlowLayout());
        parent.add(comp);

        return parent;
    }

    /**
     * Uri component creator
     *
     * @param uri Object URI
     * @param parentNode Parent Individual URI
     * @param predicate Predicate URI
     * @return Created component
     */
    private JPanel getUriComp(final UriItem uri, final UriItem parentNode, final String predicate) {

        try {
            String actualObj = null;
            List<String> individualsNames = new ArrayList<String>();
            List<Item> insts = model.listClassInstances(model.getIndividualParentClass(uri.getUri()), null);

            //Set available values by list of individuals of parentClass of object node
            for (Item indv : insts) {

                String in = indv.getAsUri().getUri();
                individualsNames.add(in);

                if (in.equals(uri.getUri())) {
                    actualObj = in;
                }
            }

            //Set comboBox listener to enable update data selecting new value
            final JComboBox comb = new JComboBox(individualsNames.toArray());
            comb.setSelectedItem(actualObj);
            comb.addActionListener(new ActionListener() {

                private String oldVal = uri.getUri();

                public void actionPerformed(ActionEvent e) {
                    parentNode.updatePropertyValue(predicate, oldVal, comb.getSelectedItem().toString());
                }
            });

            JButton goBtn = new JButton("Go"); //GoTo link to target individual button
            goBtn.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    goToInd(comb.getSelectedItem().toString());
                }
            });

            //return wrapped component
            JPanel parent = new JPanel(new FlowLayout());
            parent.add(comb);
            parent.add(goBtn);

            return parent;

        } catch (NonExistingUriNodeException ex) {
            logger.error("Can not find node with URI " + uri, ex);
        } catch (ConversionException ex) {
            logger.error("Can not conver data ", ex);
        }
        return new JPanel();
    }

    /**
     * Create component containing tble data buttons when table record present
     *
     * @param individual Source individual (subject)
     * @return JPanel
     */
    private JPanel getUpdDownFileComp(final String individual) {

        JButton dwnldBtn = new JButton("Download");
        JButton updateBtn = new JButton("Update");

        dwnldBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                downloadFile(individual);
            }
        });

        updateBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updateFile(individual);
            }
        });

        JPanel parent = new JPanel(new FlowLayout());
        parent.add(dwnldBtn);
        parent.add(updateBtn);

        return parent;
    }

    /**
     * GoTo action update view
     * @param uri
     */
    public void goToInd(String uri) {
        updateData(uri);
        dtPanel.setSelectedNode(uri);
    }

    /**
     * Literal value updating listener enables update data
     */
    private class TextFiledListenter implements KeyListener {

        private LiteralItem li;
        private JTextField tf;

        public TextFiledListenter(LiteralItem lit, JTextField tf) {
            this.li = lit;
            this.tf = tf;
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                try {
                    li.updateValue(tf.getText());
                    centerPanel.requestFocus();
                } catch (AddDeniedException ex) {
                    logger.error("Invalid datatype updated literatl node", ex);
                    JOptionPane.showMessageDialog(dtPanel.getRootFrame(), "Invalid data format");
                }
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    }

    /**
     * Literal value updating listener enables update boolean data
     */
    private class CheckBoxListenter implements ActionListener {

        private LiteralItem li;
        private JCheckBox chckBox;

        public CheckBoxListenter(LiteralItem lit, JCheckBox cb) {
            this.li = lit;
            this.chckBox = cb;
        }

        public void actionPerformed(ActionEvent e) {
            li.updateValue(new Boolean(chckBox.isSelected()));
        }
    }

    /**
     * Upload file to individuals table
     *
     * @param individual Source individual URI
     */
    private void uploadFile(String individual) {

        JFileChooser openChooser = new JFileChooser();

        int returnVal = openChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                model.uploadIndividualDataFile(individual, openChooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Upload finished");
                updateData(individual);
            } catch (Exception ex) {
                logger.error("Can not write to file", ex);
                JOptionPane.showMessageDialog(this, "Upload failed");
            }
        }

    }

    /**
     * Download individuals file from table
     *
     * @param individual Source individual URI
     */
    private void downloadFile(String individual) {

        JFileChooser saveChooser = new JFileChooser();

        int returnVal = saveChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                model.getIndividualDataFile(individual, saveChooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Download finished");
            } catch (Exception ex) {
                logger.error("Can not write to file", ex);
                JOptionPane.showMessageDialog(this, "Upload failed");
            }
        }
    }

    /**
     * Update individual table data file
     *
     * @param individual Target individual URI
     */
    private void updateFile(String individual) {

        JFileChooser openChooser = new JFileChooser();

        int returnVal = openChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                model.updateIndividualDataFile(individual, openChooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Update finished");
            } catch (Exception ex) {
                logger.error("Can not write to file", ex);
                JOptionPane.showMessageDialog(this, "Update failed");
            }
        }

    }
}
