package it.unibz.inf.tdllitefpx;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.PureFutureTranslator;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.temporal.Always;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysPast;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;

import org.gario.code.output.StatsOutputDocument;

import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.output.aalta.AaltaOutput;
import it.unibz.inf.qtl1.output.pltl.PltlOutput;
import it.unibz.inf.qtl1.output.trpuc.TrpucOutput;

import it.unibz.inf.qtl1.output.fo.FOOutput;



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
	 * @param reflexive {boolean} true is reflexive operators | false for strict operators
	 * @throws Exception 
	 */
	public static void buildCheckSatisfiability(
			TBox t, 
			boolean verbose, 
			String prefix,
			boolean purefuture,
			String solver,
			boolean reflexive) 
					throws Exception{
		
		TDLLiteFPXReasoner.buildCheckTBox(t, verbose, prefix, 
										  CheckType.satisfiability, 
										  null, 
										  purefuture,
										  solver,
										  reflexive);
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
			String solver,
			boolean reflexive) 
					throws Exception{
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("Concept",c);
		TDLLiteFPXReasoner.buildCheckTBox(t, verbose, prefix, 
										  CheckType.entity_consistency, 
										  param, 
										  purefuture,
										  solver,
										  reflexive);
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
			String solver,
			boolean reflexive) throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
		// Extends the TBox, adding the delta_R and G
		//t.addExtensionConstraints();
		
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		
		if (!reflexive) {
			qtl = qtl.makeTemporalStrict();	
		}
		
		Formula qtlX = conv.getEpsilonX();
		Formula qtlWithoutX = conv.getEpsilonWithoutX();
		
		Set<Constant> consts = qtl.getConstants();
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;
		
		if(verbose)
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
		
		if(type == CheckType.entity_consistency){
			/* Add entity consistency check:
			 * 	This means verifying TBox /\ E(c) 
			 * 	for the entity E and a brand new constant c 
			 */
			if(qtl instanceof UniversalFormula){
				Concept c  = (Concept) param.get("Concept");
				String name = c.toString()+"witness";
				//Set<Constant> consts = qtl.getConstants();
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
		Formula qtl_NX;
		Formula qtl_NWX;
		
		if (purefuture) {
			// QTL Z -> QTL N using Pure Future
			//System.out.println("TBox -> Qtl1 -> QTLN -> LTL");
			
			long start_QTL2QTLN = System.currentTimeMillis();
			
			PureFutureTranslator purefutureFormulaX = new PureFutureTranslator(conv.getFormulaToRemovePast());
			qtl_NX = purefutureFormulaX.getPureFutureTranslation();
			
			long end_QTL2QTLN = System.currentTimeMillis() - start_QTL2QTLN;
			
			qtl_N = new ConjunctiveFormula(qtl_NX,qtlWithoutX);
			
			if(verbose)
				(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
			
			
			
			
			// LTL (N)
			
			long start_QTLN2LTL = System.currentTimeMillis();
			
			Formula ltl = qtl_NX.makePropositional(consts);
			Formula ltlNoX = qtlWithoutX.makePropositional();
			
			//ltl = new AlwaysFuture(new ConjunctiveFormula(ltl,ltlNoX));
			ltl = new ConjunctiveFormula(ltl,ltlNoX);
			
			long end_QTLN2LTL = System.currentTimeMillis() - start_QTLN2LTL;
			
			if(verbose)
				(new LatexDocumentCNF(ltl)).toFile(prefix+"ltl.tex");
			
			System.out.println("Generating model LTL file...");
			System.out.println("Num of Propositions: "+ltl.getPropositions().size());
			
			StatsOutputDocument out = new StatsOutputDocument(false);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2QTLN, end_QTLN2LTL, ltl.getPropositions().size());
			
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
					(new FOOutput(qtl_N)).toFile(prefix+".tptp");
				break;
			
				default:
					break;
			}			
			
		}
		else {
			// QTL Z -> PLTL (past operators)
			System.out.println("TBox -> Qtl1 -> PLTL");
			
			long start_QTL2LTL = System.currentTimeMillis();
			
			Formula pltl = qtlX.makePropositional(consts);
			Formula pltlNoX = qtlWithoutX.makePropositional();
			
			pltl = new Always(new ConjunctiveFormula(pltl,pltlNoX));
			
			//Formula pltl = qtl.makePropositional();
			
			long end_QTL2LTL = System.currentTimeMillis() - start_QTL2LTL;
			
			if(verbose)
				(new LatexDocumentCNF(pltl)).toFile(prefix+"pltl.tex");
			
			System.out.println("Generating model PLTL file...");
			
			System.out.println("Solver..." + Constants.NuSMV);
			(new NuSMVOutput(pltl)).toFile(prefix+".smv");
			
			System.out.println("Num of Propositions: "+pltl.getPropositions().size());
			
			StatsOutputDocument out = new StatsOutputDocument(false);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2LTL, pltl.getPropositions().size());
			
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
			String solver,
			boolean reflexive) 
					throws Exception{
		TDLLiteFPXReasoner.buildLTLCheck(t, verbose, prefix, 
										 CheckType.Abox_consistency, 
										 ABox, 
										 true,
										 solver,
										 reflexive);
	}
	
	/**
	 * TBox, Abstract ABox SAT
	 * 			LTL: TBox|ABox -> QTL -> QTLN -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
	 * Check TBox and ABox SAT using only LTL pure future formulae
	 * @param t
	 * @param verbose
	 * @param prefix
	 * @param ABox
	 * @param purefuture
	 * @param solver
	 * @param abs 
	 * @throws Exception
	 */
	public static void buildCheckABoxLTLSatisfiability(
			TBox t,
			boolean verbose,
			String prefix,
			ABox ABox,
			boolean purefuture,
			String solver,
			boolean reflexive,
			boolean Abstract) 
					throws Exception{
		TDLLiteFPXReasoner.buildLTLCheck(t, verbose, prefix, 
										 CheckType.Abox_consistency, 
										 ABox, 
										 true,
										 solver,
										 reflexive,
										 Abstract);
	}
	
	
	
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
		
		// Extends the TBox, adding the delta_R and G
		//t.addExtensionConstraints();
		
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		Formula qtl_N;
		Formula qtl_NX;
		
		if (!reflexive) {
			qtl = qtl.makeTemporalStrict();	
		}
		
		Formula qtlX = conv.getEpsilonX();
		Formula qtlWithoutX = conv.getEpsilonWithoutX();
		
		Set<Constant> consts = qtl.getConstants();
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		
		ABox.getStatsABox();
		
		if(verbose) {
			(new LatexDocumentCNF(qtl)).toFile(prefix+"qtl.tex");
			
		}	

		long start_QTL2QTLN = System.currentTimeMillis();
		
		PureFutureTranslator purefutureFormulaX = new PureFutureTranslator(conv.getFormulaToRemovePast());
		qtl_NX = purefutureFormulaX.getPureFutureTranslation();
		
		long end_QTL2QTLN = System.currentTimeMillis() - start_QTL2QTLN;
		
		qtl_N = new ConjunctiveFormula(qtl_NX,qtlWithoutX);
		
			
		if(verbose)
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");	
		
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
			    
			    o = ABox.getABoxFormula(true);
			
				//qtlABox= new ConjunctiveFormula(qtl_N,o);
				
				end_ABox = System.currentTimeMillis() - start_ABox;
				
				// only for FO solvers
				//qtlFO = new ConjunctiveFormula(new Always(qtl_N), o);
				qtlFO = new ConjunctiveFormula(qtl_N, o);
				
				if(verbose)
					(new LatexDocumentCNF(qtlFO)).toFile(prefix+"qtlABoxN.tex");	
				
		//	}else
		//		throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		

		long start_QTLN2LTL = System.currentTimeMillis();
		
		Formula ltl = qtl_NX.makePropositional(consts);
		Formula ltlnox = qtlWithoutX.makePropositional();
	    ltl = new ConjunctiveFormula(ltl, ltlnox);
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
	
	
	private static void buildLTLCheck(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox,
			boolean purefuture,
			String solver,
			boolean reflexive,
			boolean Abstract) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		System.out.println("TBox|ABox -> Qtl1 -> QTLN -> LTL");
		
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
		
		// Extends the TBox, adding the delta_R and G
		//t.addExtensionConstraints();
		
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		if (!reflexive) {
			qtl = qtl.makeTemporalStrict();	
		}
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		
		if(verbose)
			(new LatexDocumentCNF(qtl)).toFile(prefix+"qtl.tex");
		
		Formula qtl_N;
		
		long start_QTL2QTLN = System.currentTimeMillis();

		// QTL Z -> QTL N using Pure Future
		PureFutureTranslator purefutureFormula = new PureFutureTranslator(qtl);
		qtl_N = purefutureFormula.getPureFutureTranslation();
		
		long end_QTL2QTLN = System.currentTimeMillis() - start_QTL2QTLN;
			
		if(verbose)
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");	
		
		ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		
		long end_ABox = 0;
		
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
				
				
				long start_ABox = System.currentTimeMillis();
				
				System.out.println("No Abstract");

			    ABox.addExtensionConstraintsABox(t, Abstract);
			    
			    Formula o = ABox.getABoxFormula(true);
			
				qtlABox= new ConjunctiveFormula(qtl_N,o);
				
				end_ABox = System.currentTimeMillis() - start_ABox;
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtlNWithABox.tex");

		long start_QTLN2LTL = System.currentTimeMillis();

		Formula ltl = qtlABox.makePropositional();
		
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
				(new FOOutput(qtlABox)).toFile(prefix+".tptp");
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
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		TDLLiteFPXReasoner.buildCheck(t, 
									  verbose, 
									  prefix, 
									  CheckType.Abox_consistency, 
									  ABox,
									  reflexive);
	}
	
	
	private static void buildCheck(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		System.out.println("TBox -> Qtl1 -> PLTL");
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
		// Extends the TBox, adding the delta_R and G
		//t.addExtensionConstraints();
	
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		if (!reflexive) {
			qtl = qtl.makeTemporalStrict();	
		}
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		
		ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		
		long end_ABox = 0;

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
				
				long start_ABox = System.currentTimeMillis();

			    ABox.addExtensionConstraintsABox(t);
			    
			    Formula o = ABox.getABoxFormula(false);
			
				qtlABox = new ConjunctiveFormula(qtl,o);
				
				end_ABox = System.currentTimeMillis() - start_ABox;
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtl.tex");

		long start_QTL2PLTL = System.currentTimeMillis();

		Formula pltl = qtlABox.makePropositional();
		
		long end_QTL2PLTL = System.currentTimeMillis() - start_QTL2PLTL;
		
		System.out.println("Num of Propositions: "+pltl.getPropositions().size());		

		if(verbose)
			(new LatexDocumentCNF(pltl)).toFile(prefix+"pltl.tex");
		
		System.out.println("Generating NuSMV file...");
		(new NuSMVOutput(pltl)).toFile(prefix+".smv");

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
		
		StatsOutputDocument out;
		if(type == CheckType.Abox_consistency) {
			out = new StatsOutputDocument(true);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_ABox, end_QTL2PLTL, pltl.getPropositions().size());
		} else {
			out = new StatsOutputDocument(false);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2PLTL, pltl.getPropositions().size());
		}
			
	}

	/**
	 * TODO: refactor!
	 * 
	 * TBox, ABox SAT
	 * 			PLTL: TBox|ABox -> QTL -> PLTL (NuSMV|NuXMV) Inputs is only future. TBox is extended with also future operators.
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
	
	public static void buildCheckABoxSatisfiabilityF(
			TBox t,
			boolean verbose,
			String prefix,
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		TDLLiteFPXReasoner.buildCheckF(t, 
									  verbose, 
									  prefix, 
									  CheckType.Abox_consistency, 
									  ABox,
									  reflexive);
	}
	
	
	private static void buildCheckF(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		System.out.println("TBox -> Qtl1 -> PLTL");
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
		// Extends the TBox only future
		//t.addExtensionConstraintsF();
	
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		if (!reflexive) {
			qtl = qtl.makeTemporalStrict();	
		}
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		
		ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		
		long end_ABox = 0;

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
				
				long start_ABox = System.currentTimeMillis();

			    ABox.addExtensionConstraintsABox(t);
			    
			    Formula o = ABox.getABoxFormula(false);
			
				qtlABox = new ConjunctiveFormula(qtl,o);
				
				end_ABox = System.currentTimeMillis() - start_ABox;
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtl.tex");

		long start_QTL2PLTL = System.currentTimeMillis();

		Formula pltl = qtlABox.makePropositional();
		
		long end_QTL2PLTL = System.currentTimeMillis() - start_QTL2PLTL;
		
		System.out.println("Num of Propositions: "+pltl.getPropositions().size());		

		if(verbose)
			(new LatexDocumentCNF(pltl)).toFile(prefix+"pltl.tex");
		
		System.out.println("Generating NuSMV file...");
		(new NuSMVOutput(pltl)).toFile(prefix+".smv");

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
		
		StatsOutputDocument out;
		if(type == CheckType.Abox_consistency) {
			out = new StatsOutputDocument(true);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_ABox, end_QTL2PLTL, pltl.getPropositions().size());
		} else {
			out = new StatsOutputDocument(false);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2PLTL, pltl.getPropositions().size());
		}
			
	}
	
	
	/** FO
	 * 
	 * @param t
	 * @param verbose
	 * @param prefix
	 * @param ABox
	 * @param reflexive
	 * @throws Exception
	 */
	public static void buildFOCheckSatisfiability(
			TBox t,
			boolean verbose,
			String prefix,
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		TDLLiteFPXReasoner.buildFOCheck(t, 
									  verbose, 
									  prefix, 
									  CheckType.Abox_consistency, 
									  ABox,
									  reflexive);
	}
	
	
	private static void buildFOCheck(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		System.out.println("TBox|ABox -> Qtl1");
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
		// Extends the TBox, adding the delta_R and G
		//t.addExtensionConstraints();
	
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		if (!reflexive) {
			qtl = qtl.makeTemporalStrict();	
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtl)).toFile(prefix+"qtl.tex");
		
		Formula qtl_N;
		
		long start_QTL2QTLN = System.currentTimeMillis();

		// QTL Z -> QTL N using Pure Future
		PureFutureTranslator purefutureFormula = new PureFutureTranslator(qtl);
		qtl_N = purefutureFormula.getPureFutureTranslation();
		
		long end_QTL2QTLN = System.currentTimeMillis() - start_QTL2QTLN;
			
		if(verbose)
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");	
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		
		ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		
		long end_ABox = 0;

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
				
				long start_ABox = System.currentTimeMillis();

			    ABox.addExtensionConstraintsABox(t);
			    
			    Formula o = ABox.getABoxFormula(true);
			
				qtlABox = new ConjunctiveFormula(qtl_N,o);
				
				end_ABox = System.currentTimeMillis() - start_ABox;
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtlabox.tex");

		System.out.println("Generating FO file...");
		(new FOOutput(qtlABox)).toFile(prefix+".tptp");
		
	}
	
	/** FO - Only Future
	 * 
	 * @param t
	 * @param verbose
	 * @param prefix
	 * @param ABox
	 * @param reflexive
	 * @throws Exception
	 */
	public static void buildFOCheckSatisfiabilityOnlyFuture(
			TBox t,
			boolean verbose,
			String prefix,
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		TDLLiteFPXReasoner.buildFOCheckOnlyFuture(t, 
									  verbose, 
									  prefix, 
									  CheckType.Abox_consistency, 
									  ABox,
									  reflexive);
	}
	
	
	private static void buildFOCheckOnlyFuture(
			TBox t, 
			boolean verbose, 
			String prefix, 
			CheckType type, 
			ABox ABox,
			boolean reflexive) 
					throws Exception{
		long total_time = System.currentTimeMillis();
		long start_time;
		
		System.out.println("TBox|ABox -> Qtl1");
		start_time = System.currentTimeMillis();
		
		long start_tbox2QTL = System.currentTimeMillis();
		// Extends the TBox, adding the delta_R and G
		//t.addExtensionConstraintsF();
	
		TDLLiteFPXConverter conv = new TDLLiteFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		if (!reflexive) {
			qtl = qtl.makeTemporalStrict();	
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtl)).toFile(prefix+"qtl.tex");
		
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
		
		ABox.getStatsABox();
		
		ConjunctiveFormula qtlABox = new ConjunctiveFormula();
		
		long end_ABox = 0;

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
				
				long start_ABox = System.currentTimeMillis();

			    ABox.addExtensionConstraintsABox(t);
			    
			    Formula o = ABox.getABoxFormula(true);
			
				qtlABox = new ConjunctiveFormula(qtl,o);
				
				end_ABox = System.currentTimeMillis() - start_ABox;
				
			}else
				throw new Exception("Undefined consistency check for qtl not in factorized form");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtlABox)).toFile(prefix+"qtlabox.tex");
		
		long start_QTL2PLTL = System.currentTimeMillis();

		Formula pltl = qtlABox.makePropositional();
		
		long end_QTL2PLTL = System.currentTimeMillis() - start_QTL2PLTL;
		
		System.out.println("Num of Propositions: "+pltl.getPropositions().size());		

		if(verbose)
			(new LatexDocumentCNF(pltl)).toFile(prefix+"pltl.tex");
		
		System.out.println("Generating NuSMV file...");
		(new NuSMVOutput(pltl)).toFile(prefix+".smv");
		(new AaltaOutput(pltl)).toFile(prefix+".aalta");
		
		System.out.println("Generating FO file...");
		(new FOOutput(qtlABox)).toFile(prefix+".tptp");

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
		
		StatsOutputDocument out;
		if(type == CheckType.Abox_consistency) {
			out = new StatsOutputDocument(true);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_ABox, end_QTL2PLTL, pltl.getPropositions().size());
		} else {
			out = new StatsOutputDocument(false);
			out.toStatsFile(prefix+"Stats.stats", end_tbox2QTL, end_QTL2PLTL, pltl.getPropositions().size());
		}
		
	}
	
}
