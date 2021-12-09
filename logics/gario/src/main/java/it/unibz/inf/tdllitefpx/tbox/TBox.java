package it.unibz.inf.tdllitefpx.tbox;

import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
//import it.unibz.inf.tdllitefpx.concepts.temporal.Always;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimeFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;



public class TBox extends LinkedList<ConceptInclusionAssertion> implements FormattableObj{
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	
	public boolean add(ConceptInclusionAssertion ci){
		isExtended = false; // TODO: Check if this is really the case
		return super.add(ci);
	}
	
	public Set<Role> getRoles(){
		
		HashSet<Role> roles = new HashSet<Role>();
		for(ConceptInclusionAssertion ci: this){
			roles.addAll(ci.getLHS().getRoles());
			roles.addAll(ci.getRHS().getRoles());
		}

		System.out.println("Set of Roles in getRoles() TBox"+roles.toString());
		return roles;
	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		StringBuilder sb = new StringBuilder();
		for (ConceptInclusionAssertion ci: this){
			sb.append(ci.toString(fmt)+
					fmt.getSymbol(this));
		}
		return sb.toString();
	}
	
	public Map<String, String> getStats(){
		HashMap<String, String> stats = new HashMap<String, String>();
		
		stats.put("CIs:", " "+this.size());
		stats.put("Basic Concepts:", " "+this.getAtomicConcepts().size());
		stats.put("Roles:", " "+this.getRoles().size());
		return stats;
	}
	
	public Set<QuantifiedRole> getQuantifiedRoles(){
		Set<Concept> concepts = getAtomicConcepts();
		Set<QuantifiedRole> qR = new HashSet<QuantifiedRole>();
		
		for(Concept c: concepts){
			if(c instanceof QuantifiedRole){
				qR.add((QuantifiedRole) c);
				System.out.println("Qrole in getQuantifiedRoles:"+c.toString());
			}
		}
		
		return qR;
	}
	
	public Map<String, Integer> getQuantifiedRolesQ(Set<QuantifiedRole> qR){
		
		HashMap<String, Integer> qRQ = new HashMap<String, Integer>();
		for (QuantifiedRole qr: qR) {
			
			qRQ.putIfAbsent(qr.getRole().toString(), qr.getQ());
			if(qRQ.get(qr.getRole().toString())<qr.getQ())
			{	
				qRQ.replace(qr.getRole().toString(), qr.getQ());
				
		   }	
		
		}
		System.out.println("getQuantifiedRolesQ in TBox"+qRQ.toString());
		return qRQ;
	}
	
	public Set<Concept> getAtomicConcepts(){
		HashSet<Concept> concepts = new HashSet<Concept>();

		for(ConceptInclusionAssertion ci: this){
			concepts.addAll(ci.getLHS().getBasicConcepts());
			concepts.addAll(ci.getRHS().getBasicConcepts());
		}
		return concepts;
	}
	
	private boolean isExtended = false;
	public boolean isExtended(){
		System.out.println("is extended?"+isExtended);
		return isExtended;}
	
	
	/**
	 * This function rewrite the quantified roles list in order to extend TBox with the right cardinalities
	 * @return
	 */
	public Set<QuantifiedRole> getQuantifiedRoles1(){
		
		Set<QuantifiedRole> qR = getQuantifiedRoles();

		System.out.println("qR in TBox getQuantifiedRoles1:"+qR.toString());

		Set<QuantifiedRole> qR1 = new HashSet<QuantifiedRole>();
		
		for(QuantifiedRole r: qR){

			System.out.println("r in TBox getQuantifiedRoles1:"+qR.toString());

			QuantifiedRole r1 = new QuantifiedRole(r.getRole(), 1);
			System.out.println("Qrole:"+r.toString());
			qR1.add(r);
			qR1.add(r1);
			System.out.println("Qrole1:"+r1.toString());
			}	
		return qR1;
	}

	
	/***
	 * Transforms the TBox into an extended TBox as explained in the report.
	 */
	public void addExtensionConstraints(){
		isExtended = true;
		//Set<QuantifiedRole> qRoles = getQuantifiedRoles();
		Set<QuantifiedRole> qRoles= getQuantifiedRoles1();

		System.out.println("set of qRoles in addExtensionConstraints:"+qRoles.toString());
		
		/* delta: + >qR \subseteq >q'R
		 * for q > q' and >qR, >q'R in T an thre's no q'' s.t. q>q''>q' and q''R \in T
		 */
		Map<Role,List<QuantifiedRole>> qRMap = new HashMap<Role, List<QuantifiedRole>>();
		
		System.out.println("qRMap in addExtensionConstraints "+qRMap.toString());

		for(QuantifiedRole qR : qRoles){

			System.out.println("qR in addExtensionConstraints "+qR.toString());

			List<QuantifiedRole> list = qRMap.get(qR.getRole());

			if(list == null){
				list = new ArrayList<QuantifiedRole>();
				qRMap.put(qR.getRole(),list);
			}
			list.add(qR);
			System.out.println("list of QuantifiedRole in AddExtension"+list.toString());
		}
		
		for(Entry<Role, List<QuantifiedRole>> e: qRMap.entrySet()){
			List<QuantifiedRole> qrL = e.getValue();

			System.out.println("qrL in AddExtension"+qrL.toString());

			Collections.sort(qrL, new Comparator<QuantifiedRole>() {
				@Override
				public int compare(QuantifiedRole o1, QuantifiedRole o2) {
					return o2.getQ()-o1.getQ();
				}
			});
			
			for(int i=0;i<qrL.size()-1;i++){
				this.add(new ConceptInclusionAssertion(
						qrL.get(i),
						qrL.get(i+1)));

				System.out.println("Extending TBox Checking (2) in formula"+qrL.get(i)+" "+qrL.get(i+1));
			}
		}
		
		/* G: + >qR \subseteq BOX >qR 
		 * for >qR \in T an R is rigid role
		 * 
		 * TODO: Avoid duplications
		 */
		for(QuantifiedRole qR : qRoles){

			System.out.println("Exending TBox Checking (3) in formula (roles)"+qR.toString());

			if(qR.getRole().getRefersTo() instanceof AtomicRigidRole){

				System.out.println("Exending TBox Checking (3) in formula (if roles are rigid)"+qR.toString());

				this.add(new ConceptInclusionAssertion(
					qR, 
					new AlwaysFuture(new AlwaysPast(qR))));
				    
			}
		}
		
	}
	
	/***
	 * Transforms the TBox into a pure future extended TBox as explained in the report.
	 * 
	 * Here we extend the rigid roles but considering only future operators. \diamodF qr -> \boxF qr
	 * 
	 * TODO: refactor these methods
	 */
	public void addExtensionConstraintsF(){
		isExtended = true;
		//System.out.println("is extended:"+isExtended);
		//Set<QuantifiedRole> qRoles = getQuantifiedRoles();
		Set<QuantifiedRole> qRoles= getQuantifiedRoles1();
		
		/* delta: + >qR \subseteq >q'R
		 * for q > q' and >qR, >q'R in T an thre's no q'' s.t. q>q''>q' and q''R \in T
		 */
		Map<Role,List<QuantifiedRole>> qRMap = new HashMap<Role, List<QuantifiedRole>>();
		
		
		for(QuantifiedRole qR : qRoles){
			List<QuantifiedRole> list = qRMap.get(qR.getRole());
			if(list == null){
				list = new ArrayList<QuantifiedRole>();
				qRMap.put(qR.getRole(),list);
			}
			list.add(qR);
		}
		
		for(Entry<Role, List<QuantifiedRole>> e: qRMap.entrySet()){
			List<QuantifiedRole> qrL = e.getValue();
			Collections.sort(qrL, new Comparator<QuantifiedRole>() {
				@Override
				public int compare(QuantifiedRole o1, QuantifiedRole o2) {
					return o2.getQ()-o1.getQ();
				}
			});
			
			for(int i=0;i<qrL.size()-1;i++){
				this.add(new ConceptInclusionAssertion(
						qrL.get(i),
						qrL.get(i+1)));
				//System.out.println(qrL.get(i)+" "+qrL.get(i+1));
			}
		}
		
		/* G: + >qR \subseteq BOX >qR 
		 * for >qR \in T an R is rigid role
		 * 
		 * TODO: Avoid duplications
		 */

		for(QuantifiedRole qR : qRoles){
			if(qR.getRole().getRefersTo() instanceof AtomicRigidRole){
				this.add(new ConceptInclusionAssertion(
					new SometimeFuture(qR),
					new AlwaysFuture(qR)));
				    
			}
		}
		
	}

}
