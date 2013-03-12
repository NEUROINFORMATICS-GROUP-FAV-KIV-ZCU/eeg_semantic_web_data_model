package cz.zcu.kiv.eeg.semweb.model.api;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Item;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Literal;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.Uri;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.toplevel.Pair;
import cz.zcu.kiv.eeg.semweb.model.api.utils.DataConverter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import oracle.spatial.rdf.client.jena.ModelOracleSem;

/**
 *  Main class of EEG/ERP portal semantic web model API
 *  All communication with database provided via this class
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class PortalModel {

    private ModelOracleSem basicModel; //Oracle semWeb model (basic)
    private OntModel ontologyModel; //Jena ontology model
    private String defNamespace; //default namespace for EEG/ERP


    public PortalModel(ModelOracleSem model, String namespace) {
        this.basicModel = model;
        this.ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF, model); //create Ontology model based on Oracle semWeb model
        this.defNamespace = namespace;
    }

    public List<Pair> getInstanceProperties(String uri) {

        List answer = new ArrayList<Pair>();

        Individual target = ontologyModel.getIndividual(uri);

        if (target == null) {
            return answer;
        }

        Iterator it = target.listProperties();
        while (it.hasNext()) {
            Statement item = (Statement) it.next();

            RDFNode object = item.getObject();
            Item objectValue;

            if (object.isLiteral()) {
                objectValue = new Literal(DataConverter.convertObject(object.asLiteral()));
            }else {
                objectValue = new Uri(object.asResource().getNameSpace(), object.asResource().getLocalName());
            }

            Pair p = new Pair(new Uri(item.getPredicate().getNameSpace(), item.getPredicate().getLocalName()), objectValue);
            answer.add(p);
        }
        return answer;
    }


    public void getPersonInstances() { //REMOVE

        Iterator softky = ontologyModel.getOntClass(defNamespace + "person").listInstances();

        while (softky.hasNext()) {
            OntResource softik = (OntResource) softky.next();
            System.out.println("PersonInst: " + softik.getURI());
        }
    }

    public void close() {
        ontologyModel.close();
    }
   

}
