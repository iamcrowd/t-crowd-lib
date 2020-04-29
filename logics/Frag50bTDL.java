import java.util.Map;
import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;
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


public class Frag50bTDL {
	
	public static void main(String[] args) throws Exception{
		Frag50bTDL exTDL = new Frag50bTDL();
		
		TDLLiteFPXReasoner.buildCheckConceptSatisfiability(
				exTDL.getTBox(),
				exTDL.Employee,
				true, 
				"tdl_50b",false);
		
		Map<String, String> stats = exTDL.getTBox().getStats();
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));

		
	}
	

	Concept Integer = new AtomicConcept("Integer");
	Concept Employee = new AtomicConcept("Employee");
	Concept Manager = new AtomicConcept("Manager");
	Concept TopManager = new AtomicConcept("TopManager");
	Concept Project = new AtomicConcept("Project");
	Concept ExProject = new AtomicConcept("ExProject");
	Concept Manages = new AtomicConcept("Manages");
	
	Role PaySlipNumber = new PositiveRole(new AtomicRigidRole("PaySlipNumber"));
	Role Salary = new PositiveRole(new AtomicLocalRole("Salary"));
	
		
	public TBox getTBox(){
		
		TBox t = new TBox();
		t.addAll(getTBoxT0());
		t.addAll(getTBoxT1());
		
		t.addAll(getTBoxExtension());
		
		return t;
	}
	
	private TBox getTBoxT0(){
		TBox t0 = new TBox();

		// D
		
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Employee)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Manager)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(TopManager)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Project)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(ExProject)));
		t0.add(new ConceptInclusionAssertion(Integer, new NegatedConcept(Manages)));
		
		// A
		
		t0.add(new ConceptInclusionAssertion(
				new QuantifiedRole(PaySlipNumber.getInverse(), 1),
				Integer));
		t0.add(new ConceptInclusionAssertion(
				new QuantifiedRole(Salary.getInverse(), 1),
				Integer));
		
		t0.add(new ConceptInclusionAssertion(
				Employee,
				new QuantifiedRole(PaySlipNumber, 1)));
		t0.add(new ConceptInclusionAssertion(
				Employee,
				new NegatedConcept(new QuantifiedRole(PaySlipNumber, 2))));
		
		t0.add(new ConceptInclusionAssertion(
				Employee,
				new QuantifiedRole(Salary, 1)));
		t0.add(new ConceptInclusionAssertion(
				Employee,
				new NegatedConcept(new QuantifiedRole(Salary, 2))));
		
		// R
		
		
		// H
		
		t0.add(new ConceptInclusionAssertion(
				TopManager,
				Manager));
		
		t0.add(new ConceptInclusionAssertion(
				Manager,
				Employee));
		
		return t0;
	}
	
	private TBox getTBoxT1(){
		TBox t1 = new TBox();
		
		// TS
		t1.add(new ConceptInclusionAssertion(
				Employee,
				new AlwaysFuture(new AlwaysPast(Employee))));
		
		t1.add(new ConceptInclusionAssertion(
				new NegatedConcept(new BottomConcept()),
				new SometimeFuture(new SometimePast(new NegatedConcept(Manager)))));

		// TRANS
		t1.add(new ConceptInclusionAssertion(
				Project,
				new NextFuture(ExProject)));
		
		// EVO
		t1.add(new ConceptInclusionAssertion(
				Manager,
				new AlwaysFuture(Manager)));
		t1.add(new ConceptInclusionAssertion(
				Manager,
				new SometimePast(Employee)));

		// GEN
		
		return t1;
	}
	
	private TBox getTBoxExtension(){
		TBox t = new TBox();
		
		t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(PaySlipNumber, 2),
				new QuantifiedRole(PaySlipNumber, 1)
				));
		t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(Salary, 2),
				new QuantifiedRole(Salary, 1)
				));
		t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(PaySlipNumber, 1),
				new AlwaysPast(new AlwaysFuture(new QuantifiedRole(PaySlipNumber, 1)))));
		t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(PaySlipNumber, 2),
				new AlwaysPast(new AlwaysFuture(new QuantifiedRole(PaySlipNumber, 2)))));
		return t;
	}

}













