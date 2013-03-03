package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.dom4j.DocumentException;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws DocumentException {

        
        DataLoader load = new DataLoader(new File("portalModel.xml"));

        System.out.println(load.loadData());

        List<PropertyDataItem> classes = load.getProperties();
        
        Iterator it = classes.iterator();
        
        while (it.hasNext()) {
            
            System.out.println(((PropertyDataItem) it.next()).getName());
        }


        
        



    }

}
