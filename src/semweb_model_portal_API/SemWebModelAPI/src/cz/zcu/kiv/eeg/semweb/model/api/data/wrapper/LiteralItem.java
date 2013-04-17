package cz.zcu.kiv.eeg.semweb.model.api.data.wrapper;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.utils.DataConverter;
import java.text.ParseException;

/**
 * Literal wrapper class
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public final class LiteralItem extends Item {

    private Object value; //converted value
    private Literal original; //semWeb model literal (Jena object)
    private OntProperty chainProperty; //individual chained property
    private Individual parentInd; //property chained individual
    private PortalModel model; //semWeb model

    public LiteralItem(Literal orginalLiteral, OntProperty chainedProperty, Individual parentIndividual, PortalModel model) throws ParseException {
        setValue(DataConverter.convertObject(orginalLiteral));

        this.model = model;
        this.chainProperty = chainedProperty;
        this.original = orginalLiteral;
        this.parentInd = parentIndividual;
    }

    /**
     * Get literal value
     * @return value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set literal value
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Get literal dataType
     * @return
     */
    public String getXsdType() {
        return original.getDatatypeURI();
    }

    @Override
    public boolean isUri() {
        return false;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    /**
     * Update literal value
     *
     * @param newValue New value
     */
    public void updateValue(Object newValue) {

        parentInd.addLiteral(chainProperty, model.getOntModel().createTypedLiteral(newValue, chainProperty.getRange().getURI())); //add new
        parentInd.removeProperty(chainProperty, original);//remove
    }

    /**
     * Remove literal bounded to individuals property
     */
    public void removeValue() {
        parentInd.removeProperty(chainProperty, original);//remove
    }

    /**
     * Return URI of parent individual
     * 
     * @return parent individual URI
     */
    public String getParentInd() {
        return parentInd.getURI();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
