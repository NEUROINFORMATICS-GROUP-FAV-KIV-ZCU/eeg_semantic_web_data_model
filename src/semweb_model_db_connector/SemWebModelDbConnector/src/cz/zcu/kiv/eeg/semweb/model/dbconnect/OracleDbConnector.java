package cz.zcu.kiv.eeg.semweb.model.dbconnect;

import com.hp.hpl.jena.rdf.model.Model;
import java.sql.Statement;
import java.util.Locale;
import oracle.spatial.rdf.client.jena.ModelOracleSem;
import oracle.spatial.rdf.client.jena.Oracle;
import oracle.spatial.rdf.client.jena.OracleUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class OracleDbConnector implements DbConnector {

    private String modelName;
    private String dbUrl;
    private String username;
    private String password;

    private Oracle oracleConnection;
    private ModelOracleSem oracleModel;
    private Statement relational;

    private static final Logger logger = Logger.getLogger(VirtuosoDbConnector.class);

    public OracleDbConnector(String model, String dbUrl, String username, String password) {

        this.modelName = model;
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }

    public Model connect() {
       try {
           Locale.setDefault(Locale.US);
           logger.info("Connecting to " + dbUrl);

           oracleConnection = new Oracle(dbUrl, username, password);
           oracleModel = ModelOracleSem.createOracleSemModel(oracleConnection, modelName);
           relational = oracleConnection.getConnection().createStatement();

           logger.info("Connection estabilished.");
        }catch (Exception ex) {
            logger.error("Connecting error:", ex);
            return null;
        }
        return oracleModel;
    }

    public void disconnect() {
        logger.info("Disconnecting from DB.");
        try {
            if (oracleModel != null && !oracleModel.isClosed()) {
                oracleModel.close();
            }
            logger.info("DB model disconnected.");
        } catch (Exception ex) {
            logger.error("Disconnecting error:", ex);

        }
    }

    public void removeModel() {
        logger.info("Removing model...");
        try {
            OracleUtils.dropSemanticModel(oracleConnection, modelName);
        } catch (Exception ex) {
            logger.error("Removing model error:", ex);
        }
        logger.info("Removing model done.");
    }

    public Statement getRelationConn() {
        return relational;
    }

    public String getLargeTextType() {
        return "CLOB";
    }

    public String getLargeBinaryType() {
        return "BLOB";
    }

    public String getVarcharType() {
        return "VARCHAR2(256)";
    }


}
