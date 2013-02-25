package virtuoso;


import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import oracle.spatial.rdf.client.jena.*;




/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class OracleJena {

    public static void main(String[] args) throws Exception
  {
    String szJdbcURL = "jdbc:oracle:thin:@localhost:1521:EEGDB";
    String szUser    = "SYSMAN";
    String szPasswd  = "password";

    String szModelName = "modelik";
	  
    Oracle oracle = new Oracle(szJdbcURL, szUser, szPasswd);

    ModelOracleSem model = ModelOracleSem.createOracleSemModel(oracle, szModelName);

    //OntModel modelO = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF, model);



    OntModel modelO = ModelFactory.createOntologyModel();


//    model.close();
//    OracleUtils.dropSemanticModel(oracle, szModelName);
//    oracle.dispose();
//    if (true) return;


    String prefix = "http://www.filip.org/";

    Resource res = model.createResource(prefix + "Filip");
    Property prop = model.createProperty(prefix + "has");

    //modelO.add(res, prop, prefix + "book");


    OntResource filipRes = modelO.createOntResource(prefix + "Filip");
    OntClass osobaClass = modelO.createClass(prefix + "osobka");
    filipRes.setRDFType(osobaClass);


    //Vytvoreni podtridy
    OntClass pokusator = modelO.createClass(prefix + "pokusnik");
    OntResource pokusInst = modelO.createOntResource(prefix + "Pepik-Pokusnik");

    pokusInst.setRDFType(pokusator); //instance tridy

    OntClass specialista = modelO.createClass(prefix + "Specialistar");
    OntResource speslInst = modelO.createOntResource(prefix + "Lojza_specialista");

    pokusInst.setRDFType(pokusator); //instance tridy
    speslInst.setRDFType(specialista); //instance tridy


    osobaClass.addSubClass(pokusator); //nastaveni podtridy
    pokusator.addSubClass(specialista);


    //Podtrida



    //Definice SubProperty
    OntProperty propRodic = modelO.createOntProperty(prefix + "personProperty"); //rodic
    OntProperty propPotomek = modelO.createOntProperty(prefix + "podVlasnost"); //potomek

    propPotomek.addSuperProperty(propRodic); //dedicnost



    //konec subProperty



    modelO.add(filipRes, prop, prefix + "book");

    modelO.add(filipRes, propPotomek, prefix + "ahoj"); //pridani dedicne property
    modelO.add(filipRes, propRodic, prefix + "nazdar"); //pridani rodicovske property


    model.add(modelO, true);

    

    //model.add(model.createStatement(res, prop, "book"));

    ResIterator it = model.listSubjects();

    while (it.hasNext()) {

        Resource r = it.nextResource();

        //System.out.println(r.getLocalName());

        if (r.getLocalName().equals("Filip")) {
            OntResource rr = modelO.getOntResource(r);
            System.out.println("RDF:TYPE of Filip is: " + rr.getRDFType().getLocalName());
        }

        if (r.getLocalName().equals("osobka")) {
            OntResource rr = modelO.getOntResource(r);
            if (rr.isClass()) {
                System.out.println("osbka je trida");

                OntClass cls = modelO.getOntClass(rr.getURI());


                ExtendedIterator itx = cls.listInstances();

                while(itx.hasNext()) {

                    OntResource resorko = (OntResource) itx.next();

                    System.out.print("Instatnci osbka je: " + resorko.getURI() + " - TRIDA -");
                    System.out.println(resorko.getRDFType().getURI());

                }

                

            }
        }
    }


    String queryString =
      " SELECT ?s ?p ?o WHERE { ?s <http://www.filip.org/personProperty> ?o . ?s ?p ?o}";

    Query query = QueryFactory.create(queryString) ;
    QueryExecution qexec = QueryExecutionFactory.create(query, model) ;

    try {
      int iMatchCount = 0;
      ResultSet results = qexec.execSelect() ;
      ResultSetFormatter.out(System.out, results, query);
    }
    finally {
      qexec.close() ;
    }




    //System.out.println(it.nextResource().listProperties().next().getPredicate().getURI());
    //System.out.println(it.nextResource().listProperties().next().getPredicate().getURI());
    
    model.close();
    
   OracleUtils.dropSemanticModel(oracle, szModelName);
    
    oracle.dispose();
    }
}
