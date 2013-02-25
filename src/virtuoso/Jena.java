//package virtuoso;
//
//import com.hp.hpl.jena.query.*;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import virtuoso.jena.driver.VirtGraph;
//import virtuoso.jena.driver.VirtuosoQueryExecution;
//import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
///**
// *
// * @author Filip Markvart filip.marq (at) seznam.cz
// */
//public class Jena {
//
//    public static void main(String[] args) throws ClassNotFoundException {
//
//        System.out.println("List available graphs: ");
//
//      //String url = "jdbc:virtuoso://10.109.94.234:1111";
//	String url = "jdbc:oracle:thin:@localhost:1521:EEGDB";
//
//      //VirtGraph set = new VirtGraph (url, "dba", "dba");
//        VirtGraph set = new VirtGraph (url, "SYSMAN", "password");
//
//	Query sparql = QueryFactory.create("SELECT DISTINCT ?graph WHERE { GRAPH ?graph {?s ?p ?o}}");
//
//	VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, set);
//
//	ResultSet results = vqe.execSelect();
//		while (results.hasNext()) {
//			QuerySolution result = results.nextSolution();
//		    RDFNode graph = result.get("graph");
//		    //RDFNode s = result.get("s");
//		    //RDFNode p = result.get("p");
//		    //RDFNode o = result.get("o");
//		    System.out.println(graph);
//		}
//
//    }
//}