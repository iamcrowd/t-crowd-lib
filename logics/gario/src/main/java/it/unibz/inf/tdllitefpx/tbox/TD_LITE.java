package it.unibz.inf.tdllitefpx.tbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.BasicConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.ConjunctiveConcept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.*;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;

public class TD_LITE {
	
/**	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TD_LITE exTDLITE = new TD_LITE();
		
		
		//Scanner sc = new Scanner(System.in);
		//System.out.println("How many basic concepts:");
		//int str = sc.nextInt();
		//System.out.println("You enter : " + str);
		
		TBox t = new TBox();
		t=exTDLITE.getTbox(4,2,4,2,5,5);
	
		(new LatexOutputDocument(t)).toFile("TDLITEtbox.tex");
		
		TDLLiteFPXReasoner.buildCheckSatisfiability(t,true,"Rand",false);
		
		Map<String, String> stats = t.getStats();
		System.out.println("");
		System.out.println("------TBOX------");
		String key;
		key="Basic Concepts:";
		System.out.println(key+ stats.get(key));
		key="Roles:";
		System.out.println(key+ stats.get(key));
		key="CIs:";
		System.out.println(key+ stats.get(key));
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("C:\\Program Files (x86)\\NuSMV-2.6\\bin\\NuSMV.exe", "Rand.smv");
		//processBuilder.redirectOutput().toString();
		try {
			System.out.println("NUSMV process!");
			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
				System.exit(0);
			} else {
				//abnormal...
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
*/
	
	/**
	 * Random generation of TBox TDLLITE
	 * 
	 * @param size
	 * @param Lc
	 * @param N
	 * @param Q
	 * @param Pr
	 * @param Pt
	 * 
	 * @return a TBox
	 */
	public TBox getTbox(int size, int Lc, int N, int Q, int Pr, int Pt){
		TBox t = new TBox();
		
		for (int i = 1; i <= size; i++){
			Concept C1 = getConcept(Lc,N,Q, Pr, Pt);
			Concept C2 = getConcept(Lc,N,Q, Pr, Pt);
		
			t.add(new ConceptInclusionAssertion(C1,C2));
			System.out.println("Tbox: "+C1+" << "+C2 );
		}
		return t;
	}

	/**
	 * 
	 * @param N
	 * @param Q
	 * @param Pr
	 * @return
	 */
	public static BasicConcept getBasicConcepts(int N, int Q, int Pr){
		int i = (int)(Math.random() * (N+1));
		int q = (int)( 1 + (Math.random() * (Q)));
	
		int op = new Random().nextInt(2);
		//Probability
		int p = new Random().nextInt(10);
	
		switch (op){
			case 0: 
				return new AtomicConcept("C"+i); 
			case 1:
				if (p < (10-Pr)) {
					Role L = new PositiveRole(new AtomicLocalRole("L"+i)); 
					return new QuantifiedRole(L,q);
				} else {
					Role G = new PositiveRole(new AtomicRigidRole("G"+i)); 
					return new QuantifiedRole(G,q);
				}
		}
		return null;
	}

	/**
	 * 
	 * @param Lc
	 * @param N
	 * @param Q
	 * @param Pr
	 * @param Pt
	 * @return
	 */
	public static Set <Concept> getConcepts( int Lc, int N,int Q, int Pr, int Pt){
		Set <Concept> Concepts = new HashSet<Concept>();
		for (int i = 1; i <= N; i++){
			Concept C = getConcept(Lc, N,Q, Pr, Pt);
			Concepts.add(C);
		}
		return Concepts;
	}
	
	/**
	 * 
	 * @param Lc
	 * @param N
	 * @param Q
	 * @param Pr
	 * @param Pt
	 * @return
	 */
	public static Concept getConcept(int Lc, int N, int Q, int Pr, int Pt){
		Concept C = new AtomicConcept("C");
		
		if (Lc == 1){
			Concept BC = getBasicConcepts(N,Q, Pr);
			C = BC;	
		} else if (Lc == 2) {
			Concept CL1=getConcept(1,N,Q, Pr, Pt);
			//Probability
			int p = new Random().nextInt(10);
			if (p < (1-Pt)) {
				Concept Cn = new NegatedConcept(CL1);
				C = Cn;
		     } else {
		    	 Concept Cu = getTemporalConcept(CL1);
		    	 C = Cu;
			}
		}
		
		if (Lc > 2){
			int p = new Random().nextInt(10);
	
			if (p < (10 - Pt)) {
				int x = (int)( 1 + (Math.random() * (Lc - 2 - 1)));
				Concept C1 = getConcept(x, N, Q, Pr, Pt);
				Concept C2 = getConcept(Lc - x - 1, N, Q, Pr, Pt);
				Concept Cc = new ConjunctiveConcept(C1,C2);
				C= Cc; 
			} else {
				Concept CUu = getTemporalConcept(getConcept(Lc-1, N, Q, Pr, Pt));
				C = CUu; 
			}
		}
		return C;
	}

	/**
	 * 
	 * @param C
	 * @return
	 */
	public static TemporalConcept getTemporalConcept(Concept C){
		int n = (int)(Math.random() * 6);

		switch (n){
			case 0:
				return new AlwaysFuture(C);	    
			case 1:
				return new AlwaysPast(C);	 
			case 2:
				return new NextFuture(C);		
			case 3:
				return new NextPast(C);
			case 4:
				return new SometimeFuture(C);
			case 5:
				return new SometimePast(C);
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public TBox getTbox2(){
		TBox t2 = new TBox();
		return t2;
	}

}
