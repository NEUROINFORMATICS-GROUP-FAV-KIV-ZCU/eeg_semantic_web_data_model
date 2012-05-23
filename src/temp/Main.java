package javaapplication19;

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
         try {
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

            IRI ontologyIRI = IRI.create("http://www.kiv.zcu.cz/eeg");
            // Create the document IRI for our ontology
            IRI documentIRI = IRI.create("file:/tmp/MyOnt.owl");
            // Set up a mapping, which maps the ontology to the document IRI
            SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
            manager.addIRIMapper(mapper);


            
            OWLOntology ontology = manager.createOntology(ontologyIRI);
            
            OWLDataFactory factory = manager.getOWLDataFactory();
            // class A


            OWLClass clsA = factory.getOWLClass(IRI.create(ontologyIRI + "/Person"));
            // Now create the axiom

           OWLDatatype range = factory.getOWLDatatype(IRI.create("http://www.datovka.cz"));



           //factory.getOWL



            OWLDataRange restriction = factory.getOWLDatatypeRestriction(range, OWLFacet.LENGTH, factory.getOWLLiteral(12));





            OWLClass measuration = factory.getOWLClass(IRI.create(ontologyIRI + "/Measuration"));


            OWLIndividual filip = factory.getOWLNamedIndividual(IRI.create(clsA.getIRI().toString() + "#Filip"));
            OWLIndividual pepik = factory.getOWLNamedIndividual(IRI.create(clsA.getIRI().toString() + "#Pepik"));

            OWLAxiom clsAxiom = factory.getOWLClassAssertionAxiom(clsA, filip);
            OWLAxiom clsAxiomB = factory.getOWLClassAssertionAxiom(clsA, pepik);

            OWLIndividual measA = factory.getOWLNamedIndividual(IRI.create(measuration.getIRI().toString() + "#SomeExperiment1"));
            OWLIndividual measB = factory.getOWLNamedIndividual(IRI.create(measuration.getIRI().toString() + "#SomeExperiment2"));


            OWLDataProperty surname = factory.getOWLDataProperty(IRI.create(clsA.getIRI() + "/surname"));
            OWLDataProperty gender = factory.getOWLDataProperty(IRI.create(clsA.getIRI() + "/gender"));
            OWLDataProperty email = factory.getOWLDataProperty(IRI.create(clsA.getIRI() + "/email"));

            OWLAnnotation anotace = factory.getOWLAnnotation(factory.getOWLDeprecated(), factory.getOWLLiteral("mojeAnotace"));

            OWLDataProperty temperature = factory.getOWLDataProperty(IRI.create(measuration.getIRI() + "/temperature"));
            OWLDataProperty weatherNote = factory.getOWLDataProperty(IRI.create(measuration.getIRI() + "/weathernote"));
            OWLObjectProperty personByOwnerId = factory.getOWLObjectProperty(IRI.create(measuration.getIRI() + "/personByOwnerId"));
            OWLObjectProperty measByOwnerId = factory.getOWLObjectProperty(IRI.create(measuration.getIRI() + "/measurationByOwnerId"));


            OWLAxiom propertka = factory.getOWLDataPropertyAssertionAxiom(surname, filip, factory.getOWLLiteral("21", OWL2Datatype.XSD_INTEGER));
            OWLAxiom propertka2 = factory.getOWLDataPropertyAssertionAxiom(surname, pepik, "Novak");

                OWLClassExpression cardinal = factory.getOWLObjectMinCardinality(22, personByOwnerId);

                OWLClassAssertionAxiom axx = factory.getOWLClassAssertionAxiom(cardinal, filip);



                Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
                annotations.add(anotace);

                OWLAnnotationAssertionAxiom axs = factory.getOWLAnnotationAssertionAxiom(clsA.getIRI(), anotace);


            OWLAxiom propertka3 = factory.getOWLDataPropertyAssertionAxiom(gender, filip, factory.getOWLLiteral("M"));
            OWLAxiom propertka4 = factory.getOWLDataPropertyAssertionAxiom(gender, pepik, "M");

            OWLAxiom propertka5 = factory.getOWLDataPropertyAssertionAxiom(email, filip, "neco@nec.com");
            OWLAxiom propertka6 = factory.getOWLDataPropertyAssertionAxiom(email, pepik, "jine@neco.org");


            OWLAxiom propertka7 = factory.getOWLDataPropertyAssertionAxiom(temperature, measA, 30);
            OWLAxiom propertka8 = factory.getOWLDataPropertyAssertionAxiom(temperature, measB, 40);

            OWLAxiom propertka9 = factory.getOWLDataPropertyAssertionAxiom(weatherNote, measA, "Poznamka 1");
            OWLAxiom propertka10 = factory.getOWLDataPropertyAssertionAxiom(weatherNote, measB, "Poznamka 2");

            OWLAxiom propertka11 = factory.getOWLObjectPropertyAssertionAxiom(personByOwnerId, measA, filip);
            OWLAxiom propertka12 = factory.getOWLObjectPropertyAssertionAxiom(personByOwnerId, measB, pepik);

            OWLAxiom propertka13 = factory.getOWLObjectPropertyAssertionAxiom(measByOwnerId, filip, measA);
            OWLAxiom propertka14 = factory.getOWLObjectPropertyAssertionAxiom(measByOwnerId, filip, measB);





            AddAxiom addAxiom0 = new AddAxiom(ontology,  propertka);
            AddAxiom addAxiom1 = new AddAxiom(ontology,  propertka2);
            AddAxiom addAxiom2 = new AddAxiom(ontology,  propertka3);
            AddAxiom addAxiom3 = new AddAxiom(ontology,  propertka4);
            AddAxiom addAxiom4 = new AddAxiom(ontology,  propertka5);
            AddAxiom addAxiom5 = new AddAxiom(ontology,  propertka6);
            AddAxiom addAxiom6 = new AddAxiom(ontology,  propertka7);
            AddAxiom addAxiom7 = new AddAxiom(ontology,  propertka8);
            AddAxiom addAxiom8 = new AddAxiom(ontology,  propertka9);
            AddAxiom addAxiom9 = new AddAxiom(ontology,  propertka10);
            AddAxiom addAxiom10 = new AddAxiom(ontology,  propertka11);
            AddAxiom addAxiom11 = new AddAxiom(ontology,  propertka12);
            AddAxiom addAxiom12 = new AddAxiom(ontology,  propertka13);
            AddAxiom addAxiom13 = new AddAxiom(ontology,  propertka14);
            AddAxiom addAxiom14 = new AddAxiom(ontology,  factory.getOWLTransitiveObjectPropertyAxiom(personByOwnerId));
            AddAxiom addAxiom15 = new AddAxiom(ontology,  clsAxiomB);
            AddAxiom addAxiom16 = new AddAxiom(ontology,  axs);



            // We now use the manager to apply the change
            manager.applyChange(addAxiom0);
            manager.applyChange(addAxiom1);
            manager.applyChange(addAxiom6);
            manager.applyChange(addAxiom2);
            manager.applyChange(addAxiom3);
            manager.applyChange(addAxiom4);
            manager.applyChange(addAxiom5);

            manager.applyChange(addAxiom7);
            manager.applyChange(addAxiom8);
            manager.applyChange(addAxiom9);
            manager.applyChange(addAxiom10);
            manager.applyChange(addAxiom11);
            manager.applyChange(addAxiom12);
            manager.applyChange(addAxiom13);

            manager.applyChange(addAxiom14);
            manager.applyChange(addAxiom15);
            manager.applyChange(addAxiom16);
            



            


           
            manager.saveOntology(ontology);

        }
        catch (OWLException e) {
            e.printStackTrace();
        }
    }

    
}
