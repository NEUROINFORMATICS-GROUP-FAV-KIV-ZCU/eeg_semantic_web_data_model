package cz.zcu.kiv.eeg.semweb.gui;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Resource;
import cz.zcu.kiv.eeg.semweb.model.api.ConnectionException;
import cz.zcu.kiv.eeg.semweb.model.api.ModelConnector;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.text.ParseException;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TestMain  {

    public static void main(String [] args) throws ConnectionException, NonExistingUriNodeException, ConversionException, ParseException {

        
        String url = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";
        String user    = "EEGTEST";
        String pwd  = "JPERGLER";

        String model = "model_semweb6";
        String ns = "http://cz.zcu.kiv.eeg#";

        ModelConnector conn = new ModelConnector(url, user, pwd, model, ns);
        PortalModel modelOnt = conn.connect();

        //MainWindow mw = new MainWindow(modelOnt);
        //mw.setVisible(true);


        List<Item> items = modelOnt.listInstance("http://cz.zcu.kiv.eeg#research_group", false);

        for (Item individual: items) {
            System.out.println(individual.getAsUri().toString());


            for (Resource ooj: modelOnt.getOntModel().getIndividual(individual.getAsUri().toString()).listRDFTypes(true).toList()) {
                System.out.println(ooj.toString());

                
            }
            //OntClass ocParent = modelOnt.getOntModel().getOntClass(oc.getURI()).getSuperClass();
            //System.out.println(ocParent.toString());

            //for (Resource oo: ocParent.listInstances(true).toList()) {
            //    System.out.println(oo.toString());
            //}
        }


        conn.disconnect();


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
