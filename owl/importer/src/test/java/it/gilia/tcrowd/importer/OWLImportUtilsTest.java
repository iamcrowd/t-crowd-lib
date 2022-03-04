package it.gilia.tcrowd.importer;

//import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;


import org.semanticweb.owlapi.model.*;


@DisplayName("Test Suite")
public class OWLImportUtilsTest {
	
    @Test
    public void testLoadOntoFromFile() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/movieontology.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.printTBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadOntoFromIRI() {
        try {
            IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
			OWLImport importer = new OWLImport();
            importer.load(ontoiri);
			importer.printTBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

