package it.gilia.tcrowd.importer;

//import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;


import org.semanticweb.owlapi.model.*;


@DisplayName("Test Suite")
public class OWLImportTest {
	
    @Test
    public void testPrintCI(){
        try {
            IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
			OWLImport importer = new OWLImport();
            importer.load(ontoiri);
			importer.dlliteCI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

