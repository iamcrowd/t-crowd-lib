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
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Flow.Processor;

import javax.management.relation.RoleList;
import javax.sql.RowSetMetaData;


/**
 * TBox, ABox SAT
 * 			LTL: TBox|ABox -> QTL -> LTL (NuSMV|NuXMV|Aalta|pltl|TRP++)
 * @author gab
 *
 */
public class DLLiteReasoner {

	static HashMap<Role, String> RolesSAT = new HashMap<Role, String>();


	/**
	 * Gets the QTL_N formula for a given DL-Lite TBox
	 * @param t
	 * @return
	 */
	private static Formula get_QTLN(TBox t){
		DLLiteConverter conv = new DLLiteConverter(t);
		Formula qtl_N = conv.getFormula();
		return qtl_N;

	}
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

		int nOfThreads = 3;
		long total_time = System.currentTimeMillis();
		long start_tbox2QTL = System.currentTimeMillis();
		
		// Convert TBox to QTL
		DLLiteConverter conv = new DLLiteConverter(t);
		Formula qtl_N = conv.getFormula();
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;
		long start_QTLN2LTL = System.currentTimeMillis();

		Formula ltl_for_role = qtl_N.makePropositional(qtl_N.getConstants());

		try{
			List<Future<String>> unsatRoles = rolesSAT(t, conv, ltl_for_role, nOfThreads);
			Set<String> setOfUNSATroles = new HashSet<String>();

			for(Future<String> role : unsatRoles){
				if (role.get() != null){
					setOfUNSATroles.add(role.get());
				}
			}
			qtl_N = conv.getFormula(setOfUNSATroles);
			if(verbose)
				(new LatexDocumentCNF(qtl_N)).toFile(prefix+"afterRolesSAT.tex");	
		}
		catch (Exception e){
			throw e;
		}

		System.exit(0);


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


	/**
	 * In this method, we use service invokeAll(). First, we put all of the instances into a collable set and then
	 * we run concurrently all of them. The methods ends after executing all of the instances.
	 */
	private static List<Future<String>> rolesSAT(TBox t, DLLiteConverter conv, Formula ltl_roles, Integer nOfThreads) throws Exception{

		ExecutorService service = Executors.newFixedThreadPool(nOfThreads);

		Set<Callable<String>> callables = new HashSet<Callable<String>>();  

		for(Role role : t.getRoles()){
			if (role instanceof PositiveRole){
				callables.add(new ProcessTask(ltl_roles, conv, role, service));
			}
		}

		try{
			java.util.List<Future<String>> futures = service.invokeAll(callables);
			return futures;

			/*for(Future<String> future : futures){  
				System.out.println(future.get());
			}*/
		}
		catch (Exception e){
			System.out.println("Process failed");
			service.shutdownNow(); 
			service.awaitTermination(30, TimeUnit.SECONDS);  
			throw new InterruptedException();
		}

	}

	
	// Abstraction ABox
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
	public static void abstractedKB(
			TBox t,
			ABox a, 
			boolean verbose, 
			String prefix,
			String solver) 
					throws Exception{

		long start_timeNA;
		start_timeNA = System.currentTimeMillis();

		long total_time = System.currentTimeMillis();
		long start_tbox2QTL = System.currentTimeMillis();
		
		// Convert TBox to QTL
		Formula qtl_N = get_QTLN(t);
		
		long end_tbox2QTL = System.currentTimeMillis() - start_tbox2QTL;

		// Get constants
		Set<Constant> constsABox = a.getConstantsABox();
		Set<Constant> consts = qtl_N.getConstants();
		Set<Constant> constsAbs = qtl_N.getConstants();
		System.out.println("QTL1 constants: " + consts.toString());
		consts.addAll(constsABox);

		// Parse ABox
		a.addExtensionConstraintsAbsABox(t);
		System.out.println("");
		System.out.println("------ABox -> FO :");
			
		/* Removed because we should ground considering only the abstracted set of individuals
		Formula o = ABox.getABoxFormula(false);*/
		System.out.println("Size FO ABox: " + a.getABoxSize());
			
		long end_timeNA_QTL =  System.currentTimeMillis()-start_timeNA;
		System.out.println("QTL NA/AA:"+(System.currentTimeMillis()-start_timeNA) + "ms");	
			
		System.out.println("");
		System.out.println("------FO -> Abstract FO :");
			
		long start_time_abs = System.currentTimeMillis();
			
		//calculate the abstraction
		a.AbstractABox();
		Set<Constant> constsABoxAbs = a.getConstantsABoxAbs();
		constsAbs.addAll(constsABoxAbs); 
		Formula aABox = a.getAbstractABoxFormula(false);
			
		long end_time_abs = System.currentTimeMillis()-start_time_abs;
			
		System.out.println("TIME ABS:"+(System.currentTimeMillis()-start_time_abs) + "ms");	
		System.out.print("Qtl N -> LTL:\n");

		long start_timeLTLAA = System.currentTimeMillis();
		long end_timeLTLAA;
	
		// LTL formula by grounding the qlt formula with consts after abstraction
		Formula aKB;

		long end_timeGroundTBox = System.currentTimeMillis();
		Formula aTBox = qtl_N.makePropositional(constsAbs);
		System.out.println("MP TBox:"+(System.currentTimeMillis()-end_timeGroundTBox) + "ms");	

		long end_timeGroundABox = System.currentTimeMillis();
		aABox = aABox.makePropositional(constsAbs);
		System.out.println("MP ABox:"+(System.currentTimeMillis()-end_timeGroundABox) + "ms");	

		aKB = new ConjunctiveFormula(aTBox, aABox);
	
		end_timeLTLAA = System.currentTimeMillis()-start_timeLTLAA;
		System.out.println("QTL->LTL AA:"+(System.currentTimeMillis()-start_timeLTLAA) + "ms");	
	
		System.out.println("tr-timeAA:"+ (end_timeNA_QTL + end_time_abs + end_timeLTLAA) + "ms");

		switch (solver) {
			case Constants.NuSMV:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(aKB)).toFile(prefix+".smv");
			break;

			case Constants.black:
				System.out.println("Solver" + Constants.black);
				(new PltlOutput(aKB)).toFile(prefix+".pltl");
			break;
			
			case Constants.all:
				System.out.println("Solver..." + Constants.NuSMV);
				(new NuSMVOutput(aKB)).toFile(prefix+".smv");
				System.out.println("Solver" + Constants.black);
				(new PltlOutput(aKB)).toFile(prefix+".pltl");
			break;
		
			default:
				break;
		}
		
		System.out.println("Num of Propositions: " + aKB.getPropositions().size());
		System.out.println("DLLite to QTL: " + end_tbox2QTL);
		System.out.println("QTL to LTL: " + end_timeLTLAA);

		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
		
		if( verbose) {
			(new LatexOutputDocument(t)).toFile(prefix+"tbox.tex");
			(new LatexDocumentCNF(qtl_N)).toFile(prefix+"qtlN.tex");
			(new LatexDocumentCNF(aKB)).toFile(prefix+"ltl.tex");
		}
	}

	/**
	 * Builds an abstracted ABox (TBox is empty) given a max cardinality Q
	 * @param ABox
	 * @param Q
	 * @throws Exception
	 */
	public static void buildAbstract(ABox ABox, int Q) throws Exception {
		long total_time = System.currentTimeMillis();
		long start_time;
	
		start_time = System.currentTimeMillis();
		ABox.addExtensionConstraintsAbsABox(Q);
		System.out.println("");
		System.out.println("------ABox -> FO :");	
		//Formula o = ABox.getABoxFormula(false);

		//System.out.println("ABox: " + o.toString());
		
		System.out.println(System.currentTimeMillis()-start_time + "ms");	
		System.out.println("");
		System.out.println("------FO -> Abstract FO :");
		   
		start_time = System.currentTimeMillis();
		ABox.AbstractABox();
		System.out.println(System.currentTimeMillis()-start_time + "ms");
		Formula oAbs = ABox.getAbstractABoxFormula(false); 

		System.out.println("Abstracted ABox: " + oAbs.toString());

		System.out.println("");
		System.out.println("Done! Total time:" + (System.currentTimeMillis()-total_time) + "ms");
	}
	
}