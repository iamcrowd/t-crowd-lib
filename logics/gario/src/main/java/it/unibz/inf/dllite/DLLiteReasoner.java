package it.unibz.inf.dllite;

import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;

import it.unibz.inf.qtl1.output.pltl.PltlOutput;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.Constants;
import it.unibz.inf.tdllitefpx.TDLLiteNFPXConverter;

import java.util.Set;


/**
 * TBox, ABox SAT
 * 			LTL: TBox|ABox -> QTL -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
 * @author gab
 *
 */
public class DLLiteReasoner {

	// Considering TBox and ABox
	
	/**
	 * TBox, ABox SAT
	 * LTL: TBox|ABox -> QTL -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
	 * Check TBox and ABox SAT using only LTL pure future formulae
	 * @param t
	 * @param verbose
	 * @param prefix
	 * @param ABox
	 * @param purefuture
	 * @param solver
	 * @throws Exception
	 */
	public static void buildCheckTBoxSat(
			TBox t,
			boolean verbose,
			String prefix,
			String solver) 
					throws Exception{
		
		
			DLLiteReasoner.buildSat(t, verbose, prefix, solver);
	}
	
	/**
	 * For tdlliteN
	 */
	private static void buildSat(
			TBox t, 
			boolean verbose, 
			String prefix, 
			String solver) 
					throws Exception{

		long total_time = System.currentTimeMillis();
		
		System.out.println("TBox|ABox -> Qtl1 -> LTL");
		
		long start_tbox2QTL = System.currentTimeMillis();
		
		TDLLiteNFPXConverter conv = new TDLLiteNFPXConverter(t);
		Formula qtl_N = conv.getFormula();
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		if(verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
		}
		
		if(verbose)
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
		
		
		long start_QTLN2LTL = System.currentTimeMillis();

		Set<Constant> consts = qtl_N.getConstants();
		
		Formula ltl = qtl_N.makePropositional(consts);
		
		long end_QTLN2LTL = System.currentTimeMillis() - start_QTLN2LTL;

		System.out.println("Num of Propositions: " + ltl.getPropositions().size());
		System.out.println("DLLite to QTL: " + end_tbox2QTL);
		System.out.println("QTL to LTL: " + end_QTLN2LTL);
			
		if(verbose)
			(new LatexDocumentCNF(ltl)).toFile(prefix+"ltl.tex");

			
		System.out.println("Solver" + Constants.pltl);
		(new PltlOutput(ltl)).toFile(prefix+".pltl");

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
	}
	
}
