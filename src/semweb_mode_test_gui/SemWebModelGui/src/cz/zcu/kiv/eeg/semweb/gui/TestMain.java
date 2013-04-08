package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.gui.connect.ConnectWindow;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Main class set look&feel and open connect window
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TestMain {

    public static void main(String[] args) throws NonExistingUriNodeException, ConversionException, ParseException, FileNotFoundException, IOException {

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            //usign basic Look&Feel manager
        }

        ConnectWindow cw = new ConnectWindow();
        cw.setVisible(true);

    }
}
