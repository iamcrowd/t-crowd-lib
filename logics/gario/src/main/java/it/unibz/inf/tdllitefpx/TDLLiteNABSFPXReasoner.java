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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.security.auth.callback.Callback;


/**
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
	public static void buildCheckTBoxAbsABoxSAT(TBox t, boolean verbose,
														String prefix, ABox ABox
														) throws Exception {
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
			
		TDLLiteNFPXConverter conv = new TDLLiteNFPXConverter(t);
		Formula qtl = conv.getFormula();
			
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
			System.out.println("Size FO ABox: " + ABox.getABoxSize());
				
			long end_timeNA_QTL =  System.currentTimeMillis()-start_timeNA;
			System.out.println("QTL NA/AA:"+(System.currentTimeMillis()-start_timeNA) + "ms");	
				
			System.out.println("");
			System.out.println("------FO -> Abstract FO :");
			    
			long start_time_abs = System.currentTimeMillis();
			    
			//calculate the abstraction
			ABox.AbstractABox();
			Set<Constant> constsABoxAbs = ABox.getConstantsABoxAbs();
			constsAbs.addAll(constsABoxAbs); 
			Formula aABox = ABox.getAbstractABoxFormula(false);
				
			long end_time_abs = System.currentTimeMillis()-start_time_abs;
				
			System.out.println("TIME ABS:"+(System.currentTimeMillis()-start_time_abs) + "ms");	
			System.out.print("Qtl N -> LTL:\n");
		
			
			long start_timeLTLNA = System.currentTimeMillis();
			long end_timeLTLNA;
			
			//LTL formula by grounding the qtl formula with consts before abstraction

			Formula ltl = qtl.makePropositional(consts);
			o = o.makePropositional(consts);
			ltl = new ConjunctiveFormula(ltl, o);
		
			end_timeLTLNA = System.currentTimeMillis() - start_timeLTLNA;
			System.out.println("QTL->LTL NA:"+(System.currentTimeMillis() - start_timeLTLNA) + "ms");
		
			long start_timeLTLAA = System.currentTimeMillis();
			long end_timeLTLAA;
		
			// LTL formula by grounding the qlt formula with consts after abstraction
			Formula aKB;

			long end_timeGroundTBox = System.currentTimeMillis();
			Formula aTBox = qtl.makePropositional(constsAbs);
			System.out.println("MP TBox:"+(System.currentTimeMillis()-end_timeGroundTBox) + "ms");	

			long end_timeGroundABox = System.currentTimeMillis();
			aABox = aABox.makePropositional(constsAbs);
			System.out.println("MP ABox:"+(System.currentTimeMillis()-end_timeGroundABox) + "ms");	

			aKB = new ConjunctiveFormula(aTBox, aABox);
		
			end_timeLTLAA = System.currentTimeMillis()-start_timeLTLAA;
			System.out.println("QTL->LTL AA:"+(System.currentTimeMillis()-start_timeLTLAA) + "ms");	
		
			//System.out.println("tr-timeNA:"+ (end_timeNA_QTL + end_timeLTLNA) + "ms");
		
			System.out.println("tr-timeAA:"+ (end_timeNA_QTL + end_time_abs + end_timeLTLAA) + "ms");
		
			long start_file = 0;
		
			start_file = System.currentTimeMillis();
			
			System.out.println("------Generating NuSMV files...");
			//(new NuSMVOutput(ltl)).toFile(prefix+".smv");
			//System.out.println("time file SMV NA:"+ (System.currentTimeMillis() - start_file) + "ms");
			
			start_file = System.currentTimeMillis();
			
			(new NuSMVOutput(aKB)).toFile(prefix+"ABSTRACT.smv");
			System.out.println("time file SMV AA:"+ (System.currentTimeMillis() - start_file) + "ms");
			
			start_file = System.currentTimeMillis();

			System.out.println("------Generating Black files...");
			//(new PltlOutput(ltl)).toFile(prefix+".pltl");
			//System.out.println("time file pltl NA:"+ (System.currentTimeMillis() - start_file) + "ms");
			
			start_file = System.currentTimeMillis();
			
			(new PltlOutput(aKB)).toFile(prefix+"ABSTRACT.pltl");
			
			System.out.println("time file pltl AA:"+ (System.currentTimeMillis() - start_file) + "ms");

			System.out.println("Num of Propositions: "+ltl.getPropositions().size());
			System.out.println("Num of Propositions ABSTRACT: "+aKB.getPropositions().size());

		} else
			throw new Exception("qtlN formula is not a ConjunctiveFormula");
	}

	public static void justAbsReasoner(TBox t, boolean verbose, String prefix, 
									   ABox ABox) throws Exception {
		
		long start_timeNA;
		start_timeNA = System.currentTimeMillis();
		
		if(verbose)
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			if (ABox != null) {
				ABox.getStatsABox();
				(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
			}
			
		TDLLiteNFPXConverter conv = new TDLLiteNFPXConverter(t);
		Formula qtl = conv.getFormula();
			
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
				
			//Formula o = ABox.getABoxFormula(false);
				
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

			Formula qtlFOA = new ConjunctiveFormula(qtl, oAbs);
			if(verbose) 
				(new LatexDocumentCNF(qtlFOA)).toFile(prefix+"qtlNAbsABox.tex");

		} else
			throw new Exception("qtlN formula is not a ConjunctiveFormula");
	}



	public static void buildAbstract(ABox ABox, int Q) throws Exception {
		
		long total_time = System.currentTimeMillis();
		long start_time;
	
		start_time = System.currentTimeMillis();
		ABox.addExtensionConstraintsAbsABox(Q);
		System.out.println("");
		System.out.println("------ABox -> FO :");	
		Formula o = ABox.getABoxFormula(false);

		//System.out.println("ABox: " + o.toString());
		
		System.out.println(System.currentTimeMillis()-start_time + "ms");	
		System.out.println("");
		System.out.println("------FO -> Abstract FO :");
		   
		start_time = System.currentTimeMillis();
		ABox.AbstractABox();
		System.out.println(System.currentTimeMillis()-start_time + "ms");
		Formula oAbs =ABox.getAbstractABoxFormula(false); 

		//System.out.println("Abstracted ABox: " + oAbs.toString());

		System.out.println("");
		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
	}

	

	public static void concurrentReasoner(TBox t, boolean verbose, String prefix, 
										  ABox ABox) throws Exception {

        //CyclicBarrier bar = new CyclicBarrier(11);

		long start_timeNA;
		start_timeNA = System.currentTimeMillis();
		
		if(verbose)
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
		if (ABox != null) {
			ABox.getStatsABox();
			(new LatexOutputDocument(ABox)).toFile(prefix+"abox.tex");
		}
			
		TDLLiteNFPXConverter conv = new TDLLiteNFPXConverter(t);
		Formula qtl = conv.getFormula();
		
		if(verbose)
			(new LatexDocumentCNF(qtl)).toFile(prefix+"qtl.tex");

		Set<Constant> consts = qtl.getConstants();
		Set<Constant> constsAbs = qtl.getConstants();
		Set<Constant> constsABox = ABox.getConstantsABox();
		consts.addAll(constsABox);
		
		/* Add Abox consistency check:
		 * 	This means verifying TBox /\ ABox 
		*/

		ABox.addExtensionConstraintsAbsABox(t);
		System.out.println("Size FO ABox: " + ABox.getABoxSize());
			
		long end_timeNA_QTL =  System.currentTimeMillis()-start_timeNA;
		System.out.println("QTL NA/AA:"+(System.currentTimeMillis()-start_timeNA) + "ms");	
			
		long start_time_abs = System.currentTimeMillis();
		//calculate the abstraction
		ABox.AbstractABox();
		Set<Constant> constsABoxAbs = ABox.getConstantsABoxAbs();
		constsAbs.addAll(constsABoxAbs); 
		Formula aABox = ABox.getAbstractABoxFormula(false);
			
		long end_time_abs = System.currentTimeMillis()-start_time_abs;
		System.out.println("Time Abstraction:"+(System.currentTimeMillis()-start_time_abs) + "ms");	
	
		long start_timeLTLAA = System.currentTimeMillis();
		long end_timeLTLAA;
	
		// LTL formula by grounding the qlt formula with consts after abstraction
		Formula aKB;
		
		ExecutorService executor = Executors.newFixedThreadPool(2);

		Future<Formula> groundTBox = executor.submit(new Callable<Formula>() {
            public Formula call() {
				Formula ltlTBox = null;
                try {
					ltlTBox = qtl.makePropositional(constsAbs);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                } catch (BrokenBarrierException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return ltlTBox;
            }
        });


		Future<Formula> makePropABox = executor.submit(new Callable<Formula>() {
            public Formula call() {
				Formula ltlABox = null;
                try {
					ltlABox = aABox.makePropositional(constsAbs);	
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                } catch (BrokenBarrierException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return ltlABox;
            }
        });
			
 /*       Thread groundTBox = new Thread(new Runnable() {
            public void run() {
                try {
					long end_timeGroundTBox = System.currentTimeMillis();
					aTBox = qtl.makePropositional(constsAbs);
					System.out.println("MP TBox:"+(System.currentTimeMillis()-end_timeGroundTBox) + "ms");	
                    bar.await();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                } catch (BrokenBarrierException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }); */

/*		Thread makePropABox = new Thread(new Runnable() {
            public void run() {
                try {
					long end_timeGroundABox = System.currentTimeMillis();
					aABox = aABox.makePropositional(constsAbs);
					System.out.println("MP ABox:"+(System.currentTimeMillis()-end_timeGroundABox) + "ms");	
                    bar.await();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                } catch (BrokenBarrierException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }); */
        
     //   makePropABox.start();
     //   bar.await();

		Formula tb = groundTBox.get();
		Formula ab = makePropABox.get();

		executor.shutdown();

		aKB = new ConjunctiveFormula(tb, ab);
		
		end_timeLTLAA = System.currentTimeMillis()-start_timeLTLAA;
		System.out.println("QTL->LTL AA:"+(System.currentTimeMillis()-start_timeLTLAA) + "ms");	
		System.out.println("tr-timeAA:"+ (end_timeNA_QTL + end_time_abs + end_timeLTLAA) + "ms");
		
		long start_file = System.currentTimeMillis();
		
		(new NuSMVOutput(aKB)).toFile(prefix+"ABSTRACT.smv");
		System.out.println("time file SMV AA:"+ (System.currentTimeMillis() - start_file) + "ms");
		
		start_file = System.currentTimeMillis();
		(new PltlOutput(aKB)).toFile(prefix+"ABSTRACT.pltl");
		
		System.out.println("time file pltl AA:"+ (System.currentTimeMillis() - start_file) + "ms");
		System.out.println("Num of Propositions ABSTRACT: "+aKB.getPropositions().size());
    }

}
