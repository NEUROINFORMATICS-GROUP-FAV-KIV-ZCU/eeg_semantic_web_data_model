package cz.zcu.kiv.eeg.semweb.model.dbconnect;

import com.hp.hpl.jena.rdf.model.Model;
import java.sql.Statement;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public interface DbConnector {

    public Model connect();

    public void disconnect();

    public void removeModel();

    public Statement getRelationConn();

    public String getLargeTextType();

    public String getLargeBinaryType();

    public String getVarcharType();
}
