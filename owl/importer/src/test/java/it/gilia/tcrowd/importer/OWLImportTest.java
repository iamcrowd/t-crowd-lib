package it.gilia.tcrowd.importer;

//import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;


import org.semanticweb.owlapi.model.*;

import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;


@DisplayName("Test Suite")
public class OWLImportTest {
	
    // http://owl.man.ac.uk/2006/07/sssw/people
    // https://protege.stanford.edu/ontologies/pizza/pizza.owl
    @Test
    public void testImportPizza(){
        try {
            IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
			OWLImport importer = new OWLImport();
            importer.load(ontoiri);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("pizza.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testImportPeople(){
        try {
            IRI ontoiri = IRI.create("http://owl.man.ac.uk/2006/07/sssw/people.owl");
			OWLImport importer = new OWLImport();
            importer.load(ontoiri);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("people.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testToyFromFile() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyTestDLLite.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("toyTestDLLite.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testToyDomainRange() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyDomainRange.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("toyDomainRange.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

