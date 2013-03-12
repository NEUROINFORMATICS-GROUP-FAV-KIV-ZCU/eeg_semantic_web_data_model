
import cz.zcu.kiv.eeg.semweb.model.api.ConnectionException;
import cz.zcu.kiv.eeg.semweb.model.api.ModelConnector;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.ItemType;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Literal;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Uri;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.toplevel.Pair;
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
    public static void main(String[] args) throws ConnectionException {



        String url = "jdbc:oracle:thin:@students.kiv.zcu.cz:1521:EEGERP";
        String user    = "EEGTEST";
        String pwd  = "JPERGLER";

        String model = "model_semweb";
        String ns = "http://cz.zcu.kiv.eeg#";

        ModelConnector conn = new ModelConnector(url, user, pwd, model, ns);
        PortalModel modelOnt = conn.connect();

        //modelOnt.getPersonInstances();
       
        List<Pair> pairs = modelOnt.getInstanceProperties("http://cz.zcu.kiv.eeg#person/researcher/instance7");

        for (Pair item: pairs) {

            Uri property = (Uri) item.getProperty();

            if (item.getValue().getType().equals(ItemType.URI)) {

                Uri obj = (Uri) item.getValue();
                System.out.println(property.getLocalName() + " - " + obj.getLocalName());
            }else {
                Literal objLit = (Literal) item.getValue();
                System.out.println(property.getLocalName() + " - " + objLit.getValue().toString());
            }

        }


        //close model
        conn.disconnect();
    }

}
