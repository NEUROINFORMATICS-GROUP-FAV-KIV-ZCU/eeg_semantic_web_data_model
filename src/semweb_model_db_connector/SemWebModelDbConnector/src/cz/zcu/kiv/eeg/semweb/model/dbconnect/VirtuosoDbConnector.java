package cz.zcu.kiv.eeg.semweb.model.dbconnect;

import com.hp.hpl.jena.rdf.model.Model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.log4j.Logger;
import virtuoso.jena.driver.VirtModel;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class VirtuosoDbConnector implements DbConnector {

    private String modelName;
    private String dbUrl;
    private String username;
    private String password;

    private Model virtModel;
    private Connection relationalConn;
    private Statement relStatement;


    private static final Logger logger = Logger.getLogger(VirtuosoDbConnector.class);

    public VirtuosoDbConnector(String model, String dbUrl, String username, String password) {

        this.modelName = model;
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }

    public Model connect() {
        try {
            logger.info("Connecting to " + dbUrl);

            //relation
            Class.forName("virtuoso.jdbc4.Driver");
            relationalConn = DriverManager.getConnection(dbUrl, username, password);
            relStatement = relationalConn.createStatement();

            //semWeb
            virtModel = VirtModel.openDatabaseModel(modelName, dbUrl, username, password);
            logger.info("Connection estabilished.");
        }catch (Exception ex) {
            logger.error("Connecting error:", ex);
            return null;
        }
        return virtModel;
    }

    public void disconnect() {
        logger.info("Disconnecting from DB.");
        try {
            if (virtModel != null && !virtModel.isClosed()) {
                virtModel.close();
            }
            if (relationalConn != null && !relationalConn.isClosed()) {
                relationalConn.close();
            }
            logger.info("DB model disconnected.");
        } catch (Exception ex) {
            logger.error("Disconnecting error:", ex);

        }
    }
    
    public void removeModel() {
        logger.info("Removing model...");
        try {
            virtModel.remove(virtModel);
        } catch (Exception ex) {
            logger.error("Removing model error:", ex);
        }
        logger.info("Removing model done.");
    }

    public Statement getRelationConn() {
        return relStatement;
    }

    public String getLargeTextType() {
        return "LONG VARCHAR";
    }

    public String getLargeBinaryType() {
        return "LONG VARBINARY";
    }

    public String getVarcharType() {
        return "VARCHAR";
    }

}
