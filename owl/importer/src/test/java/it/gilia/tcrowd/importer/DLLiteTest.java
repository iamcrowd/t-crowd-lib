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
	public void TinyTest(){
		try {
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/tiny.owl").toString());
			String[] owlfilepath = path.split(":", 2);
			OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try {
				(new LatexOutputDocument(importer.getTBox())).toFile("AdultExampleDLLiteSAT.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
					importer.getTBox(),
					importer.getABox(),
					true,
					"AdultSAT",
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
	public void AdultDLLiteSATTest(){
		try {
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/AdultExampleDLLiteSAT.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try {
				(new LatexOutputDocument(importer.getTBox())).toFile("AdultExampleDLLiteSAT.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(), 
				true, 
				"AdultSAT",
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
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("AdultExampleDLLiteUNSAT.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(),
				true, 
				"AdultUNSAT",
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
	public void StudentDLLiteSATTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyDomainRangeSAT.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("StudentDLLiteSAT.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(),
				true, 
				"StudentSAT",
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
	public void StudentDLLiteUNSATTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/toyDomainRangeUNSAT.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("StudentDLLiteUNSAT.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(),
				true, 
				"StudentUNSAT",
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
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("pizzaDLLlite.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(), 
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

	@Test
	public void NDPTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/npd-main-complete.rdf.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("npd.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(),
				true, 
				"npd",
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
	public void MovieTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/movieontology.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("movie.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
				importer.getTBox(),
				importer.getABox(),
				true, 
				"movie",
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
	public void Owl2BenchOneTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/benchmarks/owl2bench-one.owl").toString());
			String[] owlfilepath = path.split(":", 2);
			OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();
			importer.dlliteAbox();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("movie.tex");
			} catch (Exception e) {}

			DLLiteReasoner.checkKB(
					importer.getTBox(),
					importer.getABox(),
					true,
					"movie",
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


