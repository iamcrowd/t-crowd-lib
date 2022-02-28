package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;

public class NameFAbs {

	public static void main(String[] args) throws Exception {
		NameFAbs exTDL = new NameFAbs();

		TDLLiteNABSFPXReasoner.buildCheckTBoxAbsABoxSAT(
				exTDL.getTBox(), 
				true, 
				"NameFAbs", 
				exTDL.getABox()
				);

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
		key = "Concept_Assertions:";
		System.out.println(key + statsA.get(key));
		key = "Role_Assertions:";
		System.out.println(key + statsA.get(key));

	}

	Concept Person = new AtomicConcept("Person");
	Role Name = new PositiveRole(new AtomicRigidRole("Name"));


	public ABox getABox() {
		ABox A = new ABox();
		
		ABoxConceptAssertion a1 = new ABoxConceptAssertion(Person, "A");//t=0;
		ABoxConceptAssertion a2 = new ABoxConceptAssertion(Person, "B");//t=0;
		ABoxConceptAssertion a3 = new ABoxConceptAssertion(Person, "C");//t=0;

		ABoxConceptAssertion a10 = new ABoxConceptAssertion(new NextFuture(Person), "A");//t=1;
		ABoxConceptAssertion a11 = new ABoxConceptAssertion(new NextFuture(Person), "B");//t=1;
		ABoxConceptAssertion a12 = new ABoxConceptAssertion(new NextFuture(Person), "C");//t=1;

		ABoxRoleAssertion a4 = new ABoxRoleAssertion(Name, "A", "Marc", 0);//t=0;
		ABoxRoleAssertion a5 = new ABoxRoleAssertion(Name, "B", "Pipo", 0);//t=0;
		ABoxRoleAssertion a6 = new ABoxRoleAssertion(Name, "C", "Charles", 0);//t=0;
		ABoxRoleAssertion a7 = new ABoxRoleAssertion(Name, "A", "Marc", 1);//t=1;
		ABoxRoleAssertion a8 = new ABoxRoleAssertion(Name, "B", "Pipo", 1);//t=1;
		ABoxRoleAssertion a9 = new ABoxRoleAssertion(Name, "C", "Charles", 1);//t=1;
		
		A.addConceptsAssertion(a1);
		A.addConceptsAssertion(a2);
		A.addConceptsAssertion(a3);
		A.addConceptsAssertion(a10);
		A.addConceptsAssertion(a11);
		A.addConceptsAssertion(a12);

		// this should be checked. Should we add assertions according to type of roles.
		// i.e, if a4 is a rigid role then add it to shiftedRolesAssertions ...

		A.addABoxRoleAssertion(a4);
		A.addABoxRoleAssertion(a5);
		A.addABoxRoleAssertion(a6);
		A.addABoxRoleAssertion(a7);
		A.addABoxRoleAssertion(a8);
		A.addABoxRoleAssertion(a9);

		A.shiftRigidRolesAssertions();
							
		return A;
	}

	public TBox getTBox() {
		TBox t1 = new TBox();

		t1.add(new ConceptInclusionAssertion(Person,new QuantifiedRole(Name, 1)));
		t1.add(new ConceptInclusionAssertion(Person,new NegatedConcept(new QuantifiedRole(Name, 2))));

		return t1;
	}

}


