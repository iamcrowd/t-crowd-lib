package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;

public class Salary {

	public static void main(String[] args) throws Exception {
		Salary exTDL = new Salary();

		TDLLiteFPXReasoner.buildCheckABoxLTLSatisfiability(
				exTDL.getTBox(), 
				true, 
				"Salary", 
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
		Map<String, Integer> statsA = exTDL.getABox().getStatsABox();
		key = "Concept_Assertion";
		System.out.println(key + statsA.get(key));
		key = "Roles_Assertion:";
		System.out.println(key + statsA.get(key));

	}

	Concept Person = new AtomicConcept("Person");
	Role Salary = new PositiveRole(new AtomicLocalRole("Salary"));


	public ABox getABox() {
		ABox A = new ABox();
		
		ABoxConceptAssertion a2 = new ABoxConceptAssertion(Person,"John");//t=0;
		ABoxRoleAssertion a3 = new ABoxRoleAssertion(Salary,"John", "100000", 1);//t=1;
		
		A.addConceptsAssertion(a2);
		A.addABoxRoleAssertion(a3);
							
		return A;
	}

	public TBox getTBox() {

		TBox t = new TBox();
		t.addAll(getTBoxT0());
		t.addAll(getTBoxT1());

		// t.addAll(getTBoxExtension());

		return t;
	}

	private TBox getTBoxT0() {
		TBox t0 = new TBox();

		return t0;
	}

	private TBox getTBoxT1() {
		TBox t1 = new TBox();

		t1.add(new ConceptInclusionAssertion(Person,new QuantifiedRole(Salary, 1)));
		
		t1.add(new ConceptInclusionAssertion(Person,new NegatedConcept(new QuantifiedRole(Salary, 2))));


		return t1;
	}

}


