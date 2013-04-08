package cz.zcu.kiv.eeg.semweb.model.dbconnect;

import com.hp.hpl.jena.rdf.model.Model;
import java.sql.Statement;

/**
 * Databse connector interface contais all model required operations to provide nonRelational
 * and relational database communiction
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public interface DbConnector {

    /**
     * Connect to database and its model
     * @return
     */
    public Model connect();

    /**
     * Disconnec from database and close model
     */
    public void disconnect();

    /**
     * Remove database model
     */
    public void removeModel();

    /**
     * Get connection to relational databse
     * @return
     */
    public Statement getRelationConn();

    /**
     * Get database specific large text data DataType
     *
     * @return dataType name
     */
    public String getLargeTextType();

    /**
     * Get database specific large binary data DataType
     *
     * @return dataType name
     */
    public String getLargeBinaryType();

    /**
     * Get database specific common text data DataType
     * @return
     */
    public String getVarcharType();
}
