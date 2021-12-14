package it.unibz.inf.tdllitefpx;

import java.util.Map;


import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;

public class Adult {

	public static void main(String[] args) throws Exception {
		Adult exTDL = new Adult();
		
		TDLLiteFPXReasoner.buildCheckABoxLTLSatisfiability(
				exTDL.getTBox(), 
				true, 
				"Adult", 
				exTDL.getABox(),
				true,
				"Black",
				true);

		Map<String, String> stats = exTDL.getTBox().getStats();
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
		Map<String, String> statsA = exTDL.getABox().getStatsABox();
		key = "Concept_Assertion";
		System.out.println(key + statsA.get(key));
		key = "Roles_Assertion:";
		System.out.println(key + statsA.get(key));

	}

	Concept Adult = new AtomicConcept("Adult");
	Concept Minor = new AtomicConcept("Minor");

	public ABox getABox() {
		ABox A = new ABox();

		ABoxConceptAssertion a2 = new ABoxConceptAssertion(Minor, "Marc");
		
		A.addConceptsAssertion(a2);
		
		return A;
	}

	public TBox getTBox() {
		TBox t1 = new TBox();
		
		t1.add(new ConceptInclusionAssertion(Adult, new SometimePast(Minor)));
		
		return t1;
	}

}


