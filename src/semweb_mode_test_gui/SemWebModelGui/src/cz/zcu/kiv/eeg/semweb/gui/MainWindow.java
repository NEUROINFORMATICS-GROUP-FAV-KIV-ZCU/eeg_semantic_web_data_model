package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class MainWindow extends JFrame {

    private PortalModel model;
    private JComboBox selectBox;
    private JLabel label;
    private TreeNodeSelectionListener tnsl;


    public MainWindow (PortalModel model) throws NonExistingUriNodeException, ConversionException {

        this.model = model;

	setTitle("EEG/ERP portal model visualizer");
	setSize( 700, 500 );
        setLocation(640, 480);
	setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel listPanel = new JPanel();
        listPanel.setBackground(Color.green);

        String [] values = {"A", "B"};
        selectBox = new JComboBox(values);
        listPanel.add(selectBox);
        
        label = new JLabel("Nic");
        listPanel.add(label);

        tnsl = new TreeNodeSelectionListener(selectBox, label, model);

        setLayout(new BorderLayout());

        add(new ClassTreePanel(model, tnsl), BorderLayout.WEST);
        add(listPanel, BorderLayout.CENTER);
    }

    public void updateSelectBox() {

        selectBox.removeAllItems();
        selectBox.addItem("Fildas");
    }

    


}
