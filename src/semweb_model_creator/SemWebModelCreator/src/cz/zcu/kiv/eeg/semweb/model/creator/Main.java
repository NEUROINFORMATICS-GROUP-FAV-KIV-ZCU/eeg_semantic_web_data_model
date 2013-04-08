package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.gui.MainFrame;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;

/**
 * Model creator Main class
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Main {

    /** Runner
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Set logger
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("log4j.properties"));
            PropertyConfigurator.configure(props);
        } catch (IOException ex) {
            System.out.println("Can not write log file");
        }

        MainFrame mf = new MainFrame();
        mf.setVisible(true);
    }
}
