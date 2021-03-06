package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * Main window wrappes all components - contains TreeClassViewer and individual lister
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class MainWindow extends JFrame {

    private PortalModel model; //Portal data model API connect
    private static final Logger logger = Logger.getLogger(MainWindow.class);

    public MainWindow(PortalModel model) throws NonExistingUriNodeException, ConversionException {

        this.model = model;

        setTitle("EEG/ERP portal model visualizer");
        setSize(1000, 500);
        setLocation(640, 480);
        setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        DataPanel dataPanel = new DataPanel(model, this);
        ClassTreePanel treePanel = new ClassTreePanel(model, dataPanel, this);

        setLayout(new BorderLayout());
        add(treePanel, BorderLayout.WEST);
        add(dataPanel, BorderLayout.CENTER);

        //Menu containing exist and export items
        JMenuBar mainMenu = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem exportItem = new JMenuItem("Export model");
        JMenuItem exitItem = new JMenuItem("Exit");

        exportItem.addActionListener(new ActionListener() { //Export data

            public void actionPerformed(ActionEvent e) {
                exportData();
            }
        });

        exitItem.addActionListener(new ActionListener() { //Close application

            public void actionPerformed(ActionEvent e) {
                closeVisualizer();
            }
        });

        menu.add(exportItem);
        menu.add(exitItem);

        mainMenu.add(menu);
        setJMenuBar(mainMenu);

        addWindowListener(new WindowClosingListener() {

            @Override
            public void closeWindow() {
                closeVisualizer();
            }
        });
    }

    /**
     * Export data model to XML RDF syntax file
     */
    public void exportData() {

        JFileChooser saveChooser = new JFileChooser();

        int returnVal = saveChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                model.exportModel(saveChooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Export finished.");
            } catch (FileNotFoundException ex) {
                logger.error("Can not write to file", ex);
                JOptionPane.showMessageDialog(this, "Export failed.");
            }
        }
    }

    /**
     * Close visualizer window
     */
    public void closeVisualizer() {
        model.close();
        System.exit(0);
    }
}
