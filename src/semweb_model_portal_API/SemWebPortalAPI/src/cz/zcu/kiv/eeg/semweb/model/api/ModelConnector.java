package cz.zcu.kiv.eeg.semweb.model.api;

import com.hp.hpl.jena.rdf.model.Model;
import java.util.Locale;
import org.apache.log4j.Logger;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;

/**
 * This modul is used to connect to Oracle semantic web database and return accessable PortalModel
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ModelConnector {

    private String dbURL; //Database URL
    private String username; //connection username
    private String password; //connection password

    private String modelName; //Oracle semWeb model name
    private String defNamespace; //default model namespace

    //private Oracle oracleConnection; //DB connection
    //private ModelOracleSem oracleModel; //semWeb model connection

    //private VirtGraph virtuosoGraph;
    private Model basicModel;


    private static final Logger logger = Logger.getLogger(ModelConnector.class);

    public ModelConnector(String url, String user, String pwd, String model, String namespace) {
        this.dbURL = url;
        this.username = user;
        this.password = pwd;
        this.modelName = model;
        this.defNamespace = namespace;
    }

    public PortalModel connect() throws ConnectionException {

        Locale.setDefault(Locale.US);

        try {
            //oracleConnection = new Oracle(dbURL, username, password);
             //virtuosoGraph = new VirtGraph (dbURL, username, password);
        }catch (Exception ex) {
            logger.error("Connecting error:", ex);
            throw new ConnectionException("Can not connect database.", ex);
        }

        connectSemWeb();

        return new PortalModel(basicModel, defNamespace);
    }

    private void connectSemWeb() throws ConnectionException {
        try {
            //oracleModel = ModelOracleSem.createOracleSemModel(oracleConnection, modelName);
            basicModel = VirtModel.openDatabaseModel(modelName, dbURL, username, password);
        }catch (Exception ex) {
            logger.error("Connecting error:", ex);
            throw new ConnectionException("Can not connect semantic web model.", ex);
        }
    }

    public void disconnect() throws ConnectionException {
        try {
            if (basicModel != null) {
                basicModel.close();
            }
        } catch (Exception ex) {
            logger.error("Disconnecting error:", ex);
            throw new ConnectionException("Can not disconnect", ex);
        }
    }

}
