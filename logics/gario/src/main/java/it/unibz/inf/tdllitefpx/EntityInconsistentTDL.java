package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;


public class EntityInconsistentTDL {

	/**
	 * Generates two examples, one to test consistency (consistent_.smv) and one
	 * to test inconsistency (incons_employee.smv)
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
		ExampleTDL exTDL = new ExampleTDL();
		
		TBox t = exTDL.getTBox();
		
	
		/* Employee \subs TopManager */
		t.add(new ConceptInclusionAssertion(
				exTDL.Employee,
				exTDL.TopManager));

		TDLLiteFPXReasoner.buildCheckConceptSatisfiability(
				t,
				exTDL.ExProject,
				true, 
				"consistent_",false, "Aalta");
		
		TDLLiteFPXReasoner.buildCheckConceptSatisfiability(
				t,
				exTDL.Employee,
				false, 
				"incons_employee",false, "Aalta");
		
		Map<String, String> stats = t.getStats();
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));
		

		
	}
}
