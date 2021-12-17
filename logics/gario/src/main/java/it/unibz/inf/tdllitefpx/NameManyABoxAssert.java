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
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;

public class NameManyABoxAssert {

	public static void main(String[] args) throws Exception {
		NameManyABoxAssert exTDL = new NameManyABoxAssert();

		TDLLiteFPXReasoner.buildCheckABoxLTLSatisfiability(
				exTDL.getTBox(), 
				true, 
				"NameManyABoxAssert", 
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

	Concept Person = new AtomicConcept("Person");
	Role Name = new PositiveRole(new AtomicRigidRole("Name"));


	public ABox getABox() {
		ABox A = new ABox();
		
		ABoxConceptAssertion a2 = new ABoxConceptAssertion(Person,"John");//t=0;
		ABoxRoleAssertion a3 = new ABoxRoleAssertion(Name,"John", "N0", 1);//t=1;
		ABoxRoleAssertion a4 = new ABoxRoleAssertion(Name,"John", "N0", 2);//t=1;
		ABoxRoleAssertion a6 = new ABoxRoleAssertion(Name,"John", "N0", 3);//t=1;
		ABoxRoleAssertion a8 = new ABoxRoleAssertion(Name,"John", "N0", 4);//t=1;
		ABoxRoleAssertion a9 = new ABoxRoleAssertion(Name,"John", "N0", 5);//t=1;
		ABoxRoleAssertion a10 = new ABoxRoleAssertion(Name,"John", "N0", 6);//t=1;
		ABoxRoleAssertion a11= new ABoxRoleAssertion(Name,"John", "N0", 7);//t=1;
		ABoxRoleAssertion a12 = new ABoxRoleAssertion(Name,"John", "N0", 8);//t=1;
		ABoxRoleAssertion a13 = new ABoxRoleAssertion(Name,"John", "N0", 9);//t=1;
		ABoxRoleAssertion a14 = new ABoxRoleAssertion(Name,"John", "N0", 10);//t=1;
		ABoxRoleAssertion a15 = new ABoxRoleAssertion(Name,"John", "N0", 11);//t=1;
		ABoxRoleAssertion a16 = new ABoxRoleAssertion(Name,"John", "N0", 12);//t=1;
		ABoxRoleAssertion a17 = new ABoxRoleAssertion(Name,"John", "N0", 13);//t=1;
		ABoxRoleAssertion a18 = new ABoxRoleAssertion(Name,"John", "N0", 14);//t=1;
		ABoxRoleAssertion a19 = new ABoxRoleAssertion(Name,"John", "N0", 15);//t=1;
		ABoxRoleAssertion a20 = new ABoxRoleAssertion(Name,"John", "N0", 16);//t=1;
		
		A.addConceptsAssertion(a2);
		A.addABoxRoleAssertion(a3);
		A.addABoxRoleAssertion(a4);
		A.addABoxRoleAssertion(a6);
		A.addABoxRoleAssertion(a8);
		A.addABoxRoleAssertion(a9);
		A.addABoxRoleAssertion(a10);
		A.addABoxRoleAssertion(a11);
		A.addABoxRoleAssertion(a12);
		A.addABoxRoleAssertion(a13);
		A.addABoxRoleAssertion(a14);
		A.addABoxRoleAssertion(a15);
		A.addABoxRoleAssertion(a16);
		A.addABoxRoleAssertion(a17);
		A.addABoxRoleAssertion(a18);
		A.addABoxRoleAssertion(a19);
		A.addABoxRoleAssertion(a20);
							
		return A;
	}

	public TBox getTBox() {
		TBox t1 = new TBox();

		t1.add(new ConceptInclusionAssertion(Person,new QuantifiedRole(Name, 1)));
		t1.add(new ConceptInclusionAssertion(Person,new NegatedConcept(new QuantifiedRole(Name, 2))));

		return t1;
	}

}


