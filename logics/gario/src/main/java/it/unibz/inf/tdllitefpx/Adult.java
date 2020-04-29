package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;

public class Adult {

	public static void main(String[] args) throws Exception {
		Adult exTDL = new Adult();

		TDLLiteFPXReasoner.buildCheckABoxtSatisfiability(
				exTDL.getTBox(), 
				true, 
				"Adult", 
				exTDL.getABox(),false);

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
	Concept Adult = new AtomicConcept("Adult");
	Concept Minor = new AtomicConcept("Minor");

	// Role Name = new PositiveRole(new AtomicRigidRole("Name"));
	// Role Salary = new PositiveRole(new AtomicRigidRole("Salary"));
	//Role Salary = new PositiveRole(new AtomicLocalRole("Salary"));// MOi ici
	// Role TimeStamp_Min = new PositiveRole(new AtomicLocalRole("TimeStamp_Min"));

	public ABox getABox() {
		//insert John: Minor, Adult, Minor, Adult, Adult, Adult
		ABox A = new ABox();
		//AboxConceptAssertion a1= new AboxConceptAssertion(Person,"John");//t=0;
		
		ABoxConceptAssertion a2= new ABoxConceptAssertion(Minor,"John");//t=0;
		ABoxConceptAssertion a3= new ABoxConceptAssertion(new NextFuture(Adult),"John");//t=1;
		ABoxConceptAssertion a4= new ABoxConceptAssertion(new NextFuture((new NextFuture(Minor))),"John");//t=2;
		ABoxConceptAssertion a5= new ABoxConceptAssertion(new NextFuture((new NextFuture(new NextFuture(Adult)))),"John");//t=3;
		ABoxConceptAssertion a6= new ABoxConceptAssertion(new NextFuture((new NextFuture(new NextFuture(new NextFuture(Adult))))),"John");//t=4;
		ABoxConceptAssertion a7= new ABoxConceptAssertion(new NextFuture((new NextFuture(new NextFuture(new NextFuture(new NextFuture(Adult)))))),"John");//t=5;
		

		//A.addConceptsAssertion(a1);
		A.addConceptsAssertion(a2);
		A.addConceptsAssertion(a3);
		A.addConceptsAssertion(a4);
		A.addConceptsAssertion(a5);
		A.addConceptsAssertion(a6);
		A.addConceptsAssertion(a7);
					
		
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

		// D

		/*
		 * t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Employee)));
		 * t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Manager)));
		 * t0.add(new ConceptInclusionAssertion(Integer, new
		 * NegatedConcept(TopManager))); t0.add(new ConceptInclusionAssertion(Integer,
		 * new NegatedConcept(Project))); t0.add(new ConceptInclusionAssertion(Integer,
		 * new NegatedConcept(ExProject))); t0.add(new
		 * ConceptInclusionAssertion(Integer, new NegatedConcept(Manages)));
		 */

		// A

		/*
		 * t0.add(new ConceptInclusionAssertion( new QuantifiedRole(Name.getInverse(),
		 * 1), Integer)); t0.add(new ConceptInclusionAssertion( new
		 * QuantifiedRole(Salary.getInverse(), 1), Integer));
		 */

		// a person has one Name
		/*
		 * t0.add(new ConceptInclusionAssertion( Person, new QuantifiedRole(Name, 1)));
		 * t0.add(new ConceptInclusionAssertion( Person, new NegatedConcept(new
		 * QuantifiedRole(Name, 2))));
		 */
		//t0.add(new ConceptInclusionAssertion(Person, new QuantifiedRole(Salary, 1)));
		//t0.add(new ConceptInclusionAssertion(Person, new NegatedConcept(new QuantifiedRole(Salary, 2))));

		// R

		// H

		//t0.add(new ConceptInclusionAssertion(Adult,Person));
		//t0.add(new ConceptInclusionAssertion(Minor,Person));

		return t0;
	}

	private TBox getTBoxT1() {
		TBox t1 = new TBox();

		// TS
		/*
		 * A person is always a Person t1.add(new ConceptInclusionAssertion( Person, new
		 * AlwaysFuture(new AlwaysPast(Person))));
		 */

		// TRANS

		// EVO
		// EVO
		//An adult stay adult in the future
		t1.add(new ConceptInclusionAssertion(Adult,new AlwaysFuture(Adult)));
		
		//An adult was sometime Minor in the past
		t1.add(new ConceptInclusionAssertion(Adult,new NegatedConcept(Minor)));

		// GEN

		return t1;
	}

	private TBox getTBoxExtension() {
		TBox t = new TBox();

		/*
		 * t.add(new ConceptInclusionAssertion( new QuantifiedRole(Name, 2), new
		 * QuantifiedRole(Name, 1) ));
		 * 
		 * t.add(new ConceptInclusionAssertion( new QuantifiedRole(Name, 1), new
		 * AlwaysPast(new AlwaysFuture(new QuantifiedRole(Name, 1))))); t.add(new
		 * ConceptInclusionAssertion( new QuantifiedRole(Name, 2), new AlwaysPast(new
		 * AlwaysFuture(new QuantifiedRole(Name, 2)))));
		 */
		return t;
	}

}


