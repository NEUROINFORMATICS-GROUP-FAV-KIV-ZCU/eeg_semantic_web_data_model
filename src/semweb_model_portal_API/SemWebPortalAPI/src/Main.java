
import cz.zcu.kiv.eeg.semweb.model.api.ConnectionException;
import cz.zcu.kiv.eeg.semweb.model.api.ModelConnector;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ConversionException;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.NonExistingUriNodeException;
import java.text.ParseException;
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


        List<Item> items = modelOnt.listProperties("http://cz.zcu.kiv.eeg#person/researcher/instance7");

        for (Item itemka: items) {
            System.out.println(itemka.getAsUri().getUri());
        }

        items = modelOnt.listInstance("http://cz.zcu.kiv.eeg#person/researcher", false);

        System.out.println("---------------------------------------------------------------------------------------");

        for (Item itemka: items) {
            System.out.println(itemka.getAsUri().getUri() + "---- AND Group title " + itemka.getAsUri().getPropertyVal("http://cz.zcu.kiv.eeg#person/researcher/group_member").getAsUri().getPropertyVal("http://cz.zcu.kiv.eeg#research_group/title"));
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
