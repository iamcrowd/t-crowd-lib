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
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;
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

import org.gario.code.output.SymbolUndefinedException;

/**
 * Random TBox and ABox TDL-Lite with only Future Operators
 * 
 * @author gab
 *
 */
public class TD_LITE_N {

	public static Set <Concept> ConceptsSet = new HashSet<Concept>();
	public static Set<Role> RolesSet = new HashSet<Role>();
	
	public static Concept getABoxConcept( ){
		
		Concept[] ArrayC= new Concept[ConceptsSet.size()];
		ConceptsSet.toArray(ArrayC);
		int index= new Random().nextInt(ArrayC.length);
		
		return ArrayC[index];
	}
	
	public static Role getABoxRole(){
		
		Role[] ArrayR= new Role[RolesSet.size()];
		RolesSet.toArray(ArrayR);
		int index= new Random().nextInt(ArrayR.length);
		
		return ArrayR[index];
	}
	
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
			Concept C1 = getConcept(Lc, N, Q, Pr, Pt);
			Concept C2 = getConcept(Lc, N, Q, Pr, Pt);
		
			t.add(new ConceptInclusionAssertion(C1, C2));
			System.out.println("Tbox: " + C1 + " << " + C2);
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
		int i = (int)(Math.random() * (N)); //return an integer from 0 to N-1
		int q = (int)( 1 + (Math.random() * (Q)));
		
		int op = new Random().nextInt(2);//0 and 1
		//Probability
		int p = new Random().nextInt(10); //0..9
		
		switch (op){
		  case 0:
		//	System.out.println("I'm basic basic");
			  BasicConcept Basic= new AtomicConcept("C" + i);
			  ConceptsSet.add(Basic);  
			  return Basic ;
		    
		  case 1:
			  if (p < (10-Pr)) {
				 // System.out.println("I'm q local");
				  Role L = new PositiveRole(new AtomicLocalRole("L" + i)); 
				  RolesSet.add(L);
				  return new QuantifiedRole(L, q);
		        } else 
		        {
		            
			  //   System.out.println("I'm q rigid");
			      Role G = new PositiveRole(new AtomicRigidRole("G" + i)); 
			      RolesSet.add(G);
			      return new QuantifiedRole(G, q);
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
	public static Concept getConcept(int Lc, int N, int Q, int Pr, int Pt){
		Concept C= new AtomicConcept("C");
		if (Lc == 1){
			Concept BC = getBasicConcepts(N, Q, Pr);
			C = BC;
		}
		else if (Lc == 2){
			Concept CL1=getConcept(1,N,Q, Pr, Pt);
			//Probability
			int p = new Random().nextInt(10);
			if (p < (10-Pt)) {
				int opx = new Random().nextInt(3);
				
				switch (opx){
					case 0:
						//System.out.println("I'm a next");	   
						Concept Cnx = new NextFuture(CL1);
					    C = Cnx;
					case 1: 
					case 2:
        				//System.out.println("I'm a negated");
				        Concept Cn = new NegatedConcept(CL1);
				        C = Cn;
				}
			} 
			else {
				//System.out.println("I'm a temporal");
				Concept Cu = getTemporalConcept(CL1);
				C = Cu;
			}
		}
		
		if (Lc > 2){
			int p = new Random().nextInt(10);// 0..9
	
			if (p < (10-Pt)) {
				  int x = (int)( 1 + (Math.random() * (Lc-2 - 1)));
				  Concept C1 = getConcept(x, N, Q, Pr, Pt);
				  Concept C2 = getConcept(Lc - x - 1, N, Q, Pr, Pt);
				  Concept Cc = new ConjunctiveConcept(C1, C2);
				  C = Cc; 
			}
			else {
				Concept CUu = getTemporalConcept(getConcept(Lc - 1, N, Q, Pr, Pt));
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
		//int n = (int)(Math.random() * 4); //values from 0 to 3
		
		int n = (int)(Math.random() * 2);
	
		switch (n){
		  case 0:
			  return new AlwaysFuture(C);	   
		  case 1:
			  return new SometimeFuture(C);
		}
		return null;
	}
	
	public ABox getABox(int size, int max, int q) throws SymbolUndefinedException{
		//the cardinality
		ABox A = new ABox();
		int i = size;
		while ( i >= 0){
			int f = i;
			int s = (int)(Math.random() * 3);
			int j = new Random().nextInt(max + 1);
			int l = new Random().nextInt(q + 2);
			
			if (ConceptsSet.isEmpty()) {
				s = 2;
			}
			if (RolesSet.isEmpty()) {
				s = (int)(Math.random() * 2);
			}
			
		
			switch (s){
			  case 0:
				//	Concept assertion;
				  System.out.println("concept");
				  Concept Ca = getABoxConcept();	
				  while (j != 0 ){ 
						Concept nexta = new NextFuture(Ca);
						Ca = nexta;
		    			j--;	
				  }
				  ABoxConceptAssertion a= new ABoxConceptAssertion(Ca,"a"+i);
				  A.addConceptsAssertion(a);
				  break;
			  
			  case 1:
	              // negated concept assertion
				  System.out.println("negated concept");
				  Concept Cna =new NegatedConcept(getABoxConcept());	
				  while (j!=0 ){ 
						Concept nextna= new NextFuture(Cna);
						Cna= nextna;
		    			j--;	
				  }
				  ABoxConceptAssertion nega= new ABoxConceptAssertion(Cna,"a"+i);
				  A.addConceptsAssertion(nega);
				  break;
			  
			  case 2:
				  //	Role assertion;	
				  System.out.println("role");
				  while (l!=0 & i > 0  ){//cardinality
					  ABoxRoleAssertion r= new ABoxRoleAssertion(getABoxRole(),"a"+f, "b"+l,j);
					  A.addABoxRoleAssertion(r);
					  l--;
					  i--;
					}
				  
				  break;
			}
			i--;
		}
		A.toString(null);
		return A;
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
