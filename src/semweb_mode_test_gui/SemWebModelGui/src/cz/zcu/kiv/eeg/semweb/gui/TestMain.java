package cz.zcu.kiv.eeg.semweb.gui;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import cz.zcu.kiv.eeg.semweb.model.api.ConnectionException;
import cz.zcu.kiv.eeg.semweb.model.api.ModelConnector;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.text.ParseException;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TestMain  {

    public static void main(String [] args) throws ConnectionException, NonExistingUriNodeException, ConversionException, ParseException {

        
        //String url = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";
        //String user    = "EEGTEST";
        //String pwd  = "JPERGLER";


//        String url = "jdbc:oracle:thin:@localhost:1521:EEGDB";
//        String user    = "SYSMAN";
//        String pwd  = "password";

        String url = "jdbc:virtuoso://localhost:1111";
        String user    = "dba";
        String pwd  = "dba";


        String model = "model_semweb_a";
        String prefix = "http://cz.zcu.kiv.eeg#";

        ModelConnector conn = new ModelConnector(url, user, pwd, model, prefix);
        PortalModel modelOnt = conn.connect();

        MainWindow mw = new MainWindow(modelOnt);
        mw.setVisible(true);

//        OntModel modelO = modelOnt.getOntModel();
//
//        OntResource filipRes = modelO.createOntResource(prefix + "Filip");
//        OntClass osobaClass = modelO.createClass(prefix + "osobka");
//        filipRes.setRDFType(osobaClass);
//
//
//        //Vytvoreni podtridy
//        OntClass pokusator = modelO.createClass(prefix + "pokusnik");
//        OntResource pokusInst = modelO.createOntResource(prefix + "Pepik-Pokusnik");
//
//        pokusInst.setRDFType(pokusator); //instance tridy
//
//        OntClass specialista = modelO.createClass(prefix + "Specialistar");
//        OntResource speslInst = modelO.createOntResource(prefix + "Lojza_specialista");
//
//
//        speslInst.setRDFType(specialista); //instance tridy
//
//
//        osobaClass.addSubClass(pokusator); //nastaveni podtridy
//        pokusator.addSubClass(specialista);


//        OntClass occ = modelO.getOntClass(prefix + "experiment_metadata");
//        ExtendedIterator itt = occ.listInstances(false);
//
//        while (itt.hasNext()) {
//
//            System.out.println(((Resource)itt.next()).getURI());
//
//        }
        //conn.disconnect();


        /*
        List<String> cls = modelOnt.listParentClasses();
        for (String item: cls) {
            System.out.println(item);
            listSubClasses(item, "----", modelOnt);            
        }
        */

        
        

    }

    public static void listSubClasses(String item, String level, PortalModel  model) throws NonExistingUriNodeException {

        if (model.hasSubClasses(item)) {
            List<String> cls = model.listSubClasses(item);

            for (String node: cls) {
                System.out.println(level + " " + node);
                listSubClasses(node, level + "----", model);
            }
        }
    }


}
