package cz.zcu.kiv.eeg.semweb.gui.propertywindow;

import cz.zcu.kiv.eeg.semweb.gui.propertywindow.ContentPanel;
import cz.zcu.kiv.eeg.semweb.gui.propertywindow.PropertyTreePanel;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileNotFoundException;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class AddPropertyWindow extends JFrame {

    private PortalModel model;
    private PropertyTreePanel treePanel;

    JFrame root;

    private static final Logger logger = Logger.getLogger(AddPropertyWindow.class);

    public AddPropertyWindow (PortalModel model, JFrame root, String node) {

        this.model = model;
        this.root = root;

	setTitle("Add property");
	setSize( 800, 500 );
        setLocation(640, 480);
	setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        ContentPanel dataPanel = new ContentPanel(model, this);

        treePanel = new PropertyTreePanel(model, dataPanel, this, node);


        setLayout(new BorderLayout());
        add(treePanel, BorderLayout.WEST);
        add(dataPanel, BorderLayout.CENTER);

        root.setEnabled(false);
        this.setVisible(true);
    }


    public void closeAdder () {
        root.setEnabled(true);
        this.dispose();
    }

}
