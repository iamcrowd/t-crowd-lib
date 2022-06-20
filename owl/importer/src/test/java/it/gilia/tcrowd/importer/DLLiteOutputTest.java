package it.gilia.tcrowd.importer;


import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.junit.jupiter.api.*;

import java.util.Map;

import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.dllite.DLLiteConverter;
import it.unibz.inf.dllite.DLLiteReasoner;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.FO.SMTOutput;

@DisplayName("Test Suite")
public class DLLiteOutputTest {
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

			// Convert TBox to QTL
			DLLiteConverter conv = new DLLiteConverter(importer.getTBox());
			Formula tbox_formula = conv.getFormula();

			Formula abox_formula;
			importer.getABox().addExtensionConstraintsABox(importer.getTBox());
			abox_formula = importer.getABox().getABoxFormula(false);

			Formula qtlf = new ConjunctiveFormula(tbox_formula, abox_formula);
			(new SMTOutput(qtlf)).toFile("smt_adultSAT_qtl.pltl");

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

			// Convert TBox to QTL
			DLLiteConverter conv = new DLLiteConverter(importer.getTBox());
			Formula tbox_formula = conv.getFormula();

			Formula abox_formula;
			importer.getABox().addExtensionConstraintsABox(importer.getTBox());
			abox_formula = importer.getABox().getABoxFormula(false);

			Formula qtlf = new ConjunctiveFormula(tbox_formula, abox_formula);
			(new SMTOutput(qtlf)).toFile("smt_studentSAT_qtl.pltl");


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

			// Convert TBox to QTL
			DLLiteConverter conv = new DLLiteConverter(importer.getTBox());
			Formula tbox_formula = conv.getFormula();

			Formula abox_formula;
			importer.getABox().addExtensionConstraintsABox(importer.getTBox());
			abox_formula = importer.getABox().getABoxFormula(false);

			Formula qtlf = new ConjunctiveFormula(tbox_formula, abox_formula);
			(new SMTOutput(qtlf)).toFile("smt_studentUNSAT_qtl.pltl");

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


