package cz.zcu.kiv.eeg.semweb.gui;

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
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PropertyListPanel extends JScrollPane{

    private PortalModel model;
    private JPanel centerPanel;
    private DataPanel dtPanel;


    private static final Logger logger = Logger.getLogger(PropertyListPanel.class);

    public PropertyListPanel(PortalModel model, DataPanel dtPanel) {
        this.model = model;
        this.dtPanel = dtPanel;

        createCenterPanel();
    }


    private void createCenterPanel() {

        centerPanel = new JPanel(new GridLayout(28, 2, 2, 25));

        this.getViewport().add(centerPanel);
    }

    public void updateData(final String individualUri) {

        centerPanel.removeAll();

        JPanel linePanel;

        int propCount = 0;

        if (individualUri != null) {
            try {
                Item parent = model.getIndividual(individualUri);
                
                List<Item> props = parent.getAsUri().listProperties();

                for (Item property: props) {

                    for (Item propertyVal: parent.getAsUri().listPropertyVal(property.getAsUri())) {

                        propCount++;

                        linePanel = new JPanel();

                        linePanel.add(createUriLabel(property.getAsUri().getUri()));

                        if (propertyVal.isLiteral()) {
                            linePanel.add(getLiteralComp(propertyVal.getAsLiteral()));
                        }else {
                            linePanel.add(getUriComp(propertyVal.getAsUri(), parent.getAsUri(), property.getAsUri().getUri()));
                        }

                        centerPanel.add(linePanel);
                    }
                }


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
                        
                    }catch (SQLException ex) {
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
            }catch (ParseException ex2) {
                logger.error("Can not parse data", ex2);
            }
        }
        validate();
        repaint();
    }

    private JLabel createUriLabel(String uri) {

        JLabel label = new JLabel(uri);
        label.setToolTipText(model.getPropertyDescription(uri));

        return label;
    }

    private JPanel getLiteralComp(LiteralItem lit) {

        JTextField litTf = new JTextField(lit.toString());
        litTf.setPreferredSize(new Dimension(250, 30));
        //TODO add focusLost listener to update data - add ConversionController

        litTf.addKeyListener(new TextFiledListenter(lit, litTf));


        JPanel parent = new JPanel(new FlowLayout());
        parent.add(litTf);

        return parent;
    }

    private JPanel getUriComp(final UriItem uri, final UriItem parentNode, final String predicate) {
        
        try {
            String actualObj = null;
            
            List<String> individualsNames = new ArrayList<String>();
        
            List<Item> insts = model.listInstance(model.getIndividualParentClass(uri.getUri()), null);


            for (Item indv: insts) {

                String in = indv.getAsUri().getUri();
                individualsNames.add(in);

                if (in.equals(uri.getUri())) {
                    actualObj = in;
                }
            }

            final JComboBox comb = new JComboBox(individualsNames.toArray());
            comb.setSelectedItem(actualObj);
            comb.addActionListener(new ActionListener() {

                private String oldVal = uri.getUri();

                public void actionPerformed(ActionEvent e) {
                    updateIndProperty(parentNode, predicate, oldVal, comb.getSelectedItem().toString());
                }
            });

            JButton goBtn = new JButton("Go");
            goBtn.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    goToInd(comb.getSelectedItem().toString());
                }
            });


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

    private void updateIndProperty(UriItem parent, String property, String oldVal, String newVal) {

        parent.updatePropertyValue(property, oldVal, newVal);
    }

    private void updateLiteral(LiteralItem lit, Object value) {
       lit.updateValue(value);
    }


    public void goToInd(String uri) {
        updateData(uri);
        dtPanel.setSelectedNode(uri);
    }

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
                updateLiteral(li, tf.getText());
                centerPanel.requestFocus();
            }
        }

        public void keyReleased(KeyEvent e) {
        }

    }

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
