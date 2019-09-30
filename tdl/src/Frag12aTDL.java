import java.util.Map;
import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;


public class Frag12aTDL {
	
	public static void main(String[] args) throws Exception{
		Frag12aTDL exTDL = new Frag12aTDL();
		
		TDLLiteFPXReasoner.buildCheckConceptSatisfiability(
				exTDL.getTBox(),
				exTDL.Employee,
				true, 
				"tdl_12a");
		
		Map<String, String> stats = exTDL.getTBox().getStats();
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));

		
	}
	
	Concept Employee = new AtomicConcept("Employee");
	Concept Manager = new AtomicConcept("Manager");
	
	Concept Project = new AtomicConcept("Project");
	Concept ExProject = new AtomicConcept("ExProject");
	Concept Manages = new AtomicConcept("Manages");
	
	Role man = new PositiveRole(new AtomicLocalRole("man"));
	
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
		
		// A
		
		// R
		
		t0.add(new ConceptInclusionAssertion(
				Manages, new QuantifiedRole(man, 1)));
		
		// H
		
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
		

		// TRANS
		/*t1.add(new ConceptInclusionAssertion(
				Project,
				new NextFuture(ExProject)));*/
		
		// EVO
		/*t1.add(new ConceptInclusionAssertion(
				Manager,
				new AlwaysFuture(Manager)));*/
		t1.add(new ConceptInclusionAssertion(
				Manager,
				new SometimePast(Employee)));

		// GEN
		
		return t1;
	}
	
	private TBox getTBoxExtension(){
		TBox t = new TBox();
		
		
		
		return t;
	}

}













