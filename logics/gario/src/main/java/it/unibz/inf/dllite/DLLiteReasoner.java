package it.unibz.inf.dllite;

import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.output.fo.FOOutput;
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
	 * @param t an TBox
	 * @param verbose true/false 
	 * @param prefix a String
	 * @param solver a String (Black|NuSMV|all)
	 * @throws Exception
	 */
	public static void checkTBoxSat(
			TBox t, 
			boolean verbose, 
			String prefix,
			String solver) 
					throws Exception{

		long total_time = System.currentTimeMillis();
		long start_tbox2QTL = System.currentTimeMillis();
		
		DLLiteConverter conv = new DLLiteConverter(t);
		Formula qtl_N = conv.getFormula();
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;
		long start_QTLN2LTL = System.currentTimeMillis();

		Set<Constant> consts = qtl_N.getConstants();
		if (consts.isEmpty()){
			consts.add(new Constant("witness"));
		}
		
		Formula ltl = qtl_N.makePropositional(consts);
		
		long end_QTLN2LTL = System.currentTimeMillis() - start_QTLN2LTL;

		switch (solver) {
			case Constants.NuSMV:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(ltl)).toFile(prefix+".smv");
			break;

			case Constants.black:
				System.out.println("Solver" + Constants.black);
				(new PltlOutput(ltl)).toFile(prefix+".pltl");
			break;

			case Constants.FO:
				System.out.println("Generating FO file...");
				(new FOOutput(qtl_N)).toFile(prefix+".tptp");
			break;
			
			case Constants.all:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(ltl)).toFile(prefix+".smv");
				System.out.println("Solver" + Constants.black);
				(new PltlOutput(ltl)).toFile(prefix+".pltl");
				System.out.println("Generating FO file...");
				(new FOOutput(qtl_N)).toFile(prefix+".tptp");
			break;
		
			default:
				break;
		}
		
		System.out.println("Num of Propositions: " + ltl.getPropositions().size());
		System.out.println("DLLite to QTL: " + end_tbox2QTL);
		System.out.println("QTL to LTL: " + end_QTLN2LTL);

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
		
		if( verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
			(new LatexDocumentCNF(ltl)).toFile(prefix+"ltl.tex");
		}
	}
	
}
