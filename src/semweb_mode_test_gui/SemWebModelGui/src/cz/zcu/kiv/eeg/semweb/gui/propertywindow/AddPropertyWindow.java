package cz.zcu.kiv.eeg.semweb.gui.propertywindow;

import cz.zcu.kiv.eeg.semweb.gui.DataPanel;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

/**
 * Property add window wrapper
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class AddPropertyWindow extends JFrame {

    DataPanel root;

    private static final Logger logger = Logger.getLogger(AddPropertyWindow.class);

    public AddPropertyWindow (PortalModel model, DataPanel root, String nodeIndividual) {

        this.root = root;

	setTitle("Add property");
	setSize( 900, 500 );
        setLocation(640, 480);
	setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ContentPanel dataPanel = new ContentPanel(model, this, nodeIndividual);

        PropertyTreePanel treePanel = new PropertyTreePanel(model, dataPanel, this, model.getIndividualParentClass(nodeIndividual));

        setLayout(new BorderLayout());
        add(treePanel, BorderLayout.WEST);
        add(dataPanel, BorderLayout.CENTER);

        root.setEnabled(false);
        this.setVisible(true);
    }

    /**
     * Close PropertyAdd window
     */
    public void closeAdder () {
        root.closePropertyAdderAndUpdate();
        this.dispose();
    }

}
