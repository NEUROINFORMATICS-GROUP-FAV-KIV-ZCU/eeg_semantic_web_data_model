package cz.zcu.kiv.eeg.semweb.model.creator;

import cz.zcu.kiv.eeg.semweb.model.creator.gui.MainFrame;
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

         MainFrame mf = new MainFrame();
         mf.setVisible(true);
    }

}
