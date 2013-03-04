package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws DocumentException {

        String szJdbcURL = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";   
        String szUser    = "EEGTEST";
        String szPasswd  = "JPERGLER";
        String szModelName = "modelik";

        boolean operationRes;

        logger.info("Loading XML file");
        DataLoader load = new DataLoader(new File("portalModel.xml"));

        operationRes = load.loadData();
        logger.info("Data loaded successfull: " + operationRes);

        List<ClassDataItem> classes = load.getClasses();
        List<PropertyDataItem> properties = load.getProperties();

        ModelCreator cr = new ModelCreator();

        operationRes = cr.connect(szJdbcURL, szUser, szPasswd);
        logger.info("Database connect successfull: " + operationRes);

        operationRes = cr.createModel(szModelName, "http://cz.zcu.kiv.eeg#", classes, properties);
        logger.info("SemWeb model created successfull: " + operationRes);

        //System.out.println(cr.removeModel(szModelName));
        cr.disconnect();

    }

}
