package it.gilia.tcrowd.importer;

//import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Map;

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

    @Test
    public void testDisjoint() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/disjoint.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
            importer.loadFromPath(owlfilepath[1]);
            importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("disjoint.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDisjointUnion() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/disjointUnion.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("disjointUnion.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShouldNotBeParsed() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/shouldNotBeParsed.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("shouldNotBeParsed.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllCI() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyAnimals.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("toyAnimalsTbox.tex");
                (new LatexOutputDocument(importer.getABox())).toFile("toyAnimalsAbox.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFunctionalRoles() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyFunctionalRoles.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            try{
				(new LatexOutputDocument(importer.getTBox())).toFile("toyFunctionalRoles.tex");
			} catch (Exception e) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUobmOne() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/uobmOne.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            importer.dlliteAbox();

            Map<String, Integer> statsABox = importer.getABox().getStatsABox();
            System.out.println("");
            System.out.println("------DLITE ABOX");
            
            String keyA;
            keyA="Concept_Assertions:";
            System.out.println(keyA+ statsABox.get(keyA));
            keyA="Role_Assertions:";
            System.out.println(keyA+ statsABox.get(keyA));
            
            System.out.println("Constants ABox: " + importer.getABox().getConstantsABox().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testToyABoxIndividuals() {
        try {
            String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyDomainRangeSAT.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
            importer.dlliteAbox();

            Map<String, Integer> statsABox = importer.getABox().getStatsABox();
            System.out.println("");
            System.out.println("------DLITE ABOX");
            
            String keyA;
            keyA="Concept_Assertions:";
            System.out.println(keyA+ statsABox.get(keyA));
            keyA="Role_Assertions:";
            System.out.println(keyA+ statsABox.get(keyA));
            
            System.out.println("Constants ABox: " + importer.getABox().getConstantsABox().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

