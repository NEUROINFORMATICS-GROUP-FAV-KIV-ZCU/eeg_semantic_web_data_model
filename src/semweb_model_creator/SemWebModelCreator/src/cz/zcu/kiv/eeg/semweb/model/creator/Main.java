package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.dom4j.DocumentException;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws DocumentException {

        String szJdbcURL = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";   
        String szUser    = "EEGTEST";
        String szPasswd  = "JPERGLER";
        String szModelName = "modelik";



        DataLoader load = new DataLoader(new File("portalModel.xml"));

        System.out.println(load.loadData());

        List<ClassDataItem> classes = load.getClasses();
        List<PropertyDataItem> properties = load.getProperties();

        ModelCreator cr = new ModelCreator();

        System.out.println(cr.connect(szJdbcURL, szUser, szPasswd));
        System.out.println(cr.createModel(szModelName, "http://cz.zcu.kiv.eeg#", classes, properties));
        //System.out.println(cr.removeModel(szModelName));
        System.out.println(cr.disconnect());


        
        



    }

}
