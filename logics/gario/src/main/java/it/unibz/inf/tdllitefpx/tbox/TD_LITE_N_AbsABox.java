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
import it.unibz.inf.tdllitefpx.concepts.temporal.*;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;

import org.gario.code.output.SymbolUndefinedException;

/**
 * Random ABoxes
 * 
 * @author gab
 *
 */
public class TD_LITE_N_AbsABox {

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
	
	public static Role getABoxRole(){
		Role[] ArrayR= new Role[RolesSet.size()];
		RolesSet.toArray(ArrayR);
		int index= new Random().nextInt(ArrayR.length);
		
		return ArrayR[index];
	}
	
	public ABox getABox(int Assertion, int sizeInd, int N, int max) throws SymbolUndefinedException{
		ABox A = new ABox();
		int k = new Random().nextInt(9) + 2;
		int NbAssertion = Assertion;//max*sizeInd*k;
		int space = (N * sizeInd * max) + (2 * N * sizeInd * sizeInd * max);
		
		System.out.println("Total_Assertions: "+NbAssertion+"  Indiv:"+sizeInd+"  N:"+N+"  T:"+max);

		Set<Integer> hs_1 = new HashSet<Integer>(); 
		for (int j = 1; j <= sizeInd; j++) {
			hs_1.add(j); 
		}
	
		int i = 1;
		boolean ass;
		System.out.print("TDLITE ABox: "); 
		int countC = 0;
		int countR = 0;
		int z = 0;
		
		while (i <= NbAssertion){
			int p = new Random().nextInt(space);//0...1
			int j = new Random().nextInt(max); //Timestamp
			int f,g,l;
		
		  	if (p <= (N * sizeInd * 10)){
				l = new Random().nextInt(N); 
				BasicConcept Basic = new AtomicConcept("C" + l);
			  	Concept Ca = Basic;
			  	ConceptsSet.add(Ca);  
			  
			  	while (j!=0 ){ 
					Concept nexta = new NextFuture(Ca);
					Ca = nexta;
	    			j--;	
				}
			  
				f = new Random().nextInt(sizeInd) + 1;
			  	hs_1.remove(f);
			  	ABoxConceptAssertion a = new ABoxConceptAssertion(Ca, "a" + f); 
			  	ass = A.addConceptsAssertion(a);
			   	countC++;

			  	if (ass == true){
				  i++;
				}
		
			} else {
			  	countR++;
			  	//Role assertion;	
			  	int opr = (int)(Math.random() * 2);

			  	Role rr = new PositiveRole(new AtomicRigidRole("R")); 
			  	l = new Random().nextInt(N); 
			  	
				switch (opr){
					case 0:
						Role L = new PositiveRole(new AtomicLocalRole("L" + l)); 
						rr = L;
						RolesSet.add(L);
					break;
				     
				  	case 1:     
					    Role G = new PositiveRole(new AtomicRigidRole("G" + l)); 
					    RolesSet.add(G);
					    rr = G;
					break;
				}
			  
			  	f = new Random().nextInt(sizeInd) + 1;
			  	hs_1.remove(f);
			  	g = new Random().nextInt(sizeInd) + 1;
			  	hs_1.remove(g);
			
			  	ABoxRoleAssertion r = new ABoxRoleAssertion(rr, "a" + f, "a" + g, j);

				if (rr.getRefersTo() instanceof AtomicLocalRole ) {
					ass = A.addABoxRoleAssertions(r);
					A.addAbsABoxRoleAssertion(r);  // here we are invoking the method for abstraction. we need to generalise this.
					if (ass){ 
						A.addABoxLocal(r); 
						i++;
					}
				} else {  
				  	ABoxRoleAssertion r0 = new ABoxRoleAssertion(rr,"a" + f, "a" + g, 0); // should it be done here?
					A.addAbsABoxRoleAssertion(r0); // here we are invoking the method for abstraction. we need to generalise this.
				  	ass = A.addABoxRoleAssertions(r);
				  	if (ass){
						A.addABoxShiftGlobal(r0); 
						i++;
					}
				}
		  	}
		}
		System.out.println("");
		if (hs_1.isEmpty()) {
			System.out.println("ALL Individual are evolved");
		}

	//	 A.toString(null);
		return A;
	}

}
