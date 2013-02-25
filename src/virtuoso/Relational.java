package virtuoso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Relational {

  private static Connection connect = null;
  private static Statement statement = null;
  private static PreparedStatement preparedStatement = null;
  private static ResultSet resultSet = null;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
      //Class.forName("virtuoso.jdbc4.Driver");
      Class.forName("oracle.jdbc.OracleDriver");

      //connect = DriverManager.getConnection("jdbc:virtuoso://10.109.94.234:1111", "dba", "dba");
      //connect = DriverManager.getConnection("jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP", "EEGTEST","JPERGLER");
      connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:EEGDB", "SYSMAN","password");
      //connect.setCatalog("db.dba");
      statement = connect.createStatement();

      //createData();
      getOracleData();
      //deleteData();
      connect.close();
    }


    private static void createData() throws SQLException {

        System.out.println("Create new table EXAMPLE");
        statement.execute("CREATE TABLE EXAMPLE (JMENO VARCHAR(20), PRIJMENI VARCHAR(20), VEK INTEGER)");
        
        //statement.execute("CREATE TABLE ARTICLES (ARTICLE_ID NUMERIC, PERSON_ID NUMERIC, RESEARCH_GROUP_ID NUMERIC, TITLE VARCHAR(150), TEXT BLOB, TIME DATE)");
        
        System.out.println("Insert row to EXAMPLE");
        statement.execute("INSERT INTO EXAMPLE (JMENO, PRIJMENI, VEK) VALUES ('Filip', 'Markvart', 23)");
        statement.execute("INSERT INTO EXAMPLE (JMENO, PRIJMENI, VEK) VALUES ('Pepik', 'Novak', 25)");
    }


    private static void getData() throws SQLException {

        System.out.println("List table example:");
        resultSet = statement.executeQuery("SELECT prijmeni, vek FROM example WHERE vek > 20");

        while (resultSet.next()) {
        System.out.print("Prijmeni: " + resultSet.getString("prijmeni") + " - ");
        System.out.println("Vek: " + resultSet.getString("vek"));
      }
    }


    private static void getOracleData() throws SQLException {

        System.out.println("List table Oracle example:");
//        String select = "create table family_rdf_data(id number, triple sdo_rdf_triple_s)";

        String select = "select username from dba_users";


        //String select = "drop table filip_test";

        //String select = "select PASSWORD_ from EEGTEST.PERSON";
        
        //statement.execute(select);

        resultSet = statement.executeQuery(select);

        while (resultSet.next()) {
        //System.out.println("Prijmeni: " + resultSet.getString("PERSON1_48_0_") + " - ");
        System.out.println("Pohlavi: " + resultSet.getString("username"));
        }


    }

    private static void deleteData() throws SQLException {

        System.out.println("Delete table EXAMPLE");
        statement.execute("DROP TABLE EXAMPLE");
    }
}
