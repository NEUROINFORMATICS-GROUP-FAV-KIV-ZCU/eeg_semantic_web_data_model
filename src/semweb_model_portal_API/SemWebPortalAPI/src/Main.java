
import cz.zcu.kiv.eeg.semweb.model.api.ConnectionException;
import cz.zcu.kiv.eeg.semweb.model.api.ModelConnector;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.LiteralItem;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import cz.zcu.kiv.eeg.semweb.model.search.DisjunctionCondition;
import cz.zcu.kiv.eeg.semweb.model.search.PropertyValLikeCondition;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *  #############  Just for testing purposes ###############
 *
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ConnectionException, NonExistingUriNodeException, ConversionException, ParseException {


        String url = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";
        String user    = "EEGTEST";
        String pwd  = "JPERGLER";

        String model = "model_semweb";
        String ns = "http://cz.zcu.kiv.eeg#";

        ModelConnector conn = new ModelConnector(url, user, pwd, model, ns);
        PortalModel modelOnt = conn.connect();

        //modelOnt.getPersonInstances();


        Item itemkal = modelOnt.getInstance("http://cz.zcu.kiv.eeg#person/researcher", false);

        
            System.out.println(itemkal.getAsUri().getPropertyVal(ns + "person/given_name"));

        
        

        DisjunctionCondition dc = new DisjunctionCondition();
        dc.addCondition(new PropertyValLikeCondition(modelOnt.getPropertyByUri(ns + "person/given_name"), "givenName"));
        //dc.addCondition(new HasPropertyCondition(new UriItem(ns, "person/researcher/facebook_id", modelOnt)));


        List <Item> items = new ArrayList<Item>();

        items = modelOnt.listInstance(ns + "person/researcher", dc);

        System.out.println("---------------------------------------------------------------------------------------");

        for (Item itemka: items) {
            System.out.println(itemka.getAsUri().getPropertyVal(ns + "person/given_name") + "---- AND Group title " + itemka.getAsUri().getPropertyVal(ns + "person/researcher/group_member").getAsUri().getPropertyVal("http://cz.zcu.kiv.eeg#research_group/title"));

            LiteralItem lit = itemka.getAsUri().getPropertyVal(ns + "person/given_name").getAsLiteral();

            if (lit.getValue().equals("givenName0")) {
                lit.updateValue("Filipek");
                System.out.println("Nasel");

            }


        }



//        List<Pair> pairs = modelOnt.getInstanceProperties("http://cz.zcu.kiv.eeg#person/researcher/instance7");
//
//        for (Pair item: pairs) {
//
//            Uri property = (Uri) item.getProperty();
//
//            if (item.getValue().getType().equals(ItemType.URI)) {
//
//                Uri obj = (Uri) item.getValue();
//                System.out.println(property.getLocalName() + " - " + obj.getLocalName());
//            }else {
//                Literal objLit = (Literal) item.getValue();
//                System.out.println(property.getLocalName() + " - " + objLit.getValue().toString());
//            }
//
//        }


        //close model
        conn.disconnect();
    }

}
