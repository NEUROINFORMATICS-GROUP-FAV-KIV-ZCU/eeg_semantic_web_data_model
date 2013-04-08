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
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TestMain  {

    public static void main(String [] args) throws NonExistingUriNodeException, ConversionException, ParseException, FileNotFoundException, IOException {

        try {
    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
            UIManager.setLookAndFeel(info.getClassName());
            break;
        }
    }
} catch (Exception e) {
    // If Nimbus is not available, you can set the GUI to another look and feel.
}


        //String url = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";
        //String user    = "EEGTEST";
        //String pwd  = "JPERGLER";


//        String url = "jdbc:oracle:thin:@localhost:1521:EEGDB";
//        String user    = "SYSMAN";
//        String pwd  = "password";

//        String url = "jdbc:virtuoso://localhost:1111";
//        String user    = "dba";
//        String pwd  = "dba";


//        String model = "model_semweb_a";
//        String prefix = "http://cz.zcu.kiv.eeg#";

        ConnectWindow cw = new ConnectWindow();
        cw.setVisible(true);

    }
}
