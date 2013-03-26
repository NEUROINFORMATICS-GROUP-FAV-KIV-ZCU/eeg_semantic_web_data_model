package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.TableItem;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.VirtuosoDbConnector;
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

        //String szJdbcURL = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";
        //String szUser    = "EEGTEST";
        //String szPasswd  = "JPERGLER";

        //String szJdbcURL = "jdbc:oracle:thin:@localhost:1521:EEGDB";
        //String szUser    = "SYSMAN";
        //String szPasswd  = "password";

        String szJdbcURL = "jdbc:virtuoso://localhost:1111";
        String szUser    = "dba";
        String szPasswd  = "dba";

        String szModelName = "model_semweb_a";

        boolean operationRes;

        logger.info("Loading XML file");
        DataLoader load = new DataLoader(new File("portalModel.xml"));

        operationRes = load.loadData();
        logger.info("Data loaded successfull: " + operationRes);

        List<ClassDataItem> classes = load.getClasses();
        List<PropertyDataItem> properties = load.getProperties();
        List<TableItem> tables = load.getTables();

        DbConnector connector = new VirtuosoDbConnector(szModelName, szJdbcURL, szUser, szPasswd);

        ModelCreator cr = new ModelCreator(connector);

        cr.connect();
        



        //operationRes = cr.createModel("http://cz.zcu.kiv.eeg#", "EEG_", classes, properties, tables);
        //logger.info("SemWeb model created successfull: " + operationRes);

        //SimpleDataCreator sdc = new SimpleDataCreator();
        //cr.insertData(szModelName, "http://cz.zcu.kiv.eeg#", sdc.getData());


      cr.removeModel();
      System.out.println(cr.removeTables("EEG_", tables));
        cr.disconnect();

    }

}
