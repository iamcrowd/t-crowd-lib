package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;

import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;

import it.unibz.inf.tdllitefpx.tbox.TD_LITE_N_ABox;

public class AbstractionTestRandom {

	public static void main(String[] args) throws Exception {
		TD_LITE_N_ABox ABS = new TD_LITE_N_ABox();
		
		TBox t = new TBox();
		ABox a = new ABox();
		
//		int size, int Lc, int N, int Q, int Pr, int Pt
		
		t = ABS.getFTBox(10,5,2,3,5,5);
		
		(new LatexOutputDocument(t)).toFile("TestAbstraction_TBox.tex");
		
//		int NbAssertion,int N, int sizeInd, int max
		
		//a = ABS.getABox(100500,2,100,5);
		a = ABS.getABox(100,2,2,5);

		TDLLiteNABSFPXReasoner.buildCheckAboxtLTLSatisfiability(
				t, 
				true, 
				"ABS_Test", 
				a,
				"Black");

		Map<String, String> stats = t.getStats();
		System.out.println("");
		System.out.println("------TBOX------");
		String key;
		key = "Basic Concepts:";
		System.out.println(key + stats.get(key));
		key = "Roles:";
		System.out.println(key + stats.get(key));
		key = "CIs:";
		System.out.println(key + stats.get(key));
		System.out.println("------ABOX------");
		Map<String, Integer> statsA = a.getStatsABox();
		key = "Concept_Assertion:";
		System.out.println(key + statsA.get(key));
		key = "Roles_Assertion:";
		System.out.println(key + statsA.get(key));

	}

}


