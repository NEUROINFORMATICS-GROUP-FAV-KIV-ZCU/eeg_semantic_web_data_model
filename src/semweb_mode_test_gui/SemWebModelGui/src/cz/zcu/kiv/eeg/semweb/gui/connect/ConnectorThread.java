package cz.zcu.kiv.eeg.semweb.gui.connect;

import cz.zcu.kiv.eeg.semweb.gui.MainWindow;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import javax.swing.JOptionPane;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConnectorThread implements Runnable {

    private ConnectProgressWindow root;

    public ConnectorThread(ConnectProgressWindow root) {
        this.root = root;
    }

    public void run() {
       connectModel();
    }

     private void connectModel() {

        this.root.updateStatus("Connecting model", 1);

        DbConnector connector = root.getConnection();
        PortalModel model = new PortalModel(connector, root.getDefaultPrefix(), root.getDefaultTablePrefix(), root.getReasoner());

        if (!model.connect()) {
            this.root.updateStatus("Connecting failed", 1);
            JOptionPane.showMessageDialog(root, "Can not connect database", "Connection error", JOptionPane.ERROR_MESSAGE);
            root.reconnect();
        }

        this.root.updateStatus("Connection estabilished", 2);
        this.root.updateStatus("Loading model", 3);
        
        MainWindow mw = null;
        
        try {
            mw = new MainWindow(model);
        } catch (NonExistingUriNodeException ex) {
            ex.printStackTrace();
        } catch (ConversionException ex) {
            ex.printStackTrace();
        }

        this.root.updateStatus("Loading model", 8);
        mw.getModel().getClassDescription(root.getDefaultTablePrefix() + "person");

        this.root.updateStatus("Model loaded.", 10);

        mw.setVisible(true);
        root.closeFrames();
    }
}
