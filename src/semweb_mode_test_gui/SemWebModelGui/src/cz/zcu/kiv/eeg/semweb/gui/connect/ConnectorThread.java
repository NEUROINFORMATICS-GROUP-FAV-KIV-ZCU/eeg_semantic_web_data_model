package cz.zcu.kiv.eeg.semweb.gui.connect;

import cz.zcu.kiv.eeg.semweb.gui.MainWindow;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * Connector threa connects model and set actual connecting progres status to
 * to status dialog window
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConnectorThread implements Runnable {

    private ConnectProgressWindow root;
    private static final Logger logger = Logger.getLogger(ConnectorThread.class);

    /**
     * Create connector thread
     *
     * @param root progress window instance
     */
    public ConnectorThread(ConnectProgressWindow root) {
        this.root = root;
    }

    /**
     * Connect model
     */
    public void run() {
        connectModel();
    }

    /**
     * Connect model and update connecting progress dialog
     */
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
            logger.error("Model connecting error: ", ex);
        } catch (ConversionException ex) {
            logger.error("Model connecting error: ", ex);
        }

        this.root.updateStatus("Loading model", 8);

        this.root.updateStatus("Model loaded.", 10);

        mw.setVisible(true);
        root.closeFrames();
    }
}
