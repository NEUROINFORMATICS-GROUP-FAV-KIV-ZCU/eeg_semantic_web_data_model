package cz.zcu.kiv.eeg.semweb.model.testdata;

/**
 * Simple wrapper for created data items (triple statements)
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Triple {

    private String subject; //URI
    private String predicate; //URI
    private String object; //URI or literal (determined by property range)

    public Triple(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
