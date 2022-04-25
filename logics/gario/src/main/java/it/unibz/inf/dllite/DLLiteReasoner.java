package it.unibz.inf.dllite;

import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.output.pltl.PltlOutput;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.Constants;
import it.unibz.inf.tdllitefpx.abox.ABox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.Flow.Processor;


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
			
			case Constants.all:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(ltl)).toFile(prefix+".smv");
				System.out.println("Solver" + Constants.black);
				(new PltlOutput(ltl)).toFile(prefix+".pltl");
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

	// Considering TBox and ABox
	/**
	 * TBox, ABox SAT
	 * LTL: TBox|ABox -> QTL -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
	 * Check TBox and ABox SAT using only LTL pure future formulae
	 * @param t an TBox
	 * @param a an ABox
	 * @param verbose true/false 
	 * @param prefix a String
	 * @param solver a String (Black|NuSMV|all)
	 * @throws Exception
	 */
	public static void checkKB(
			TBox t,
			ABox a, 
			boolean verbose, 
			String prefix,
			String solver) 
					throws Exception{

		long total_time = System.currentTimeMillis();
		long start_tbox2QTL = System.currentTimeMillis();
		
		// Convert TBox to QTL
		DLLiteConverter conv = new DLLiteConverter(t);
		Formula qtl_N = conv.getFormula();
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;
		long start_QTLN2LTL = System.currentTimeMillis();

		/*Formula ltl_for_role = qtl_N.makePropositional(qtl_N.getConstants());
		for(Role role : t.getRoles()){
			if (role instanceof PositiveRole){
				Formula formRole = conv.getFormulaByRole(role);
				Formula ltlR = new ConjunctiveFormula(ltl_for_role, formRole.makePropositional());
				(new NuSMVOutput(ltlR)).toFile(prefix + role.toString() + ".smv");
				String output = runSolver(prefix + role.toString() + ".smv");
				System.out.println("output" + role.toString() + ": " + output);
			}
		}

		System.exit(0); */

		// Get constants
		Set<Constant> constsABox = a.getConstantsABox();
		Set<Constant> consts = qtl_N.getConstants();
		System.out.println("QTL1 constants: " + consts.toString());
		consts.addAll(constsABox);

		// Parse ABox
		a.addExtensionConstraintsABox(t);

		Formula o = a.getABoxFormula(false);
		
		Formula KB = new ConjunctiveFormula(qtl_N, o);
				
		if(verbose)
				(new LatexDocumentCNF(KB)).toFile(prefix+"KB.tex");	

		// Ground the final formula
		Formula ltl = qtl_N.makePropositional(consts);
		o = o.makePropositional(consts);

		Formula ltl_KB = new ConjunctiveFormula(ltl, o);
		
		long end_QTLN2LTL = System.currentTimeMillis() - start_QTLN2LTL;

		switch (solver) {
			case Constants.NuSMV:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(ltl_KB)).toFile(prefix+".smv");
			break;

			case Constants.black:
				System.out.println("Solver" + Constants.black);
				(new PltlOutput(ltl_KB)).toFile(prefix+".pltl");
			break;
			
			case Constants.all:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(ltl_KB)).toFile(prefix+".smv");
				System.out.println("Solver" + Constants.black);
				(new PltlOutput(ltl_KB)).toFile(prefix+".pltl");
				
			/*	int i = 1;
				while (i <= 10){
					String output = runSolver(prefix + ".smv");
					System.out.println("output" + i + ": " + output);
					i++;
				}*/
			break;
		
			default:
				break;
		}
		
		System.out.println("Num of Propositions: " + ltl_KB.getPropositions().size());
		System.out.println("DLLite to QTL: " + end_tbox2QTL);
		System.out.println("QTL to LTL: " + end_QTLN2LTL);

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
		
		if( verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
			(new LatexDocumentCNF(ltl_KB)).toFile(prefix+"ltl.tex");
		}
	}


	private static String runSolver(String file) throws InterruptedException{
		ProcessBuilder processBuilder = new ProcessBuilder();
		System.out.println(file);
		processBuilder.command("/home/gbraun/Documentos/TemporalDLlite/NuXMV/nuXmv", "-dcx", "-bmc", "-bmc_length", "60", file);
		System.out.println("Commando" + processBuilder.command());
	//	processBuilder.command("/home/gbraun/Documentos/TemporalDLlite/NuXMV/nuXmv","-source", "/home/gbraun/Documentos/TemporalDLlite/NuXMV/script.txt", file);
		StringBuilder output;
		try {
			System.out.println("NUxmv process!");
			Process process = processBuilder.start();

/*			while (process.isAlive()){
				output = new StringBuilder();

				BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}
			} */



			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
			/*	if (output.toString().contains("false")){
					System.out.println("SAT");
					return "SAT";
				} else if (output.toString().contains("true")){
					System.out.println("UNSAT");
					return "UNSAT";
				} */
				//System.exit(0);
			} else {
				System.out.println("Process failed!");
			}
			process.destroy();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
