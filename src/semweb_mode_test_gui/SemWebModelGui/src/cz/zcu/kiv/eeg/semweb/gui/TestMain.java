package cz.zcu.kiv.eeg.semweb.gui;

import cz.zcu.kiv.eeg.semweb.gui.connect.ConnectWindow;
import cz.zcu.kiv.eeg.semweb.model.api.ConnectionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TestMain  {

    public static void main(String [] args) throws ConnectionException, NonExistingUriNodeException, ConversionException, ParseException, FileNotFoundException, IOException {

        
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
