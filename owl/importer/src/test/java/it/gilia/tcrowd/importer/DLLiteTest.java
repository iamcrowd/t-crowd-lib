package it.gilia.tcrowd.importer;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.util.Map;

import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.dllite.DLLiteReasoner;

@DisplayName("Test Suite")
public class DLLiteTest {

	@Test
	public void AdultDLLiteTest(){
		try{
			String path = new String(OWLImport.class.getClassLoader().getResource("ontologies/AdultExampleDLLite.owl").toString());
        	String[] owlfilepath = path.split(":", 2);
        	OWLImport importer = new OWLImport();
			importer.loadFromPath(owlfilepath[1]);
			importer.dlliteCI();

			try{
				(new LatexOutputDocument(importer.getTBox())).toFile("AdultExampleDLLite.tex");
			} catch (Exception e) {}

			DLLiteReasoner.buildCheckTBoxSat(
				importer.getTBox(), 
				true, 
				"Adult", 
				"Black");


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


