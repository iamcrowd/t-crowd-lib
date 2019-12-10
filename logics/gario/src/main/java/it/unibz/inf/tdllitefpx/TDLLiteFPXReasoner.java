package it.unibz.inf.tdllitefpx;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Term;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TDLLiteFPXReasoner {
	/* TODO: Add stats */

	/***
	 * Verifies the satisfiability of the TBox {@literal t}.
	 * If the option verbose is set, the latex files of the intermediate 
	 * steps are generated.
	 * prefix specifies the names of the files.
	 * @throws Exception 
	 */
	public static void buildCheckSatisfiability(
			TBox t, 
			boolean verbose, 
			String prefix) 
					throws Exception{
		TDLLiteFPXReasoner.buildCheck(t, verbose, prefix, CheckType.satisfiability, null );
	}
	
	/***
	 * Verifies the satisfiability of the concept {@literal c} 
	 * in the tbox {@literal t}.
	 * If the option verbose is set, the latex files of the intermediate 
	 * steps are generated.
	 * prefix specifies the names of the files.
	 */
	public static void buildCheckConceptSatisfiability(
			TBox t,
			Concept c,
			boolean verbose,
			String prefix) 
					throws Exception{
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("Concept",c);
		TDLLiteFPXReasoner.buildCheck(t, verbose, prefix, CheckType.entity_consistency, null);
	}
	
	public static void buildCheckABoxtSatisfiability(
			TBox t,
			boolean verbose,
			String prefix,
			ABox ABox) 
					throws Exception{
		TDLLiteFPXReasoner.buildCheck(t, verbose, prefix, CheckType.Abox_consistency, ABox);
	}
	
	private static void buildCheck(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		// Extends the TBox, adding the delta_R and G
		t.addExtensionConstraints();
		
		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		System.out.print("TBox -> Qtl :");
		start_time = System.currentTimeMillis();
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		/*Here we consider that temporal operator are stricts
		//qtl = qtl.makeTemporalStrict();
		*/
		System.out.println(System.currentTimeMillis()-start_time + "ms");		
		ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		
		if(type == CheckType.Abox_consistency){
			/* Add entity consistency check:
			 * 	This means verifying TBox /\ ABox 
			 * 	for the entity E and a brand new constant c 
			 */
			if(qtl instanceof UniversalFormula){
				
			    Set<Constant> constsABox = ABox.getConstantsABox();
				Set<Constant> consts = qtl.getConstants();
				consts.addAll(constsABox);
				System.out.println("");
				System.out.println("Constants: "+consts);
				
				
			    ABox.addExtensionConstraintsABox(t);
			    
			    Formula o = ABox.getABoxFormula();
			
				qtlABox= new ConjunctiveFormula(qtl,o);
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtl.tex");
		
		/* Here we have past operators so now need
		
		System.out.print("Qtl Z -> Qtl N :");
		start_time = System.currentTimeMillis();
		
		NaturalTranslator natural = new NaturalTranslator(qtl);
		Formula qtl_N= natural.getTranslation();
		
		System.out.println(System.currentTimeMillis()-start_time + "ms");
		
		if(verbose)
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
		*/
		System.out.println("");
		System.out.print("Qtl N -> LTL:");
		start_time = System.currentTimeMillis();
		
		
		Formula ltl = qtlABox.makePropositional();
		System.out.println(System.currentTimeMillis()-start_time + "ms");
		
		
		if(verbose)
			(new LatexDocumentCNF(ltl)).toFile(prefix+"ltl.tex");
		
			System.out.println("Generating NuSMV file...");
			(new NuSMVOutput(ltl)).toFile(prefix+".smv");

			System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
			System.out.println("Num of Propositions: "+ltl.getPropositions().size());		
			
	}

}
