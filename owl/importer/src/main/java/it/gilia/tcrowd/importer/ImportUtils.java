package it.gilia.tcrowd.importer;

import java.io.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;

/**
 * Class use for utility methods that can be used anywhere in the application.
 * The methods inside this class are public and static, and will facilitate
 * coding complex methods/functions
 */
public class ImportUtils {

    /**
     * Validates the given OWL 2 spec defined in an OWL file. The path to this
     * file must be provided as a parameter. The method will execute
     * successfully (no exceptions will be thrown) if the OWL 2 file is valid
     * according to the W3C standard. If the OWL 2 spec is not valid, then it
     * will throw a ValidationException. File path is given as this example:
     * "C:\\pizza.owl.xml"
     *
     * @param owl2FilePath The path to a file that contains an OWL spec (OWL/XML
     * | RDF/XML)
     * @throws FileNotFoundException
     * @throws ValidationException
     */
    public static void validateOWL(File owl2File) throws FileNotFoundException, Exception {
        try {
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology o = man.loadOntologyFromOntologyDocument(owl2File);
            if (o.isEmpty()) {
                throw new Exception("Ontology given is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the given OWL 2 spec defined in an OWL file. The path to this
     * file must be provided as a parameter. The method will execute
     * successfully (no exceptions will be thrown) if the OWL 2 file is valid
     * according to the W3C standard. If the OWL 2 spec is not valid, then it
     * will throw a ValidationException. File path is given as this example:
     * "C:\\pizza.owl.xml"
     *
     * @param owl2FilePath The path to a file that contains an OWL spec (OWL/XML
     * | RDF/XML)
     * @throws FileNotFoundException
     * @throws ValidationException
     */
    public static void validateOWL(IRI owl2IRI) throws FileNotFoundException, Exception {
        try {
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology o = man.loadOntologyFromOntologyDocument(owl2IRI);
            if (o.isEmpty()) {
                throw new Exception("Ontology given is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
