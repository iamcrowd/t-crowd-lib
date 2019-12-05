package it.unibz.inf.tdllitefpx;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;

import it.unibz.inf.tdllitefpx.abox.Abox;
import it.unibz.inf.tdllitefpx.abox.AboxConceptAssertion;

import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimeFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;


public class TestAbox1 {
	
	public static void main(String[] args) throws Exception{
		TestAbox1 exTDL = new TestAbox1();
		
	/*	TDLLiteFPXReasoner.buildCheckSatisfiability(
				exTDL.getTBox(),
				true, 
				"Status5");  */
		exTDL.getABox();
		TDLLiteFPXReasoner.buildCheckAboxtSatisfiability(
				exTDL.getTBox(),
				true, 
				"Abox1",exTDL.getABox());
		
		Map<String, String> stats = exTDL.getTBox().getStats();
		
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));
		
		
	}
	

//	Concept Integer = new AtomicConcept("Integer");
	Concept Person = new AtomicConcept("Person");
	//Concept Adult = new AtomicConcept("Adult");
    //Concept Minor = new AtomicConcept("Minor");
	
	Role Name = new PositiveRole(new AtomicRigidRole("Name"));
	//Role Salary = new PositiveRole(new AtomicRigidRole("Salary"));
	//Role Salary = new PositiveRole(new AtomicLocalRole("Salary"));//MOi ici
	//Role TimeStamp_Min = new PositiveRole(new AtomicLocalRole("TimeStamp_Min"));

	
	
	public Abox getABox(){
		
		Abox A = new Abox();
		AboxConceptAssertion a1= new AboxConceptAssertion(new NextFuture(Person),"john");
		Formula ltlfa1 = a1.makeAssertionPropositional();
		A.add(ltlfa1);
		
		AboxConceptAssertion a2= new AboxConceptAssertion(Person,"Maria");
		Formula ltlfa2 = a2.makeAssertionPropositional();
		A.add(ltlfa2);
	
	//	A.addAll(getABox0());
	//	A.addAll(getABox1());
		
		
		return A;
	}
	
	public TBox getTBox(){
		
		TBox t = new TBox();
		t.addAll(getTBoxT0());
	//	t.addAll(getTBoxT1());
		
		//t.addAll(getTBoxExtension());
		
		return t;
	}
	
	private TBox getTBoxT0(){
		TBox t0 = new TBox();

		// D
		
		/*t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Employee)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Manager)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(TopManager)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Project)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(ExProject)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Manages)));*/
		
		// A
		
		/*		t0.add(new ConceptInclusionAssertion(
				new QuantifiedRole(Name.getInverse(), 1),
				Integer));
			t0.add(new ConceptInclusionAssertion(
				new QuantifiedRole(Salary.getInverse(), 1),
				Integer));*/
		
		// a person has one Name
			t0.add(new ConceptInclusionAssertion(
				Person,
				new QuantifiedRole(Name, 1)));
		t0.add(new ConceptInclusionAssertion(
				Person,
				new NegatedConcept(new QuantifiedRole(Name, 2))));
		/*			
		t0.add(new ConceptInclusionAssertion(
				Person,
				new QuantifiedRole(Salary, 1)));
		t0.add(new ConceptInclusionAssertion(
				Person,
				new NegatedConcept(new QuantifiedRole(Salary, 2))));
		*/
		// R
		
		
		// H
		
	//	t0.add(new ConceptInclusionAssertion(
	//		Adulte,
	//			Person));
		
	//	t0.add(new ConceptInclusionAssertion(
		//		Minor,
			//	Person));
		
		return t0;
	}
	
	private TBox getTBoxT1(){
		TBox t1 = new TBox();
		
		// TS
		/* A person is always a Person
		t1.add(new ConceptInclusionAssertion(
				Person,
				new AlwaysFuture(new AlwaysPast(Person))));*/
		

		// TRANS
		
		
		// EVO
		//An adulte stay adulte in the future
		//t1.add(new ConceptInclusionAssertion(
		//Adulte,
		//new AlwaysFuture(Adulte)));
		
		//An adulte was sometime Minor in the past
	//	t1.add(new ConceptInclusionAssertion(
			//	Adulte,
			//	new SometimePast(Minor)));

		// GEN
		
		return t1;
	}
	
	private TBox getTBoxExtension(){
		TBox t = new TBox();
		
		/*t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(Name, 2),
				new QuantifiedRole(Name, 1)
				));
		
		t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(Name, 1),
				new AlwaysPast(new AlwaysFuture(new QuantifiedRole(Name, 1)))));
		t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(Name, 2),
				new AlwaysPast(new AlwaysFuture(new QuantifiedRole(Name, 2)))));*/
		return t;
	}

}

