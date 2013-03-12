package cz.zcu.kiv.eeg.semweb.model.creator.data;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TableItem {

    public enum DATA_TYPE {BLOB, CLOB};

    private final String TABLE_DATA_TYPE_TEXT = "text";
    private final String TABLE_DATA_TYPE_BINARY = "binary";

    private String name;
    private DATA_TYPE type;
    
    
    public TableItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public DATA_TYPE getType() {
        return type;
    }

    public void setDataType(String type) {
        if (type.equals(TABLE_DATA_TYPE_TEXT)) {
            this.type = DATA_TYPE.CLOB;
        } else {
          this.type = DATA_TYPE.BLOB;
        }
    }
}
