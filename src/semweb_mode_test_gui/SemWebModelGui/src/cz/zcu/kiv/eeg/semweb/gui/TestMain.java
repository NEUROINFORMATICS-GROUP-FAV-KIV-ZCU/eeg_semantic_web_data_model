package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.gui.connect.ConnectWindow;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.apache.log4j.PropertyConfigurator;

/**
 * Main class set look&feel and open connect window
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TestMain {

    public static void main(String[] args) {

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

        //set logger
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("log4j.properties"));
        } catch (IOException ex) {
            System.out.println("Can not write log file");
        }
        PropertyConfigurator.configure(props);

        //show connect window
        ConnectWindow cw = new ConnectWindow();
        cw.setVisible(true);

    }
}
