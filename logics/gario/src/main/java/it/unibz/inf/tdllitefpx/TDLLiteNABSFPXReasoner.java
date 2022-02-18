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
public class TDLLiteNABSFPXReasoner {

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
	public static void buildCheckAboxtLTLSatisfiability(TBox t,boolean verbose,
														String prefix, ABox ABox, 
														String solver) throws Exception {
		//A call for on Both the ABox and the ABstracted ABox		
		TDLLiteNABSFPXReasoner.buildCheckABox(t, verbose, prefix, ABox);
	}

	private static void buildCheckABox(TBox t, boolean verbose, String prefix, 
									   ABox ABox) throws Exception {
		
		long start_timeNA;
		start_timeNA = System.currentTimeMillis();
		
		if(verbose)
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			if (ABox != null) {
				ABox.getStatsABox();
				(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
			}
		//	System.out.println("TBox -> Qtl :");
		//	start_time = System.currentTimeMillis();
			
		TDLLiteNFPXConverter conv = new TDLLiteNFPXConverter(t);
		Formula qtl = conv.getFormula();
			
		//Formula qtlX = conv.getEpsilonX();
		//Formula qtlWithoutX = conv.getEpsilonWithoutX();
		
		// Here we consider that temporal operator are stricts
		//qtl = qtl.makeTemporalStrict();
		
		//	System.out.print(System.currentTimeMillis()-start_time + "ms");		
			
		//ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		// ConjunctiveFormula qtlABoxAbs = new ConjunctiveFormula();
			
		Set<Constant> consts = qtl.getConstants();
		Set<Constant> constsAbs = qtl.getConstants();

		if(verbose)
			(new LatexDocumentCNF(qtl)).toFile(prefix+"qtl.tex");
		
		/* Add Abox consistency check:
		 * 	This means verifying TBox /\ ABox 
		*/
		Set<Constant> constsABox = ABox.getConstantsABox();
		consts.addAll(constsABox);	
				
		if (qtl instanceof ConjunctiveFormula){

			ABox.addExtensionConstraintsAbsABox(t);
			System.out.println("");
			System.out.println("------ABox -> FO :");
				
			Formula o = ABox.getABoxFormula(false);
				
			long end_timeNA_QTL =  System.currentTimeMillis()-start_timeNA;
			System.out.println("QTL NA/AA:"+(System.currentTimeMillis()-start_timeNA) + "ms");	
				
			System.out.println("");
			System.out.println("------FO -> Abstract FO :");
			    
			long start_time_abs = System.currentTimeMillis();
			    
			//calculate the abstraction
			ABox.AbstractABox();
			Set<Constant> constsABoxAbs = ABox.getConstantsABoxAbs();
			constsAbs.addAll(constsABoxAbs); 

			Formula oAbs = ABox.getAbstractABoxFormula(false);
				
			long end_time_abs = System.currentTimeMillis()-start_time_abs;
				
			System.out.println("TIME ABS:"+(System.currentTimeMillis()-start_time_abs) + "ms");	
				
				// only for FO solvers
			Formula qtlFO = new ConjunctiveFormula(qtl, o);
			Formula qtlFOA = new ConjunctiveFormula(qtl, oAbs);

			if(verbose) 
				(new LatexDocumentCNF(qtlFO)).toFile(prefix+"qtlNABox.tex");

			if(verbose) 
				(new LatexDocumentCNF(qtlFOA)).toFile(prefix+"qtlNAbsABox.tex");
				
			System.out.print("Qtl N -> LTL:\n");
		
			long start_timeLTLNA = System.currentTimeMillis();
			long end_timeLTLNA;
		
			// LTL formula by grounding the qtl formula with consts before abstraction
			Formula ltl = qtl.makePropositional(consts);
			o = o.makePropositional(consts);
			ltl = new ConjunctiveFormula(ltl, o);
		
			end_timeLTLNA = System.currentTimeMillis() - start_timeLTLNA;
			System.out.println("QTL->LTL NA:"+(System.currentTimeMillis() - start_timeLTLNA) + "ms");	
		
			long start_timeLTLAA = System.currentTimeMillis();
			long end_timeLTLAA;
		
			// LTL formula by grounding the qlt formula with consts after abstraction
			Formula ltlAbs = qtl.makePropositional(constsAbs);
			oAbs = oAbs.makePropositional(constsAbs);
			ltlAbs = new ConjunctiveFormula(ltlAbs, oAbs);
		
			end_timeLTLAA = System.currentTimeMillis()-start_timeLTLAA;
			System.out.println("QTL->LTL AA:"+(System.currentTimeMillis()-start_timeLTLAA) + "ms");	
		
			System.out.println("tr-timeNA:"+ (end_timeNA_QTL + end_timeLTLNA) + "ms");
		
			System.out.println("tr-timeAA:"+ (end_timeNA_QTL + end_time_abs + end_timeLTLAA) + "ms");
		
			long start_file = 0;
		
			start_file = System.currentTimeMillis();
			
			System.out.println("------Generating NuSMV files...");
			(new NuSMVOutput(ltl)).toFile(prefix+".smv");
			
			System.out.println("time file SMV NA:"+ (System.currentTimeMillis() - start_file) + "ms");
			
			start_file = System.currentTimeMillis();
			
			(new NuSMVOutput(ltlAbs)).toFile(prefix+"ABSTRACT.smv");
			
			System.out.println("time file SMV AA:"+ (System.currentTimeMillis() - start_file) + "ms");
			
			start_file = System.currentTimeMillis();

			System.out.println("------Generating Black files...");
			(new PltlOutput(ltl)).toFile(prefix+".pltl");
			
			System.out.println("time file pltl NA:"+ (System.currentTimeMillis() - start_file) + "ms");
			
			start_file = System.currentTimeMillis();
			
			(new PltlOutput(ltlAbs)).toFile(prefix+"ABSTRACT.pltl");
			
			System.out.println("time file pltl AA:"+ (System.currentTimeMillis() - start_file) + "ms");
			
			System.out.println("Generating FO file...");
			(new FOOutput(qtlFO)).toFile(prefix+".tptp");
			(new FOOutput(qtlFOA)).toFile(prefix+"ABSTRACT.tptp");

//			System.out.println("File Done!:" + (System.currentTimeMillis()-total_time) + "ms");
			System.out.println("Num of Propositions: "+ltl.getPropositions().size());
			System.out.println("Num of Propositions ABSTRACT: "+ltlAbs.getPropositions().size());

		} else
			throw new Exception("qtlN formula is not a ConjunctiveFormula");
	}


	private static void buildAbstract(ABox ABox, int Q) throws Exception {
		
		long total_time = System.currentTimeMillis();
		long start_time;
	
		start_time = System.currentTimeMillis();
		ABox.addExtensionConstraintsAbsABox(Q);
		System.out.println("");
		System.out.println("------ABox -> FO :");
			
		Formula o = ABox.getABoxFormula(false);
		System.out.println("ABox: " + o.toString());
		System.out.println(System.currentTimeMillis()-start_time + "ms");	
		System.out.println("");
		System.out.println("------FO -> Abstract FO :");
		   
		start_time = System.currentTimeMillis();
		ABox.AbstractABox();
		System.out.println(System.currentTimeMillis()-start_time + "ms");
		Formula oAbs =ABox.getAbstractABoxFormula(false); 
		System.out.println("ABox: " + oAbs.toString());

		System.out.println("");
		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
	}
	
}
