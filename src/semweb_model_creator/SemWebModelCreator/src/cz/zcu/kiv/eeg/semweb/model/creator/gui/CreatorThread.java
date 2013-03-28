package cz.zcu.kiv.eeg.semweb.model.creator.gui;

import cz.zcu.kiv.eeg.semweb.model.creator.DataLoader;
import cz.zcu.kiv.eeg.semweb.model.creator.ModelCreator;
import cz.zcu.kiv.eeg.semweb.model.creator.data.ClassDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.PropertyDataItem;
import cz.zcu.kiv.eeg.semweb.model.creator.data.TableItem;
import cz.zcu.kiv.eeg.semweb.model.dbconnect.DbConnector;
import cz.zcu.kiv.eeg.semweb.model.testdata.SimpleDataCreator;
import java.io.File;
import java.util.List;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class CreatorThread implements Runnable {

    private CreateProgressWindow root;
    
    public CreatorThread(CreateProgressWindow aThis) {
        this.root = aThis;

    }

    public void run() {
       createModel();
    }

     private void createModel() {

        this.root.updateStatus("Loading XML file", 1, false);

        DataLoader load = new DataLoader(new File(root.getFileName()));

        if (!load.loadData()) {
            this.root.updateStatus("Loading XML failed", 10, true);
            return;
        }

        List<ClassDataItem> classes = load.getClasses();
        List<PropertyDataItem> properties = load.getProperties();
        List<TableItem> tables = load.getTables();

        this.root.updateStatus("Connecting DB", 3, false);
        DbConnector connector = root.getConnection();
        
        ModelCreator cr = new ModelCreator(connector);
        if (!cr.connect()) {
            this.root.updateStatus("DB connecting failed", 10, true);
            return;
        }

        this.root.updateStatus("DB connected successfull", 4, false);

        if (root.getRemoveModel()) {
            this.root.updateStatus("Removing model", 4, false);
            cr.removeModel();
            this.root.updateStatus("Removing model", 5, false);
            cr.removeTables(root.getDefaultTablePrefix(), tables);
            this.root.updateStatus("Model removed", 9, false);
        }else {
            this.root.updateStatus("Creating model", 4, false);
            if (!cr.createModel(root.getDefaultPrefix(), root.getDefaultTablePrefix(), classes, properties, tables)) {
                this.root.updateStatus("Model creating failed", 10, true);
                cr.disconnect();
                return;
            }
            this.root.updateStatus("Inserting data", 6, false);

            if (root.getSimpleData()) {
                SimpleDataCreator sdc = new SimpleDataCreator(root.getDataSize());
                if (!cr.insertData(root.getModelName(), root.getDefaultPrefix(), sdc.getData())) {
                    this.root.updateStatus("Data insertion failed", 10, true);
                    cr.disconnect();
                    return;
                }
            }
        }
        this.root.updateStatus("Disconnectiong DB", 9, false);
        cr.disconnect();

        this.root.updateStatus("Done", 10, true);
    }

}
