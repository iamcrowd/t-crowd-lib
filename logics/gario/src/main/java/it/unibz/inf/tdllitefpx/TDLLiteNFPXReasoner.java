package it.unibz.inf.tdllitefpx;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.PureFutureTranslator;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.temporal.Always;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;

import org.gario.code.output.StatsOutputDocument;

import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.output.aalta.AaltaOutput;
import it.unibz.inf.qtl1.output.pltl.PltlOutput;
import it.unibz.inf.qtl1.output.trpuc.TrpucOutput;

import it.unibz.inf.qtl1.output.FO.FOOutput;



import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Term;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
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
public class TDLLiteNFPXReasoner {

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
			String solver,
			boolean reflexive) 
					throws Exception{
		TDLLiteNFPXReasoner.buildLTLCheck(t, verbose, prefix, 
										 CheckType.Abox_consistency, 
										 ABox, 
										 true,
										 solver,
										 reflexive);
	}
	
	/**
	 * For tdlliteN
	 */
	private static void buildLTLCheck(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox,
			boolean purefuture,
			String solver,
			boolean reflexive) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		System.out.println("TBox|ABox -> Qtl1 -> QTLN -> LTL");
		
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
		
		TDLLiteNFPXConverter conv = new TDLLiteNFPXConverter(t);
		Formula qtl_N = conv.getFormula();
		
		if (!reflexive) {
			qtl_N = qtl_N.makeTemporalStrict();	
		}
		
		Set<Constant> consts = qtl_N.getConstants();
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		
		ABox.getStatsABox();
		
		if(verbose)
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
		
		long end_QTL2QTLN = 0;
		
		long end_ABox = 0;
		
		Formula o = null;
		Formula qtlFO = null;
		
		if(type == CheckType.Abox_consistency){
			/* Add entity consistency check:
			 * 	This means verifying TBox /\ ABox 
			 * 	for the entity E and a brand new constant c 
			 */
		//	if(qtl_N instanceof UniversalFormula){
				
			    Set<Constant> constsABox = ABox.getConstantsABox();
				consts = qtl_N.getConstants();
				consts.addAll(constsABox);
				System.out.println("");
				System.out.println("Constants: "+consts);
				
				
				long start_ABox = System.currentTimeMillis();

			    ABox.addExtensionConstraintsABox(t);

				System.out.println("ABox in Reasoner after extension"+ ABox.toString());
			    
			    o = ABox.getABoxFormula(false);
				
				end_ABox = System.currentTimeMillis() - start_ABox;
				
				// only for FO solvers
				qtlFO = new ConjunctiveFormula(qtl_N, o);
				
				if(verbose)
					(new LatexDocumentCNF(qtlFO)).toFile(prefix+"qtlABoxN.tex");	
				
		//	}else
		//		throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		

		long start_QTLN2LTL = System.currentTimeMillis();
		
		Formula ltl = qtl_N.makePropositional(consts);

		o = o.makePropositional(consts);
		ltl = new ConjunctiveFormula(ltl, o);

		
		long end_QTLN2LTL = System.currentTimeMillis() - start_QTLN2LTL;

		
		System.out.println("Num of Propositions: "+ltl.getPropositions().size());
		
		StatsOutputDocument out;
		
		if(type == CheckType.Abox_consistency) {
			out = new StatsOutputDocument(true);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2QTLN, end_ABox, end_QTLN2LTL, ltl.getPropositions().size());
		} else {
			out = new StatsOutputDocument(false);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2QTLN, end_QTLN2LTL, ltl.getPropositions().size());
		}
			
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
			
			case Constants.pltl:
				System.out.println("Solver" + Constants.pltl);
				(new PltlOutput(ltl)).toFile(prefix+".pltl");
			break;
			
			case Constants.TRPUC:
				System.out.println("Solver" + Constants.TRPUC);
				(new TrpucOutput(ltl)).toFile(prefix+".ltl");
			break;
			
			case Constants.all:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(ltl)).toFile(prefix+".smv");
				System.out.println("Solver" + Constants.Aalta);
				(new AaltaOutput(ltl)).toFile(prefix+".aalta");
				System.out.println("Solver" + Constants.pltl);
				(new PltlOutput(ltl)).toFile(prefix+".pltl");
				System.out.println("Solver" + Constants.TRPUC);
				(new TrpucOutput(ltl)).toFile(prefix+".ltl");
				System.out.println("Generating FO file...");
				(new FOOutput(qtlFO)).toFile(prefix+".tptp");
			break;
			
			default:
			break;
		}

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
	}
	

	
	/** FO - Only Future TBox
	 * 
	 * @param t
	 * @param verbose
	 * @param prefix
	 * @param reflexive
	 * @throws Exception
	 */
	public static void buildFOCheckTBoxSatisfiabilityOnlyFuture(
			TBox t,
			boolean verbose,
			String prefix,
			boolean reflexive) 
					throws Exception{
		TDLLiteNFPXReasoner.buildFOCheckTBoxOnlyFuture(t, 
									  verbose, 
									  prefix, 
									  CheckType.satisfiability, 
									  reflexive);
	}
	
	
	private static void buildFOCheckTBoxOnlyFuture(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			boolean reflexive) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		System.out.println("TBox -> Qtl1");
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
	
		TDLLiteNFPXConverter conv = new TDLLiteNFPXConverter(t);
		Formula qtl_N = conv.getFormula();
		
		Set<Constant> consts = qtl_N.getConstants();
		
		if (!reflexive) {
			qtl_N = qtl_N.makeTemporalStrict();	
		}
		
		if(verbose) 
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) (new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
		
		long start_QTL2PLTL = System.currentTimeMillis();

		Formula ltl = qtl_N.makePropositional(consts);
		
		long end_QTL2PLTL = System.currentTimeMillis() - start_QTL2PLTL;
		
		System.out.println("Num of Propositions: "+ltl.getPropositions().size());		

		if(verbose) 
			(new LatexDocumentCNF(ltl)).toFile(prefix+"pltl.tex");
		
		System.out.println("Generating NuSMV file...");
		(new NuSMVOutput(ltl)).toFile(prefix+".smv");
		(new AaltaOutput(ltl)).toFile(prefix+".aalta");
		
		System.out.println("Solver" + Constants.pltl);
		(new PltlOutput(ltl)).toFile(prefix+".pltl");
		
		System.out.println("Generating FO file...");
		(new FOOutput(qtl_N)).toFile(prefix+".tptp");

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
		
		StatsOutputDocument out;
		out = new StatsOutputDocument(false);
		out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2PLTL, ltl.getPropositions().size());
		
	}
	
}
