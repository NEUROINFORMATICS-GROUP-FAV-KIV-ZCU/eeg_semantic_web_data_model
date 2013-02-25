/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package virtuoso;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class RDF {

  private static Connection connect = null;
  private static Statement statement = null;
  private static PreparedStatement preparedStatement = null;
  private static ResultSet resultSet = null;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
      //Class.forName("virtuoso.jdbc3.Driver");
      Class.forName("oracle.jdbc.OracleDriver");


      //connect = DriverManager.getConnection("jdbc:virtuoso://10.109.94.234:1111", "dba", "dba");
      //connect = DriverManager.getConnection("jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP", "EEGTEST","JPERGLER");
      connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:EEGDB", "SYSMAN","password");

      //connect = DriverManager.getConnection("jdbc:virtuoso://10.109.94.234:1111/UID=dba/PWD=dba");
      statement = connect.createStatement();

      createGraph();
      //listGrpahs();
      //System.out.println("------------");
      //insertTriplet();
      System.out.println("------------");
      //listTriplet();
      //dropGraph();
    }


    private static void listGrpahs() throws SQLException {
       String sparql = "SPARQL SELECT DISTINCT ?g WHERE { GRAPH ?g {?s ?p ?o}}";

       resultSet = statement.executeQuery(sparql);

       System.out.println("List available graphs:");
       while (resultSet.next()) {
           System.out.println("Graf: " + resultSet.getString("g"));
       }
    }


    private static void createGraph() throws SQLException {
       String sparql = "SPARQL CREATE GRAPH <http://mygraph.kiv.zcu.cz>";
       System.out.println("Create new graph");
       statement.execute(sparql);

    }

    private static void dropGraph() throws SQLException {

        String sparql = "SPARQL DROP GRAPH <http://mygraph.kiv.zcu.cz>";
        System.out.println("Drop new graph");
        statement.execute(sparql);
    }

    private static void insertTriplet() throws SQLException {

        String sparql = "SPARQL INSERT INTO GRAPH <http://mygraph.kiv.zcu.cz> "
                + "{ <http://www.mygraph.kiv.zcu.cz/foaf.rdf#i> <http://purl.org/dc/elements/1.1/title>  'Example title' ."
                + "<http://www.mygraph.kiv.zcu.cz/foaf.rdf#i> <http://purl.org/dc/elements/1.1/date> <2012-01-10T12:25:00>."
                + "<http://www.mygraph.kiv.zcu.cz/students/card#i> <http://purl.org/dc/elements/1.1/title> 'Filip Markvart' }";
        System.out.println("Insert triplet to graph.");
        statement.execute(sparql);
    }

    private static void listTriplet() throws SQLException {

        String sparql = "SPARQL SELECT * from <http://mygraph.kiv.zcu.cz> WHERE {?s ?p ?o}";
        System.out.println("List triplet of graph.");
        resultSet = statement.executeQuery(sparql);

        while (resultSet.next()) {
           System.out.println("Subject: " + resultSet.getString("s"));
           System.out.println("Predicate: " + resultSet.getString("p"));
           System.out.println("Object: " + resultSet.getString("o"));
       }
    }



}
