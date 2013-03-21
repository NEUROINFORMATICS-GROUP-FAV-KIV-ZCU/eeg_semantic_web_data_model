package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.TableItem;
import cz.zcu.kiv.eeg.semweb.model.testdata.SimpleDataCreator;
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
        String szModelName = "model_semweb4";

        boolean operationRes;

        logger.info("Loading XML file");
        DataLoader load = new DataLoader(new File("portalModel.xml"));

        operationRes = load.loadData();
        logger.info("Data loaded successfull: " + operationRes);

        List<ClassDataItem> classes = load.getClasses();
        List<PropertyDataItem> properties = load.getProperties();
        List<TableItem> tables = load.getTables();

        ModelCreator cr = new ModelCreator();

        operationRes = cr.connect(szJdbcURL, szUser, szPasswd);
        logger.info("Database connect successfull: " + operationRes);



        operationRes = cr.createModel(szModelName, "http://cz.zcu.kiv.eeg#", "EEG_", classes, properties, tables);
        logger.info("SemWeb model created successfull: " + operationRes);

        SimpleDataCreator sdc = new SimpleDataCreator();
        cr.insertData(szModelName, "http://cz.zcu.kiv.eeg#", sdc.getData());


      //System.out.println(cr.removeModel(szModelName));
      //System.out.println(cr.removeTables("EEG_", tables));
        cr.disconnect();

    }

}
