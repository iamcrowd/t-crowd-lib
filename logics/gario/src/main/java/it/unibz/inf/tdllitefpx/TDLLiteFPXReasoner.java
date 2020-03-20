package it.unibz.inf.tdllitefpx;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.PureFutureTranslator;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;

import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.output.aalta.AaltaOutput;

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


/**
 * TBox SAT
 * 			LTL: TBox -> QTL -> QTLN -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
 * 			PLTL: TBox -> QTL -> PLTL (NuSMV|NuXMV)
 * TBox Concept SAT
 * 			LTL: TBox -> QTL -> QTLN -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
 * 			PLTL: TBox -> QTL -> PLTL (NuSMV|NuXMV)
 * TBox, ABox SAT
 * 			LTL: TBox|ABox -> QTL -> QTLN -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
 * 			PLTL: TBox|ABox -> QTL -> PLTL (NuSMV|NuXMV)
 * @author gab
 *
 */
public class TDLLiteFPXReasoner {
	/* TODO: Add stats */

	/***
	 * TBox SAT
	 * 			LTL: TBox -> QTL -> QTLN -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
	 * 			PLTL: TBox -> QTL -> PLTL (NuSMV|NuXMV)
	 * 
	 * 
	 * Verifies the satisfiability of the TBox {@literal t}.
	 * If the option verbose is set, the latex files of the intermediate 
	 * steps are generated.
	 * prefix specifies the names of the files.
	 * @param purefuture {boolean} to use only future operators
	 * @param solver {string} is one of (NuSMV|Aalta)
	 * @throws Exception 
	 */
	public static void buildCheckSatisfiability(
			TBox t, 
			boolean verbose, 
			String prefix,
			boolean purefuture,
			String solver) 
					throws Exception{
		
		TDLLiteFPXReasoner.buildCheckTBox(t, verbose, prefix, 
										  CheckType.satisfiability, 
										  null, 
										  purefuture,
										  solver);
	}
	
	/***
	 * TBox Concept SAT
	 * 			LTL: TBox -> QTL -> QTLN -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
	 * 			PLTL: TBox -> QTL -> PLTL (NuSMV|NuXMV)
	 * 
	 * 
	 * Verifies the satisfiability of the concept {@literal c} 
	 * in the TBox {@literal t}.
	 * If the option verbose is set, the latex files of the intermediate 
	 * steps are generated.
	 * prefix specifies the names of the files.
	 * @param purefuture {boolean} to use only future operators
	 */
	public static void buildCheckConceptSatisfiability(
			TBox t,
			Concept c,
			boolean verbose,
			String prefix,
			boolean purefuture,
			String solver) 
					throws Exception{
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("Concept",c);
		TDLLiteFPXReasoner.buildCheckTBox(t, verbose, prefix, 
										  CheckType.entity_consistency, 
										  param, 
										  purefuture,
										  solver);
	}
	
	/**
	 * Check TBox for satisfiability. TBox could have past operators
	 * 
	 * @param t a TBox
	 * @param verbose true|false for printing results
	 * @param prefix string for output files
	 * @param type (satisfiability|entity_consistency)
	 * @param param
	 * @param solver (NuSMV|Aalta)
	 * @throws Exception
	 */
	private static void buildCheckTBox(
			TBox t,
			boolean verbose, 
			String prefix, 
			CheckType type, 
			Map<String,Object> param,
			boolean purefuture,
			String solver) throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		start_time = System.currentTimeMillis();
		
		// Extends the TBox, adding the delta_R and G
		t.addExtensionConstraints();
		
		if(verbose)
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
		
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		qtl = qtl.makeTemporalStrict();	
		
		if(type == CheckType.entity_consistency){
			/* Add entity consistency check:
			 * 	This means verifying TBox /\ E(c) 
			 * 	for the entity E and a brand new constant c 
			 */
			if(qtl instanceof UniversalFormula){
				Concept c  = (Concept) param.get("Concept");
				String name = c.toString()+"witness";
				Set<Constant> consts = qtl.getConstants();
				while(consts.contains(new Constant(name))){
					name = name +"0";
				}
			
				Variable x = ((UniversalFormula) qtl).getQuantifiedVar();
				Atom cAtom = (Atom) conv.conceptToFormula(c);
				cAtom.substitute(x, new Constant(name));
			
				qtl = new UniversalFormula(new ConjunctiveFormula(
					qtl.getSubFormulae().get(0),
					cAtom),
					x);
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtl)).toFile(prefix+"qtl.tex");
		
		Formula qtl_N;
		
		if (purefuture) {
			// QTL Z -> QTL N using Pure Future
			System.out.println("TBox -> Qtl1 -> QTLN -> LTL");
			
			System.out.println("TBox -> Qtl1 -> -> LTL?");
			
			PureFutureTranslator purefutureFormula = new PureFutureTranslator(qtl);
			qtl_N = purefutureFormula.getPureFutureTranslation();
			
			if(verbose)
				(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
			
			// LTL (N)
			Formula ltl = qtl_N.makePropositional();
			
			if(verbose)
				(new LatexDocumentCNF(ltl)).toFile(prefix+"ltl.tex");
			
			System.out.println("Generating model LTL file...");
			System.out.println("Num of Propositions: "+ltl.getPropositions().size());
			
			switch (solver) {
				case Constants.NuSMV:
					System.out.println("Solver..." + Constants.NuSMV);
					(new NuSMVOutput(ltl)).toFile(prefix+".smv");
				break;
				
				case Constants.Aalta:
					System.out.println("Solver" + Constants.Aalta);
					(new AaltaOutput(ltl)).toFile(prefix+".aalta");
				break;
			
				default:
					break;
			}			
			
		}
		else {
			// QTL Z -> PLTL (past operators)
			System.out.println("TBox -> Qtl1 -> PLTL");
			
			Formula pltl = qtl.makePropositional();
			
			if(verbose)
				(new LatexDocumentCNF(pltl)).toFile(prefix+"pltl.tex");
			
			System.out.println("Generating model PLTL file...");
			
			System.out.println("Solver..." + Constants.NuSMV);
			(new NuSMVOutput(pltl)).toFile(prefix+".smv");
			
			System.out.println("Num of Propositions: "+pltl.getPropositions().size());
		}
		
		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
					
	}

	
	// Considering TBox and ABox
	
	/**
	 * TBox, ABox SAT
	 * 			LTL: TBox|ABox -> QTL -> QTLN -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
	 * Check TBox and ABox SAT using only LTL pure future formulae
	 * @param t
	 * @param verbose
	 * @param prefix
	 * @param ABox
	 * @param purefuture
	 * @param solver
	 * @throws Exception
	 */
	public static void buildCheckABoxLTLSatisfiability(
			TBox t,
			boolean verbose,
			String prefix,
			ABox ABox,
			boolean purefuture,
			String solver) 
					throws Exception{
		TDLLiteFPXReasoner.buildLTLCheck(t, verbose, prefix, 
										 CheckType.Abox_consistency, 
										 ABox, 
										 true,
										 solver);
	}
	
	private static void buildLTLCheck(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox,
			boolean purefuture,
			String solver) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		// Extends the TBox, adding the delta_R and G
		t.addExtensionConstraints();
		
		System.out.println("TBox|ABox -> Qtl1 -> QTLN -> LTL");
		
		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		start_time = System.currentTimeMillis();
		
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		Formula qtl_N;
		
		// QTL Z -> QTL N using Pure Future
		PureFutureTranslator purefutureFormula = new PureFutureTranslator(qtl);
		qtl_N = purefutureFormula.getPureFutureTranslation();
			
		if(verbose)
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");	
		
		ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		
		if(type == CheckType.Abox_consistency){
			/* Add entity consistency check:
			 * 	This means verifying TBox /\ ABox 
			 * 	for the entity E and a brand new constant c 
			 */
			if(qtl_N instanceof UniversalFormula){
				
			    Set<Constant> constsABox = ABox.getConstantsABox();
				Set<Constant> consts = qtl_N.getConstants();
				consts.addAll(constsABox);
				System.out.println("");
				System.out.println("Constants: "+consts);
				
				
			    ABox.addExtensionConstraintsABox(t);
			    
			    Formula o = ABox.getABoxFormula();
			
				qtlABox= new ConjunctiveFormula(qtl_N,o);
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtl.tex");

	
		Formula ltl = qtlABox.makePropositional();
			
		System.out.println("Num of Propositions: "+ltl.getPropositions().size());		

			
		if(verbose)
			(new LatexDocumentCNF(ltl)).toFile(prefix+"ltl.tex");
			
		switch (solver) {
			case Constants.NuSMV:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(ltl)).toFile(prefix+".smv");
			break;
				
			case Constants.Aalta:
				System.out.println("Solver" + Constants.Aalta);
				(new AaltaOutput(ltl)).toFile(prefix+".aalta");
			break;
			
			default:
			break;
		}

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
			
	}
	
	
	/**
	 * TBox, ABox SAT
	 * 			PLTL: TBox|ABox -> QTL -> PLTL (NuSMV|NuXMV)
	 * 
	 * 
	 * Verifies the KB satisfiability <tbox,abox>.
	 * If the option verbose is set, the latex files of the intermediate 
	 * steps are generated.
	 * prefix specifies the names of the files.
	 * 
	 * @param t an TBox
	 * @param verbose
	 * @param prefix
	 * @param ABox an ABox
	 * @throws Exception
	 */
	
	public static void buildCheckABoxtSatisfiability(
			TBox t,
			boolean verbose,
			String prefix,
			ABox ABox) 
					throws Exception{
		TDLLiteFPXReasoner.buildCheck(t, 
									  verbose, 
									  prefix, 
									  CheckType.Abox_consistency, 
									  ABox);
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
		
		System.out.println("TBox -> Qtl1 -> PLTL");
		
		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		start_time = System.currentTimeMillis();
		
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
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
			
				qtlABox = new ConjunctiveFormula(qtl,o);
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtl.tex");

		Formula pltl = qtlABox.makePropositional();
		System.out.println("Num of Propositions: "+pltl.getPropositions().size());		

		if(verbose)
			(new LatexDocumentCNF(pltl)).toFile(prefix+"pltl.tex");
		
		System.out.println("Generating NuSMV file...");
		(new NuSMVOutput(pltl)).toFile(prefix+".smv");

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
			
	}
	
}
