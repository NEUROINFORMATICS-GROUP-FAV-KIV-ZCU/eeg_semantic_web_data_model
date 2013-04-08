package cz.zcu.kiv.eeg.semweb.model.creator.data;

/**
 * Table item wrapper.
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TableItem {

    public enum DATA_TYPE {BLOB, CLOB};

    private String name;
    private String type;
    
    
    public TableItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setDataType(String type) {       
        this.type = type;
    }
    
}
