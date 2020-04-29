package it.unibz.inf.tdllitefpx;

import java.util.Map;
import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;

/**
 * A ISA (>= 10 R)
 * B ISA (>= 8 R)
 * C ISA (>= 6 R)
 * D ISA (>= 4 R)
 * E ISA (>= 1 R)
 * @author gab
 *
 */
public class AleCardinalities {
	
	public static void main(String[] args) throws Exception{
		AleCardinalities exCard = new AleCardinalities();
		
		TDLLiteFPXReasoner.buildCheckSatisfiability(
				exCard.getTBox(),
				true, 
				"cardinalities",
				false,
				"NuSMV",true);
		
		Map<String, String> stats = exCard.getTBox().getStats();
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));

		
	}
	

	
	Concept A = new AtomicConcept("A");
	Concept B = new AtomicConcept("B");
	Concept C = new AtomicConcept("C");
	Concept D = new AtomicConcept("D");
	Concept E = new AtomicConcept("E");
	
	Role R = new PositiveRole(new AtomicLocalRole("R"));
	
		
	public TBox getTBox(){
		
		TBox t = new TBox();
		t.addAll(getTBoxT0());
		t.addAll(getTBoxT1());
		
		return t;
	}
	
	private TBox getTBoxT0(){
		TBox t0 = new TBox();

		// Cardinality: a person has no more then 2 Salaries at one moment
		
		t0.add(new ConceptInclusionAssertion(
				A,
				new QuantifiedRole(R, 10)));
		
		t0.add(new ConceptInclusionAssertion(
				B,
				new QuantifiedRole(R, 8)));
		
		t0.add(new ConceptInclusionAssertion(
				C,
				new QuantifiedRole(R, 6)));
		
		t0.add(new ConceptInclusionAssertion(
				D,
				new QuantifiedRole(R, 4)));
		
		t0.add(new ConceptInclusionAssertion(
				E,
				new QuantifiedRole(R, 1)));
		
		
		return t0;
	}
	
	private TBox getTBoxT1(){
		TBox t1 = new TBox();
		
		return t1;
	}

}
