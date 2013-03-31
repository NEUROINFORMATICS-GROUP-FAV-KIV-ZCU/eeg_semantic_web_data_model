package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class MainWindow extends JFrame {

    private PortalModel model;
    private JComboBox selectBox;
    private JLabel label;
    private ClassTreePanel treePanel;


    public MainWindow (PortalModel model) throws NonExistingUriNodeException, ConversionException {

        this.model = model;

	setTitle("EEG/ERP portal model visualizer");
	setSize( 1000, 500 );
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


        DataPanel dataPanel = new DataPanel();

      

        treePanel = new ClassTreePanel(model, this);


        setLayout(new BorderLayout());

        add(treePanel, BorderLayout.WEST);
        //add(listPanel, BorderLayout.CENTER);
        add(dataPanel, BorderLayout.CENTER);



        JMenuBar mainMenu = new JMenuBar();

        JMenu menu = new JMenu("Menu");

        JMenuItem exportItem = new JMenuItem("Export model");
        JMenuItem exitItem = new JMenuItem("Exit");

        menu.add(exportItem);
        menu.add(exitItem);

        mainMenu.add(menu);

        setJMenuBar(mainMenu);

    }

    public void updateSelectBox() {

        selectBox.removeAllItems();
        selectBox.addItem("Fildas");
    }

    public PortalModel getModel() {
        return model;
    }



}
