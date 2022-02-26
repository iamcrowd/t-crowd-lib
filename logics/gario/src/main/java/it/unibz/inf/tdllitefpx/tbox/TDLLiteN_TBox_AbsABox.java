package it.unibz.inf.tdllitefpx.tbox;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;

import org.gario.code.output.SymbolUndefinedException;

/**
 * Random KB
 * This class aims at randomly generating both TDLLiteN TBox and ABox.
 * 
 * @author
 *
 */
public class TDLLiteN_TBox_AbsABox {

	public static Set<Concept> ConceptsSet = new HashSet<Concept>();
	public static Set<Role> RolesSet = new HashSet<Role>();
	public static Set<Role> RolesSetG = new HashSet<Role>();
	public static Set<Role> RolesSetL = new HashSet<Role>();
	
	public static Concept getABoxConcept( ){
		Concept[] ArrayC = new Concept[ConceptsSet.size()];
		ConceptsSet.toArray(ArrayC);
		int index = new Random().nextInt(ArrayC.length);
		return ArrayC[index];
	}
	
	public static Role getABoxRoleG(){
		if (RolesSetG.isEmpty()) {
			return null;
		}else {	
			Role[] ArrayR = new Role[RolesSetG.size()];
			RolesSetG.toArray(ArrayR);
			int index = new Random().nextInt(ArrayR.length);
			return ArrayR[index];
		}
	}

	public static Role getABoxRoleL(){
		if (RolesSetL.isEmpty()) {
			return null;
		} else {	
			Role[] ArrayR = new Role[RolesSetL.size()];
			RolesSetL.toArray(ArrayR);
			int index = new Random().nextInt(ArrayR.length);
			return ArrayR[index];
		}
	}

	public TBox getFTBox(int size, int Lc, int N, int Q, int Pr, int Pt){
		TBox t = new TBox();
		System.out.println("------TDLITE TBox: size:"+size+" Lc:"+Lc+" N:"+N+" Q:"+Q);
		
		for (int i = 1; i <= size; i++){
			Concept C1 = getFConcept(Lc, N, Q, Pr, Pt);
			Concept C2 = getFConcept(Lc, N, Q, Pr, Pt);
			t.add(new ConceptInclusionAssertion(C1, C2));
			System.out.println(" "+C1+" << "+C2 );
		}
		
		return t;
	}

	public static BasicConcept getBasicConcepts(int N, int Q, int Pr){
		int i = new Random().nextInt(N); //return an integer from 0 to N-1
		int q = (int)( 1 + (Math.random() * (Q)));
	
		int op = new Random().nextInt(2);//0 and 1
		//Probability
		int p = new Random().nextInt(10); //0..9
	
		switch (op){
	  		case 0:
		  		BasicConcept Basic = new AtomicConcept("C" + i);
		  		ConceptsSet.add(Basic);  
		  		return Basic;
	    
	  		case 1:
		  		if (p < (10 - Pr)) {
			  		Role L = new PositiveRole(new AtomicLocalRole("L" + i)); 
			  		RolesSetL.add(L);	
			  		return new QuantifiedRole(L, q);
	        	} else {
		      		Role G = new PositiveRole(new AtomicRigidRole("G" + i)); 
		      		RolesSetG.add(G);
		      		return new QuantifiedRole(G, q);
	        	}
		}
		return null;
	}

	public static Concept getFConcept(int Lc, int N, int Q, int Pr, int Pt){
		Concept C = new AtomicConcept("C");
		if (Lc == 1){
			Concept BC = getBasicConcepts(N, Q, Pr);
			C = BC;
		} else if (Lc == 2) {
			Concept CL1 = getFConcept(1, N, Q, Pr, Pt);
			//Probability
			int p = new Random().nextInt(10);
			if (p < (10 - Pt)) {   
				Concept Cnx = new NextFuture(CL1);
				C = Cnx;
			} else {
				Concept Cu = getFTemporalConcept(CL1);
				C = Cu;
			}
		}
		if (Lc > 2){
			
			int p = new Random().nextInt(10);// 0..9
			if (p < (10 - Pt)) {
				  int x = (int)( 1 + (Math.random() * (Lc-2 - 1)));
				  Concept C1 = getFConcept(x, N, Q, Pr, Pt);
				  Concept C2 = getFConcept(Lc-x-1, N, Q, Pr, Pt);
				  Concept Cc = new ConjunctiveConcept(C1, C2);
				  C = Cc; 
			}else {
				Concept CUu = getFTemporalConcept(getFConcept(Lc-1, N, Q, Pr, Pt));
				C = CUu; 
			}
		}
		return C;
	}

	public static TemporalConcept getFTemporalConcept(Concept C){
		int n = (int)(Math.random() * 2);
		switch (n) {
	  		case 0:
		  		return new AlwaysFuture(C);	   
	  		case 1:
		  		return new SometimeFuture(C);
		}
		return null;
	}
	

	public ABox getABox(int NbAssertion, int N, int sizeInd, int max) throws SymbolUndefinedException{
		ABox A = new ABox();
		int space = (N * sizeInd * max) + (2 * N * sizeInd * sizeInd * max);
		int countR=0;
		
		System.out.println("");
		System.out.println("------TDLITE ABox: Assertions: "+NbAssertion+"  Indiv:"+sizeInd+"  N:"+N+"  T:"+max);
	
		Set<Integer> hs_1 = new HashSet<Integer>(); 
		for (int j = 1; j <= sizeInd; j++) {
			hs_1.add(j); 
		}
	
		int i = 1;
	 	while (i <= NbAssertion){
			int p = new Random().nextInt(space);//0...1
			int j = new Random().nextInt(max); //Timestamp
			int f, g;
		
			if (p <= (N*sizeInd*10)){	 
			  //	Concept assertion;

			  	Concept Basic = getABoxConcept();
			  	Concept Ca = Basic; 
			  
			  	while (j != 0 ){ 
					Concept nexta = new NextFuture(Ca);
					Ca = nexta;
	    			j--;	
				}
			  
				f = new Random().nextInt(sizeInd) + 1;
			  	hs_1.remove(f);
			  	ABoxConceptAssertion a= new ABoxConceptAssertion(Ca,"a"+f); 
			  	A.addConceptsAssertion(a);
				i++;
		 	}else {
			  	countR++;
			  //Role assertion;	
			  	int opr = (int)(Math.random() * 2);
			 /*			  
			 // negated concept assertion
			  BasicConcept Basic2= new AtomicConcept("C"+i);
			  Concept Cna=new NegatedConcept(Basic2);
			  ConceptsSet.add(Cna); 
			  
			  while (j!=0 )//States
				{ 
					Concept nextna= new NextFuture(Cna);
					Cna= nextna;
	    			j--;	
				}
			  f=new Random().nextInt(sizeInd)+1;
			  hs_1.remove(f);
			  AboxConceptAssertion nega= new AboxConceptAssertion(Cna,"a"+f); //Not XXXCna(af);
			  A.addConceptsAssertion(nega);
			  i++;
			  break;
*/		  
				f = new Random().nextInt(sizeInd) + 1;
				hs_1.remove(f);
				g = new Random().nextInt(sizeInd) + 1;
				hs_1.remove(g);
			
			 	if (RolesSetL.isEmpty()) {
					 opr = 0;
				};
			 	
				if (RolesSetG.isEmpty()) {
					opr = 1;
				};

			 	if (RolesSetG.isEmpty() & RolesSetL.isEmpty()) {
					 opr = 2;
				};

			 	countR++;

				switch (opr){	 		    
			  		case 0:
					  ABoxRoleAssertion rr = new ABoxRoleAssertion(getABoxRoleG(),"a"+f, "a"+g, j);
					  ABoxRoleAssertion r0 = new ABoxRoleAssertion(getABoxRoleG(),"a"+f, "a"+g, 0);
					  A.addAbsABoxRoleAssertion(r0);
					  A.addABoxRoleAssertions(rr);
				//	  System.out.print(rr.toString());
					  i++;		    
			  		break;
				     
			  		case 1:     
					   ABoxRoleAssertion rl= new ABoxRoleAssertion(getABoxRoleL(),"a"+f, "a"+g,j);
					   A.addAbsABoxRoleAssertion(rl);
					   A.addABoxRoleAssertions(rl);
					   i++;		    
			  		break;
			  		case 2:
					break;  
				}
			}
		}
		if (hs_1.isEmpty()) {
			System.out.println("ALL Individual");
		}
	 	A.toString(null);
		return A;
	}

	public ABox getUnsatABox(int size, int max, int q) throws SymbolUndefinedException{
	//the cardinality
		ABox A = new ABox();
		int i = size;
		int rand = size/2;

		if (rand == 0){
			int j = new Random().nextInt(max + 1);
		 	Concept Ca = getABoxConcept();	
		 
			while (j!=0 ){ 
				Concept nexta = new NextFuture(Ca);
				Ca = nexta;
 				j--;
			}	
		  	ABoxConceptAssertion a = new ABoxConceptAssertion(Ca, "a" + i);
	      	A.addConceptsAssertion(a);	 
	 	}else {
			int bot = (int)( 1 + (Math.random() * (rand)));
			System.out.println("bot:"+bot);
			
			for (int k = 1; k <= bot; k++){
				int j = new Random().nextInt(max + 1);
				System.out.println("UNSAT");
				Concept Ca = getABoxConcept(); 
				
				while (j != 0 ){ 
					Concept nexta = new NextFuture(Ca);
					Ca = nexta;
	 				j--;	
				}
			  	Concept Cna = new NegatedConcept(Ca);
			  	ABoxConceptAssertion a = new ABoxConceptAssertion(Ca,"a" + i);
			  	ABoxConceptAssertion nota = new ABoxConceptAssertion(Cna,"a" + i);
			  	A.addConceptsAssertion(a);
			  	A.addConceptsAssertion(nota);	
			}
        
			int size1 = size-bot*2;
		
			for (int k = 1; k <= size1; k++){
				int f = k;
				int s = (int)(Math.random() * 3);
				int j= new Random().nextInt(max+1);
				int l= 1+new Random().nextInt(q); // we dont broke cardinality
				if (ConceptsSet.isEmpty()) {s=2;}
				if (RolesSetL.isEmpty()) {s=(int)(Math.random() * 2);}
				
				switch (s){
		  			case 0:
			  			//	Concept assertion;
			  			Concept Ca =getABoxConcept();

			  			while (j != 0 ){ 
							Concept nexta= new NextFuture(Ca);
							Ca = nexta;
	    					j--;	
						}
			  			ABoxConceptAssertion a= new ABoxConceptAssertion(Ca, "a" + k);
			  			A.addConceptsAssertion(a);
			  		break;
		  
		  			case 1:
              			// negated concept assertion
			  			Concept Cna = new NegatedConcept(getABoxConcept());

			 			while (j != 0 ){ 
							Concept nextna = new NextFuture(Cna);
							Cna = nextna;
	    					j--;	
						}
			 			ABoxConceptAssertion nega = new ABoxConceptAssertion(Cna, "a" + k);
			  			A.addConceptsAssertion(nega);
			  			A.addConceptsAssertion(nega);
			  		break;
		  
		  			case 2:
			  			// Role assertion;	
			  			System.out.println("role");
			  			
						while (l != 0 & k <= size1){ 
				  			ABoxRoleAssertion r = new ABoxRoleAssertion(getABoxRoleL(), "a"+f, "b"+l, j);
				  			A.addABoxRoleAssertion(r);
				  			l--;
				  			k++;
						}
			  
			  		break;
				}
				i--;
			}
		}
		A.toString(null);
		return A;
	}

	public TBox getTbox2(){
	
		TBox t2 = new TBox();
		return t2;
	}

}
