package it.gilia.tcrowd.importer;


import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.junit.jupiter.api.*;

import java.util.Map;

import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.dllite.DLLiteReasoner;

@DisplayName("Test Suite")
public class DLLiteTest {

	@Test
	public void AdultDLLiteSATTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/AdultExampleDLLiteSAT.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("AdultExampleDLLiteSAT.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(), 
				true, 
				"Adult",
				"all");


			Map<String, String> stats = importer.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX------");
			String key;
			key = "Basic Concepts:";
			System.out.println(key + stats.get(key));
			key = "Roles:";
			System.out.println(key + stats.get(key));
			key = "CIs:";
			System.out.println(key + stats.get(key));
		} catch (Exception e) {}
	}

	@Test
	public void AdultDLLiteUNSATTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/AdultExampleDLLiteUNSAT.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("AdultExampleDLLiteUNSAT.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkTBoxSat(
				importer.getTBox(), 
				true, 
				"Adult",
				"all");


			Map<String, String> stats = importer.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX------");
			String key;
			key = "Basic Concepts:";
			System.out.println(key + stats.get(key));
			key = "Roles:";
			System.out.println(key + stats.get(key));
			key = "CIs:";
			System.out.println(key + stats.get(key));
		} catch (Exception e) {}
	}

	@Test
	public void StudentDLLiteTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyDomainRange.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("StudentDLLite.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkTBoxSat(
				importer.getTBox(), 
				true, 
				"Student",
				"all");


			Map<String, String> stats = importer.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX------");
			String key;
			key = "Basic Concepts:";
			System.out.println(key + stats.get(key));
			key = "Roles:";
			System.out.println(key + stats.get(key));
			key = "CIs:";
			System.out.println(key + stats.get(key));
		} catch (Exception e) {}
	}

	@Test
	public void PizzaDLLiteTest(){
		try{
			IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
			OWLImport importer = new OWLImport();
			importer.load(ontoiri);
			importer.dlliteCI();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("pizzaDLLlite.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkTBoxSat(
				importer.getTBox(), 
				true, 
				"Pizza",
				"all");


			Map<String, String> stats = importer.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX------");
			String key;
			key = "Basic Concepts:";
			System.out.println(key + stats.get(key));
			key = "Roles:";
			System.out.println(key + stats.get(key));
			key = "CIs:";
			System.out.println(key + stats.get(key));
		} catch (Exception e) {}
	}

}


