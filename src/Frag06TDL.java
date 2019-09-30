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


public class Frag06TDL {
	
	public static void main(String[] args) throws Exception{
		Frag06TDL exTDL = new Frag06TDL();
		
		TDLLiteFPXReasoner.buildCheckConceptSatisfiability(
				exTDL.getTBox(),
				exTDL.Project,
				true, 
				"tdl_06a");
		
		Map<String, String> stats = exTDL.getTBox().getStats();
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));

		
	}
	

	
	Concept Project = new AtomicConcept("Project");
	Concept ExProject = new AtomicConcept("ExProject");
	Concept Manages = new AtomicConcept("Manages");
	
	Role prj = new PositiveRole(new AtomicLocalRole("prj"));
	
		
	public TBox getTBox(){
		
		TBox t = new TBox();
		t.addAll(getTBoxT0());
		t.addAll(getTBoxT1());
		
		t.addAll(getTBoxExtension());
		
		return t;
	}
	
	private TBox getTBoxT0(){
		TBox t0 = new TBox();

		t0.add(new ConceptInclusionAssertion(
				Manages, new QuantifiedRole(prj, 1)));
		t0.add(new ConceptInclusionAssertion(
				new QuantifiedRole(prj, 1),
				Manages));
		t0.add(new ConceptInclusionAssertion(
				new QuantifiedRole(prj,2),
				new BottomConcept()));
		
		return t0;
	}
	
	private TBox getTBoxT1(){
		TBox t1 = new TBox();
		
		return t1;
	}
	
	private TBox getTBoxExtension(){
		TBox t = new TBox();
		
		t.add(new ConceptInclusionAssertion(
				new QuantifiedRole(prj, 2),
				new QuantifiedRole(prj, 1)
				));
		
		return t;
	}

}
