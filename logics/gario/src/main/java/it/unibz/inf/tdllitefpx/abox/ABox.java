package it.unibz.inf.tdllitefpx.abox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Bot;
import it.unibz.inf.qtl1.formulae.Alphabet;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.ConjunctiveConcept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimeFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.concepts.temporal.TemporalConcept;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.TBox;


public class ABox extends ConjunctiveFormula implements FormattableObj {

	Set <ABoxConceptAssertion> ABox = new HashSet<ABoxConceptAssertion>();

	Set<ABoxConceptAssertion> ConceptsAssertion = new HashSet<ABoxConceptAssertion>();
	Set<ABoxRoleAssertion> RolesAssertion = new HashSet<ABoxRoleAssertion>();
	Set<Formula> ABoxFormula = new HashSet<Formula>();
	HashMap<String, Set<String>> QRigid = new HashMap<String, Set<String>>();
	HashMap<String, Set<String>> QRigidL = new HashMap<String, Set<String>>();
	HashMap<String, Set<String>> QLocal = new HashMap<String, Set<String>>();
	HashMap<String, Set<String>> QRigidinv = new HashMap<String, Set<String>>();

	// Global added for the abstraction

	HashMap<String, Set<String>> QNegRigid = new HashMap<String, Set<String>>();
	HashMap<String, Set<String>> QNegRigidL = new HashMap<String, Set<String>>(); //rigid timestamped
	HashMap<String, Set<String>> QNegLocal = new HashMap<String, Set<String>>();

	Set <ABoxConceptAssertion> FORigid = new HashSet<ABoxConceptAssertion>();
	Set <ABoxConceptAssertion> FOLocal = new HashSet<ABoxConceptAssertion>();
		
	Set <ABoxConceptAssertion> ShiftABox = new HashSet<ABoxConceptAssertion>();
	Set <ABoxConceptAssertion> AbstractABox = new HashSet<ABoxConceptAssertion>();
		
	Set <ABoxRoleAssertion> ABoxLocal = new HashSet<ABoxRoleAssertion>();
	Set <ABoxRoleAssertion> ABoxShiftGlobal = new HashSet<ABoxRoleAssertion>();
	boolean inconsistent;

	Set<ABoxRoleAssertion> ShiftedRolesAssertion = new HashSet<ABoxRoleAssertion>();
		
	Set<ABoxRoleAssertion> NegatedRolesAssertion = new HashSet<ABoxRoleAssertion>();
	Set<ABoxRoleAssertion> ShiftedNegatedRolesAssertion = new HashSet<ABoxRoleAssertion>();
	
	HashMap<String, Set<Concept>> To = new HashMap<String, Set<Concept>>();
	HashMap<Integer, Set<String>> ToHash = new HashMap<Integer, Set<String>>();

	Alphabet a = new Alphabet();
	Variable x = new Variable("x");

	private static final long serialVersionUID = 1L;

	/**
	 * Create the list of Concepts Assertion	
	 * @param c an ABox Concept Assertion
	 */
	public boolean addConceptsAssertion(ABoxConceptAssertion c) {

		boolean s = ConceptsAssertion.add(c);

		Set<Concept>ToList = new HashSet<Concept>();
		To.putIfAbsent(c.value, ToList);
		ToList = To.get(c.value);
		ToList.add(c.getConceptAssertion());
		To.replace(c.value, ToList);

		return s;
	}

	//	Create the list of Concept Assertions
	public boolean addABox(ABoxConceptAssertion c){
		boolean s = ABox.add(c);
		return s;
	}

	//	Create the list of Concept Assertions
	public boolean addABoxLocal(ABoxRoleAssertion r){
		boolean s = ABoxLocal.add(r);
		return s;
	}

	public boolean addABoxShiftGlobal(ABoxRoleAssertion r){
		boolean s = ABoxShiftGlobal.add(r);
		return s;
	}
	
	//	Create the list of Role Assertions
	public boolean addABoxRoleAssertions(ABoxRoleAssertion r){ 
		boolean s = RolesAssertion.add(r);
		return s;
	}

	//	Create the list of Role Assertions
	public boolean addABoxNegatedRoleAssertions(ABoxRoleAssertion r){
		boolean s = NegatedRolesAssertion.add(r);
		return s;
	}	
	
	/**
	 * Return the set of ABox concept assertions
	 *  
	 * @return Set<ABoxConceptAssertion>
	 */
	public Set<ABoxConceptAssertion> getABoxConceptAssertions(){
		return this.ConceptsAssertion;
	}
	
	/**
	 * Return the set of ABox role assertions
	 * 
	 * @return Set<ABoxRoleAssertion>
	 */
	public Set<ABoxRoleAssertion> getABoxRoleAssertions(){
		return this.RolesAssertion;
	}
	
	/**
	 * Return a list of role names included in the current ABox
	 * 
	 * @return Set<Role>
	 */
	public Set<Role> getRolesABox() {
		HashSet<Role> roles = new HashSet<Role>();
		for (ABoxRoleAssertion r : RolesAssertion) {
			roles.add(r.ro);
		}
		return roles;
	}

	/**
	 * Return a list of negated role names included in the current ABox
	 * 
	 * @return Set<Role>
	 */
	public Set<Role> getNegatedRolesAbox(){
		HashSet<Role> roles = new HashSet<Role>();
		for(ABoxRoleAssertion r: NegatedRolesAssertion){
			roles.add(r.ro);
		
		}
		return roles;
	}

	public void PrintABoxRoleAssertions(Set<ABoxRoleAssertion> s){ 
		//	Create the list of Role Assertions
		String list = "";
		Iterator iterator = s.iterator();
		while(iterator.hasNext()) {
			String c = iterator.next().toString();
			list = list + c;
		} 
		System.out.println(list);
	}


	/**
	 * Add a role assertion to a list of roles together with their successor and predecessor
	 * 
	 * @implNote check repetitions
	 * 
	 * @param r an ABox Role Assertion
	 */
	public void addABoxRoleAssertion(ABoxRoleAssertion r) {
		RolesAssertion.add(r);
		Set<String>successorR = new HashSet<String>();
		Set<String>successorL = new HashSet<String>();
		Set<String>successorLG = new HashSet<String>();
		Set<String>PredecessorR = new HashSet<String>();
		Set<String>PredecessorL = new HashSet<String>();
		Set<String>PredecessorLG = new HashSet<String>();
			
		if (r.ro.getRefersTo() instanceof AtomicRigidRole )
		{
			successorR.add(r.y);
			PredecessorR.add(r.x);
			QRigid.putIfAbsent(r.ro.toString()+"_"+r.x, successorR);
			QRigid.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y, PredecessorR);
			
			successorR = QRigid.get(r.ro.toString()+"_"+r.x);
			PredecessorR = QRigid.get(r.ro.getInverse().toString()+"_"+r.y);
			
			successorR.add(r.y);
			PredecessorR.add(r.x);
			QRigid.replace(r.ro.toString()+"_"+r.x,successorR);
			QRigid.replace(r.ro.getInverse().toString()+"_"+r.y,PredecessorR);
			
			//Rigid TimeStamps				
			successorLG.add(r.y);
			PredecessorLG.add(r.x);
			QRigidL.putIfAbsent(r.ro.toString()+"_"+r.x+"_"+r.t, successorLG);
			QRigidL.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t, PredecessorLG);
			
			successorLG = QRigidL.get(r.ro.toString()+"_"+r.x+"_"+r.t);
			PredecessorLG = QRigidL.get(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t);
			
			successorLG.add(r.y);
			PredecessorLG.add(r.x);
			QRigidL.replace(r.ro.toString()+"_"+r.x+"_"+r.t,successorLG); // XE1G1(a2) <=> G1_a2_1 (1: timeStamp)
			QRigidL.replace(r.ro.getInverse().toString()+"_"+r.x+"_"+r.t,PredecessorLG);			
		}	
		
		else {
			successorL.add(r.y);
			PredecessorL.add(r.x);
			QLocal.putIfAbsent(r.ro.toString()+"_"+r.x+"_"+r.t, successorL);
			QLocal.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t, PredecessorL);
			
			successorL=QLocal.get(r.ro.toString()+"_"+r.x+"_"+r.t);
			PredecessorL=QLocal.get(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t);
			
			successorL.add(r.y);
			PredecessorL.add(r.x);
			QLocal.replace(r.ro.toString()+"_"+r.x+"_"+r.t,successorL); // XE1G1(a2) <=> G1_a2_1 (1: timeStamp)
			QLocal.replace(r.ro.getInverse().toString()+"_"+r.x+"_"+r.t,PredecessorL);
		}
		//System.out.println("QRigid:"+QRigid.toString());
		//System.out.println("QRigidL:"+QRigidL.toString());
		//System.out.println("QLocal:"+QLocal.toString());

	}

	/**
	 * Return a set of constants in the ABox (for concepts and roles)
	 * 
	 * @return Set<Constant>
	 */
	public Set<Constant> getConstantsABox() {
		Set<Constant> consts = new HashSet<Constant>();

		for (ABoxConceptAssertion c : ConceptsAssertion) {
			consts.add(c.getConstant());
		}

		for (ABoxRoleAssertion r : RolesAssertion) {
			consts.add(r.getx());
			consts.add(r.gety());
		}
		return consts;
	}
	
	
	/**
	 * Return a mapping String to Integer for Rigid Roles
	 * 
	 * @param qR a set of QuantifiedRole >= / <= q
	 * @return Map<String, Integer>
	 */
	public Map<String, Integer> getQRigidABox(Set<QuantifiedRole> qR) {
		HashMap<String, Integer> qRAQ = new HashMap<String, Integer>();
		
		for (QuantifiedRole qr : qR) {
			qRAQ.putIfAbsent(qr.getRole().toString(), qr.getQ());
			if (qRAQ.get(qr.getRole().toString()) < qr.getQ()) {
				qRAQ.replace(qr.getRole().toString(), qr.getQ());
			}
		}
		return qRAQ;
	}


	public Map<String, Integer> getQuantifiedRolesABox(int Q){
		HashMap<String, Integer> qRQ = new HashMap<String, Integer>();
		Set<Role> Roles = getRolesABox();
		for (Role r: Roles) {
			int q = new Random().nextInt(Q)+1;
			qRQ.putIfAbsent(r.toString(), q);
				
		}
		Set<Role> NRoles=getNegatedRolesAbox();
		for (Role nr: NRoles) {
			int q = new Random().nextInt(Q)+1;
			qRQ.putIfAbsent(nr.toString(), q);
				
		}
		return qRQ;
	}	
	

	/**
	 * Extend the ABox constraints from given TBox
	 * 
	 * @param tbox a TBox
	 */
	public void addExtensionConstraintsABox(TBox tbox) {

		Set<QuantifiedRole> qRoles = tbox.getQuantifiedRoles();
		Map<String, Integer> qRolesQ = tbox.getQuantifiedRolesQ(qRoles);
		// Set <Role> Roles=getRolesAbox();
		/*
		 * >= 2.Name(John) >= 1.Name(John) /*>= 2.NameInv(Kennedy) >= 1.NameInv(Kennedy)
		 */
		Set<Role> Roles = getRolesABox();
		for (Role r : Roles) {
			if (r.getRefersTo() instanceof AtomicRigidRole) {

				for (String keyL : QRigidL.keySet()) {
					String[] keyLi = keyL.split("_");
					String index = keyLi[0].concat("_" + keyLi[1]);

					int Qtabox = QRigid.get(index).size();
					if (r.toString().equals(keyLi[0])) {
						int Qtbox = qRolesQ.get(keyLi[0]);
						int j = Math.min(Qtbox, Qtabox);

			//			For ABox just take max cardinality of each role
						QuantifiedRole qL = new QuantifiedRole(r, j);
						Concept cr = (Concept) qL;

						int t = Integer.parseInt(keyLi[2]);
						while (t != 0) { // States
							Concept tqL1 = new NextFuture(cr);
							cr = tqL1;
							t--;
						}
						addConceptsAssertion(new ABoxConceptAssertion(cr, keyLi[1]));

					} else if (r.getInverse().toString().equals(keyLi[0])) {
						int Qtbox = qRolesQ.get(r.toString());
						int j = Math.min(Qtbox, Qtabox);
						
						QuantifiedRole qLinv = new QuantifiedRole(r.getInverse(), j);
						Concept cinvr = (Concept) qLinv;

						int t = Integer.parseInt(keyLi[2]);
						while (t != 0){ // States
							Concept tqL2 = new NextFuture(cinvr);
							cinvr = tqL2;
							t--;
						}
						addConceptsAssertion(new ABoxConceptAssertion(cinvr, keyLi[1]));
					}
				}
			} // Local Roles
			else {
				for (String keyL : QLocal.keySet()) {
					String[] keyLi = keyL.split("_");
					int Qtaboxi = QLocal.get(keyL).size();

					if (r.toString().equals(keyLi[0])) {
						int Qtbox = qRolesQ.get(keyLi[0]);
						int j = Math.min(Qtbox, Qtaboxi);
							
						QuantifiedRole qL = new QuantifiedRole(r, j);
						Concept cr = (Concept) qL;
						int t = Integer.parseInt(keyLi[2]);
							
						while (t != 0) { // States
							Concept tqL1 = new NextFuture(cr);
							cr = tqL1;
							t--;
						}
						addConceptsAssertion(new ABoxConceptAssertion(cr, keyLi[1]));

					} else if (r.getInverse().toString().equals(keyLi[0])) {
						qRolesQ.putIfAbsent(r.getInverse().toString(), 1);
						int Qtbox = qRolesQ.get(r.toString());
						int j = Math.min(Qtbox, Qtaboxi);

						QuantifiedRole qLinv = new QuantifiedRole(r.getInverse(), j);
						Concept cinvr = (Concept) qLinv;
						int t = Integer.parseInt(keyLi[2]);
							
						while (t != 0) { // States
							Concept tqL2 = new NextFuture(cinvr);
							cinvr = tqL2;
							t--;
						}
						addConceptsAssertion(new ABoxConceptAssertion(cinvr, keyLi[1]));

					}
				}
			}
		}
	}
	


	/**
	 * Get the size of the original ABox (# of assertions)
	 * @return the size of the original ABox
	 */
	public Integer getABoxSize(){
		return ABox.size();
	}
	/**
	 * Generate QTL formula
	 * 
	 * @return Formula
	 */
	public Formula getABoxFormula(boolean r){
		ConjunctiveFormula qtl = new ConjunctiveFormula();
		System.out.println("Concepts="+ConceptsAssertion.size());
		System.out.println("FO Local="+FOLocal.size());
		System.out.println("FO Global="+FORigid.size());
	//	ABox.addAll(ConceptsAssertion);
		int i = 0;
		if (inconsistent == false){
			for(ABoxConceptAssertion c: ABox){ //previous ConceptsAssertion
				Formula cf = conceptToFormula(c.c, r);
				cf.substitute(x, new Constant(c.value));
				qtl.addConjunct(cf);
				i++;
			}
			System.out.println("Size FO ABox: "+i);
		}
		return qtl;
	}

	/**
	 * 
	 * @param c
	 * @param futur
	 * @return
	 */
	public Formula conceptToFormula(Concept c, boolean futur) {
			if (c instanceof AtomicConcept) {
				if (futur) {
					return new Atom(c.toString() + "F", x);
				} else {
					return new Atom(c.toString(), x);
				}
			}
			else if (c instanceof QuantifiedRole) {
				QuantifiedRole qE = (QuantifiedRole)c;
			    Atom atom;
			    
				if (futur){
					atom = a.get("E"+qE.getQ()+qE.getRole().toString()+"F",1);
				} else {
				    atom = a.get("E"+qE.getQ()+qE.getRole().toString(),1); //Modif
				}
				
				atom.setArg(0, x);
				return atom ;
			}
			else if( c instanceof QuantifiedRole) {
				QuantifiedRole qE = (QuantifiedRole)c;
				Atom atom;
				if (futur){
					atom = a.get("E"+qE.getQ()+qE.getRole().getInverse().toString()+"F",1); //Modif
				} else {
					atom = a.get("E"+qE.getQ()+qE.getRole().getInverse().toString(),1);	
				}
				
				atom.setArg(0, x);
				return atom;
			}
			else if(c instanceof BottomConcept)
				return Bot.getStatic();
			else if(c instanceof NegatedConcept){
				NegatedConcept nc = (NegatedConcept) c;
				return new NegatedFormula(conceptToFormula(nc.getRefersTo(), futur));
			}
			else if(c instanceof ConjunctiveConcept){
				ConjunctiveConcept cc = (ConjunctiveConcept)c;
				ConjunctiveFormula cf = new ConjunctiveFormula();
				for(Concept d : cc.getConjuncts()){
					cf.addConjunct(conceptToFormula(d, futur));
				}
				return cf;
			}
			else if(c instanceof TemporalConcept){
				TemporalConcept d = (TemporalConcept) c;
				if(c instanceof NextFuture){
					return new it.unibz.inf.qtl1.formulae.temporal.NextFuture(
							conceptToFormula(d.getRefersTo(), futur));
				}else if(c instanceof NextPast){
					return new it.unibz.inf.qtl1.formulae.temporal.NextPast(
							conceptToFormula(d.getRefersTo(), futur));
				}else if(c instanceof AlwaysPast){
					return new it.unibz.inf.qtl1.formulae.temporal.AlwaysPast(
							conceptToFormula(d.getRefersTo(), futur));
				}else if(c instanceof AlwaysFuture){
					return new it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture(
							conceptToFormula(d.getRefersTo(), futur));
				}else if(c instanceof SometimePast){
					return new it.unibz.inf.qtl1.formulae.temporal.SometimePast(
							conceptToFormula(d.getRefersTo(), futur));
				}else if(c instanceof SometimeFuture){
					return new it.unibz.inf.qtl1.formulae.temporal.SometimeFuture(
							conceptToFormula(d.getRefersTo(), futur));
				}
				
			}
			
			System.err.println("Unexpected case "+c.getClass().toString());
			return null;
	}


	/**
	 * Abstract code
	 */



	/**
	 * Shift Rigid Roles
	 * i.e. all of the rigid roles are moved to time 0.
	 * This is the first abstracting step.
	 * 
	 * @implNote Name(A, Marc, 1), Name(A, Marc, 2), ... become Name(A, Marc, 0) 
	 * 
	 */
	public void shiftRigidRolesAssertions(){
		Iterator iterator = RolesAssertion.iterator();
		while(iterator.hasNext()) {
			ABoxRoleAssertion ra = (ABoxRoleAssertion) iterator.next();
			ABoxRoleAssertion shiftedR = new ABoxRoleAssertion(ra.getRole(),
															   ra.getx().toString(), 
															   ra.gety().toString(), 
															   0);
			ShiftedRolesAssertion.add(shiftedR);
		} 
	}

	/**
	 * Useless method?
	 * @param c
	 */
	public void ShiftABox(ABoxConceptAssertion c){
		ShiftABox.add(c);
	}


	public void AbstractABox() {
		if (inconsistent == false) {
			for(String indexTo : To.keySet()){
				Set<String> Hashvalue = new HashSet<String>();
				Integer newindex = To.get(indexTo).hashCode();
				ToHash.putIfAbsent(newindex, Hashvalue);
				Hashvalue = ToHash.get(newindex);
				Hashvalue.add(indexTo);
				ToHash.replace(newindex, Hashvalue);
			}

			System.out.println("Indv:"+To.size());
			System.out.println("New Indv:"+ToHash.size());
	   
			int i = 1;
			if (To.size() != ToHash.size()){
				for(Integer indexToHash : ToHash.keySet()){
   					Set <String> values = ToHash.get(indexToHash);
					Set<Concept> ConceptsAbstract = new HashSet <Concept>();
					String concatinstance = String.join("_", values);
		
					for(String instance : values){
						ConceptsAbstract = To.get(instance);
					}

					//System.out.println("type" + i + ": " + concatinstance);
					
					for(Concept c : ConceptsAbstract){
						AbstractABox.add(new ABoxConceptAssertion(c, concatinstance));
					}
					i++;
				}
	   		}
	   		else {
		   		AbstractABox = ABox;
	   		}
	   		System.out.println("Size FO ABstract ABox: "+AbstractABox.size());
		}
	}


	/**
	 * Role assertion in Abstracted ABox
	 * 
	 * @implNote should we check if role is rigid or local here?
	 * 
	 * @param ABoxRoleAssertion r
	 */
	public void addAbsABoxRoleAssertion(ABoxRoleAssertion r){
		ShiftedRolesAssertion.add(r);
		Set<String>successorR=new HashSet<String>();
		Set<String>successorL=new HashSet<String>();
	//	Set<String>successorLG=new HashSet<String>();
		Set<String>PredecessorR=new HashSet<String>();
		Set<String>PredecessorL=new HashSet<String>();
	//	Set<String>PredecessorLG=new HashSet<String>();
		
			
		if (r.ro.getRefersTo() instanceof AtomicRigidRole){
					
			QRigid.putIfAbsent(r.ro.toString()+"_"+r.x, successorR);
			QRigid.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y, PredecessorR);
			
			successorR = QRigid.get(r.ro.toString()+"_"+r.x);
			PredecessorR = QRigid.get(r.ro.getInverse().toString()+"_"+r.y);
			
			successorR.add(r.y);
			PredecessorR.add(r.x);
			QRigid.replace(r.ro.toString()+"_"+r.x,successorR);
			QRigid.replace(r.ro.getInverse().toString()+"_"+r.y,PredecessorR);
			
			//Rigid TimeStamps				
	/*		successorLG.add(r.y);
			PredecessorLG.add(r.x);
			QRigidL.putIfAbsent(r.ro.toString()+"_"+r.x+"_"+r.t, successorLG);
			QRigidL.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t, PredecessorLG);
			
			successorLG=QRigidL.get(r.ro.toString()+"_"+r.x+"_"+r.t);
			PredecessorLG=QRigidL.get(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t);
			
			successorLG.add(r.y);
			PredecessorLG.add(r.x);
			QRigidL.replace(r.ro.toString()+"_"+r.x+"_"+r.t,successorLG); // XE1G1(a2) <=> G1_a2_1 (1: timeStamp)
			QRigidL.replace(r.ro.getInverse().toString()+"_"+r.x+"_"+r.t,PredecessorLG);			
		*/
		}
		else {
				QLocal.putIfAbsent(r.ro.toString()+"_"+r.x+"_"+r.t, successorL);
				QLocal.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t, PredecessorL);
				
				successorL = QLocal.get(r.ro.toString()+"_"+r.x+"_"+r.t);
				PredecessorL = QLocal.get(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t);
				
				successorL.add(r.y);
				PredecessorL.add(r.x);
				QLocal.replace(r.ro.toString()+"_"+r.x+"_"+r.t,successorL); // XE1G1(a2) <=> G1_a2_1 (1: timeStamp)
				QLocal.replace(r.ro.getInverse().toString()+"_"+r.x+"_"+r.t,PredecessorL);
			}
	//	System.out.println("QRigid:"+QRigid.toString());
	//	System.out.println("QRigidL:"+QRigidL.toString());
	//	System.out.println("QLocal:"+QLocal.toString());
	}



	/**
	 * Negated Role assertion in Abstracted ABox
	 * 
	 * @param ABoxRoleAssertion r
	 */		
	public void addAbsABoxNegatedRoleAssertion(ABoxRoleAssertion r){
		//	Create the list of Role Assertions
		ShiftedNegatedRolesAssertion.add(r);
		Set<String>successorR=new HashSet<String>();
		Set<String>successorL=new HashSet<String>();
		//	Set<String>successorLG=new HashSet<String>();
		Set<String>PredecessorR=new HashSet<String>();
		Set<String>PredecessorL=new HashSet<String>();
		//	Set<String>PredecessorLG=new HashSet<String>();
						
		if (r.ro.getRefersTo() instanceof AtomicRigidRole){
			QNegRigid.putIfAbsent(r.ro.toString()+"_"+r.x, successorR);
			QNegRigid.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y, PredecessorR);
				
			successorR = QNegRigid.get(r.ro.toString()+"_"+r.x);
			PredecessorR = QNegRigid.get(r.ro.getInverse().toString()+"_"+r.y);
				
			successorR.add(r.y);
			PredecessorR.add(r.x);
			QNegRigid.replace(r.ro.toString()+"_"+r.x,successorR);
			QNegRigid.replace(r.ro.getInverse().toString()+"_"+r.y,PredecessorR);
				
			//Rigid TimeStamps				
	/*			successorLG.add(r.y);
				PredecessorLG.add(r.x);
				QNegRigidL.putIfAbsent(r.ro.toString()+"_"+r.x+"_"+r.t, successorLG);
				QNegRigidL.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t, PredecessorLG);
				
				successorLG=QNegRigidL.get(r.ro.toString()+"_"+r.x+"_"+r.t);
				PredecessorLG=QNegRigidL.get(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t);
				
				successorLG.add(r.y);
				PredecessorLG.add(r.x);
				QNegRigidL.replace(r.ro.toString()+"_"+r.x+"_"+r.t,successorLG); // XE1G1(a2) <=> G1_a2_1 (1: timeStamp)
				QNegRigidL.replace(r.ro.getInverse().toString()+"_"+r.x+"_"+r.t,PredecessorLG);			
		*/	
		} else {
			
			QNegLocal.putIfAbsent(r.ro.toString()+"_"+r.x+"_"+r.t, successorL);
			QNegLocal.putIfAbsent(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t, PredecessorL);
			
			successorL = QNegLocal.get(r.ro.toString()+"_"+r.x+"_"+r.t);
			PredecessorL = QNegLocal.get(r.ro.getInverse().toString()+"_"+r.y+"_"+r.t);
			
			successorL.add(r.y);
			PredecessorL.add(r.x);
			QNegLocal.replace(r.ro.toString()+"_"+r.x+"_"+r.t,successorL); // XE1G1(a2) <=> G1_a2_1 (1: timeStamp)
			QNegLocal.replace(r.ro.getInverse().toString()+"_"+r.x+"_"+r.t,PredecessorL);
		}
	//	System.out.println("QNegRigid:"+QRigid.toString());
	//	System.out.println("QNegRigidL:"+QRigidL.toString());
	//	System.out.println("QNegLocal:"+QLocal.toString());
	}

	/**
	 * Returns the set of constants in the abstracted ABox
	 * @return Set<Constant>
	 */
	public Set<Constant> getConstantsABoxAbs(){
		Set<Constant> consts = new HashSet<Constant>();	
	
		for(ABoxConceptAssertion c: AbstractABox){
			consts.add(c.getConstant());
		}	
		return consts; 
	}


	/**
	 * Stats for Abstracted ABox
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, Integer> getStatsAbsABox(){
		HashMap<String, Integer> stats = new HashMap<String, Integer>();
		
		stats.put("Concept_Assertions:", ConceptsAssertion.size());
		stats.put("Role_Assertions:", RolesAssertion.size()+NegatedRolesAssertion.size());
		stats.put("ABox Instances:", ABox.size());
		stats.put("Abstract ABox Instances:", AbstractABox.size());

		return stats;
	}

		/**
	 * Stats for ABox
	 * 
	 * @return Map<String, Integer>
	 */
	public Map<String, Integer> getStatsABox() {
		HashMap<String, Integer> stats = new HashMap<String, Integer>();

		stats.put("Concept_Assertions:", ConceptsAssertion.size());
		stats.put("Role_Assertions:", RolesAssertion.size() + NegatedRolesAssertion.size());
		return stats;
	}

	/**
	 * Extend the Abstracted ABox constraints given a TBox
	 * 
	 * @param tbox a TBox
	 */
	public void addExtensionConstraintsAbsABox(TBox tbox){
			
		Set<QuantifiedRole> qRoles = tbox.getQuantifiedRoles();
		Map<String, Integer> qRolesQ = tbox.getQuantifiedRolesQ(qRoles);
		
			//Set <Role> Roles=getRolesAbox();
			/* >= 2.Name(John)
			 * >= 1.Name(John)
			 /*>= 2.NameInv(Kennedy)
			 * >= 1.NameInv(Kennedy)
			 */
			
		System.out.println("");
		System.out.println("------ Shifted TDLITE ABOX");
//			System.out.println("**RolesAssertion:"+RolesAssertion.size());
//			PrintAboxRoleAssertions(RolesAssertion);
//			PrintAboxRoleAssertions(ShiftedRolesAssertion);
//			System.out.println("**NegatedRolesAssertion:");
		int GainP = RolesAssertion.size() - (ShiftedRolesAssertion.size());
//			int GainN= NegatedRolesAssertion.size()-ShiftedNegatedRolesAssertion.size();
		System.out.println("Gain_Rigid= "+GainP); //+"+GainN+"="+(GainP+GainN));
//			System.out.println("ShiftedRolesAssertion:"+ShiftedRolesAssertion.size());
//			PrintAboxRoleAssertions(ShiftedRolesAssertion);
//			System.out.println("**NegatedRolesAssertion:");
//			PrintAboxRoleAssertions(NegatedRolesAssertion);
//			System.out.println("**ShiftedNegatedRolesAssertion:");
//			PrintAboxRoleAssertions(ShiftedNegatedRolesAssertion);
//			System.out.println("");
//			System.out.println("*Local Assertions:"+ABoxLocal.size());
//			System.out.println("*Global Assertions:"+ABoxShiftGlobal.size());
			
			
			
//			boolean inconsistentL =RolesAssertion.removeAll(NegatedRolesAssertion);
		Set<ABoxRoleAssertion> Inconsist = new HashSet<ABoxRoleAssertion>();
		Inconsist = ShiftedRolesAssertion;
//			System.out.println("list inconsistent");
		Inconsist.retainAll(ShiftedNegatedRolesAssertion);
//			PrintAboxRoleAssertions(Inconsist);
			
//			inconsistent =ShiftedRolesAssertion.removeAll(ShiftedNegatedRolesAssertion);
//			System.out.println("Inconsistent: "+inconsistent);
		Set<Role> Roles = getRolesABox();
		for (Role r: Roles){
			if (r.getRefersTo() instanceof AtomicRigidRole){
				for(String keyL : QRigid.keySet()){
				    String[]keyLi = keyL.split("_"); //keyL:G1_a2_1
				    String index = keyLi[0].concat("_"+keyLi[1]); //index:G1_a2
				    	
					int Qtabox = QRigid.get(index).size();// exp: get(G1_a8)=size([b2, b1])=2				
							
					if (r.toString().equals(keyLi[0])){
						int Qtbox = qRolesQ.get(keyLi[0]); //get the max cardinality of the Role from the TBox
						int j = Math.min(Qtbox, Qtabox);
						Set<Concept>ToList = new HashSet<Concept>();
								
						//Cardinality						
						QuantifiedRole qL = new QuantifiedRole(r, j);	
		    			Concept cr = (Concept)qL;
		    			//	ShiftABox(new AboxConceptAssertion (cr,keyLi[1])); //abstract assertion rigid at 0
		    			addABox(new ABoxConceptAssertion (cr,keyLi[1]));
		    			FORigid.add(new ABoxConceptAssertion (cr,keyLi[1]));
		    					
		    			//	ToList.add(cr);
		    			To.putIfAbsent(keyLi[1], ToList);
		    			ToList=To.get(keyLi[1]);
		    			ToList.add(cr);
		    			To.replace(keyLi[1], ToList);
		    					
		    					
		    			/*		int t= Integer.parseInt(keyLi[2]); //get the timeStamp of the assertion
		    					while (t!=0 )//States      //exp: XG1(a2)= add(XG1, a2)
		    					{ 
		    						Concept tqL1= new NextFuture(cr);  THIS VERSION IS WHEN WE HANDLE RIGID ON FO LEVEL
		    						cr= tqL1;
					    			t--;	
		    					}
								
		    					addABox(new AboxConceptAssertion (cr,keyLi[1])); 
		    					System.out.println("To:"+To.toString());*/
				    } else if (r.getInverse().toString().equals(keyLi[0])) {
							qRolesQ.putIfAbsent(r.getInverse().toString(), 1);
							int Qtbox = qRolesQ.get(r.getInverse().toString());
							int j = Math.min(Qtbox, Qtabox);
							Set<Concept>ToList = new HashSet<Concept>();
								
								//Cardinality
		    				QuantifiedRole qLinv = new QuantifiedRole(r.getInverse(), j);
		    				Concept cinvr = (Concept)qLinv;
		    			//		ShiftABox(new AboxConceptAssertion (cinvr,keyLi[1])); //abstract assertion rigid at 0
		    				addABox(new ABoxConceptAssertion (cinvr,keyLi[1]));
		    				FORigid.add(new ABoxConceptAssertion (cinvr,keyLi[1]));
		    					
		    					//ToList.add(cinvr);
		    				To.putIfAbsent(keyLi[1], ToList);
		    				ToList = To.get(keyLi[1]);
		    				ToList.add(cinvr);
		    				To.replace(keyLi[1], ToList);
		    					
		    				/*	int t= Integer.parseInt(keyLi[2]); THIS VERSION IS WHEN WE HANDLE RIGID ON FO LEVEL
		    					while (t!=0 )//States
		    					{ 
		    						Concept tqL2= new NextFuture(cinvr);
		    						cinvr= tqL2;
					    			t--;	
		    					}
		    					addABox(new AboxConceptAssertion (cinvr,keyLi[1]));	    */					    					
				    }
				}
						
			}
						
					//Local Roles
			for(String keyL : QLocal.keySet()){
			   	String[]keyLi = keyL.split("_");
				int Qtaboxi = QLocal.get(keyL).size();
									
				if (r.toString().equals(keyLi[0])){
					int Qtbox = qRolesQ.get(keyLi[0]);
					int j = Math.min(Qtbox, Qtaboxi);
					Set<Concept>ToList = new HashSet<Concept>();
						
                        //Cardinality
					QuantifiedRole qL = new QuantifiedRole(r, j);	
	    			Concept cr = (Concept)qL;
	    					
	    			int t = Integer.parseInt(keyLi[2]);
	    			while (t != 0 ){ 
	    				Concept tqL1 = new NextFuture(cr);
	    				cr = tqL1;
	    				t--;	
	    			}
	    				
	    			addABox(new ABoxConceptAssertion (cr,keyLi[1]));
	    			//	ShiftABox(new AboxConceptAssertion (cr,keyLi[1]));//the abstract is the same element
	    			FOLocal.add(new ABoxConceptAssertion (cr,keyLi[1]));
	    					
	    			ToList.add(cr);
	    			To.putIfAbsent(keyLi[1], ToList);
	    			ToList = To.get(keyLi[1]);
	    			ToList.add(cr);
	    			To.replace(keyLi[1], ToList);
			   	} else if (r.getInverse().toString().equals(keyLi[0])){
						qRolesQ.putIfAbsent(r.getInverse().toString(), 1);
						int Qtbox = qRolesQ.get(r.toString());
						int j = Math.min(Qtbox, Qtaboxi);
						Set<Concept>ToList = new HashSet<Concept>();
							
					 //Cardinality
	    				QuantifiedRole qLinv = new QuantifiedRole(r.getInverse(), j);
	    				Concept cinvr = (Concept)qLinv;
	    					
	    				int t = Integer.parseInt(keyLi[2]);
	    				while (t != 0){ 
	    					Concept tqL2 = new NextFuture(cinvr);
	    					cinvr = tqL2;
			    			t--;	
	    				}
	    					
	    				addABox(new ABoxConceptAssertion (cinvr,keyLi[1]));
	    				//	ShiftABox(new AboxConceptAssertion (cinvr,keyLi[1])); //the abstract is the same element
	    				FOLocal.add(new ABoxConceptAssertion (cinvr,keyLi[1]));
	    					
	    				ToList.add(cinvr);
	    				To.putIfAbsent(keyLi[1], ToList);
	    				ToList = To.get(keyLi[1]);
	    				ToList.add(cinvr);
	    				To.replace(keyLi[1], ToList);
					}
			}
		}
			//  System.out.println("To:"+To.toString());
		ABox.addAll(ConceptsAssertion);
	}

	/**
	 * Extend the Abstracted ABox constraints given a Q
 	 * 
	 * @param int Q
	 */
	public void addExtensionConstraintsAbsABox(int Q){
		System.out.println("");
		System.out.println("------ Shifted TDLITE ABOX");
		System.out.println("**RolesAssertion:"+RolesAssertion.size());

		//PrintABoxRoleAssertions(RolesAssertion);
		//PrintABoxRoleAssertions(ShiftedRolesAssertion);


	//	System.out.println("**NegatedRolesAssertion:");
		int GainP = RolesAssertion.size() - (ShiftedRolesAssertion.size());
	//	int GainN= NegatedRolesAssertion.size()-ShiftedNegatedRolesAssertion.size();
		System.out.println("Gain_Rigid= "+GainP); //+"+"+GainN+"="+(GainP+GainN));
	//	System.out.println("ShiftedRolesAssertion:"+ShiftedRolesAssertion.size());
	//	PrintAboxRoleAssertions(ShiftedRolesAssertion);
	//	System.out.println("**NegatedRolesAssertion:");
	//	PrintAboxRoleAssertions(NegatedRolesAssertion);
	//	System.out.println("**ShiftedNegatedRolesAssertion:");
	//	PrintAboxRoleAssertions(ShiftedNegatedRolesAssertion);
		System.out.println("");
	//	System.out.println("*Local Assertions:"+ABoxLocal.size());
	//	System.out.println("*Global Assertions:"+ABoxShiftGlobal.size());
		
		
		
	//	boolean inconsistentL =RolesAssertion.removeAll(NegatedRolesAssertion);
		Set<ABoxRoleAssertion> Inconsist = new HashSet<ABoxRoleAssertion>();
		Inconsist=ShiftedRolesAssertion;
	//	System.out.println("list inconsistent");
		Inconsist.retainAll(ShiftedNegatedRolesAssertion);
	//	PrintAboxRoleAssertions(Inconsist);
		
	//	inconsistent =ShiftedRolesAssertion.removeAll(ShiftedNegatedRolesAssertion);
	//	System.out.println("Inconsistent: "+inconsistent);
	
		boolean ass;	
		if (inconsistent == false){
			Map<String, Integer> qRolesQ = getQuantifiedRolesABox(Q); 
			
				//Set <Role> Roles=getRolesAbox();
				/* >= 2.Name(John)
				 * >= 1.Name(John)
				 /*>= 2.NameInv(Kennedy)
				 * >= 1.NameInv(Kennedy)
				 */

			Set<Role> Roles = getRolesABox();
			for (Role r: Roles){
				if (r.getRefersTo() instanceof AtomicRigidRole){
					for(String keyL : QRigid.keySet()){

						String[]keyLi = keyL.split("_"); //keyL:G1_a2_1
						String index = keyLi[0].concat("_"+keyLi[1]); //index:G1_a2			
						int Qtabox = QRigid.get(index).size();// exp: get(G1_a8)=size([b2, b1])=2	
						int q = Math.min(Qtabox,Q);
						
						if (r.toString().equals(keyLi[0])){
									  //qRolesQ.get(keyLi[0]); //get the random  cardinality for the Role in the ABox		
							Set<Concept>ToList = new HashSet<Concept>();
							QuantifiedRole qL = new QuantifiedRole(r, q);	//Cardinality
							Concept cr = (Concept)qL;
							ass = addABox(new ABoxConceptAssertion(cr, keyLi[1])); 
							FORigid.add(new ABoxConceptAssertion(cr, keyLi[1]));
							
							if (ass = false){
								System.out.println("duplicate: "+ cr.toString()+"("+keyLi[1]);
							}
	
							To.putIfAbsent(keyLi[1], ToList);
							ToList=To.get(keyLi[1]);
							ToList.add(cr);
							To.replace(keyLi[1], ToList);
						}
						else if (r.getInverse().toString().equals(keyLi[0])){
								//	qRolesQ.putIfAbsent(r.getInverse().toString(), 1);
								//	int q=1;//qRolesQ.get(r.getInverse().toString());
						
								Set<Concept>ToList = new HashSet<Concept>();	
									//Cardinality
								QuantifiedRole qLinv = new QuantifiedRole(r.getInverse(), q);
								Concept cinvr = (Concept)qLinv;
								ass = addABox(new ABoxConceptAssertion (cinvr,keyLi[1]));
								FORigid.add(new ABoxConceptAssertion (cinvr,keyLi[1]));
								
								if (ass=false){
									System.out.println("duplicate: "+ cinvr.toString()+"("+keyLi[1]);
								}
							
								To.putIfAbsent(keyLi[1], ToList);
								ToList=To.get(keyLi[1]);
								ToList.add(cinvr);
								To.replace(keyLi[1], ToList);  					    					
						}
					}
							
				}			
				//Local Roles	
				for(String keyL : QLocal.keySet()){
					//	System.out.println("local role:"+r.toString());	
					String[]keyLi = keyL.split("_");
					int Qtaboxi = QLocal.get(keyL).size();
					int q = Math.min(Qtaboxi, Q);
							
					if (r.toString().equals(keyLi[0])){
						Set<Concept>ToList = new HashSet<Concept>();
								
						//Cardinality
						QuantifiedRole qL = new QuantifiedRole(r, q);	
						Concept cr = (Concept)qL;
								
						int t = Integer.parseInt(keyLi[2]);
						
						while (t != 0){ 
							Concept tqL1 = new NextFuture(cr);
							cr = tqL1;
							t--;	
						}
								
						ass = addABox(new ABoxConceptAssertion (cr,keyLi[1]));
						FOLocal.add(new ABoxConceptAssertion (cr,keyLi[1]));
						
						if (ass == false){
							System.out.println("duplicate: "+ cr.toString()+"("+keyLi[1]);
						}
						//		ShiftABox(new AboxConceptAssertion (cr,keyLi[1]));//the abstract is the same element
								
						ToList.add(cr);
						To.putIfAbsent(keyLi[1], ToList);
						ToList=To.get(keyLi[1]);
						ToList.add(cr);
						To.replace(keyLi[1], ToList);			
					} else if (r.getInverse().toString().equals(keyLi[0])){
						//		qRolesQ.putIfAbsent(r.getInverse().toString(), 1);
						//		int q=1;qRolesQ.get(r.toString());	
								Set<Concept>ToList=new HashSet<Concept>();
								
							 	//Cardinality
								QuantifiedRole qLinv=new QuantifiedRole(r.getInverse(), q);
								Concept cinvr = (Concept)qLinv;
								
								int t= Integer.parseInt(keyLi[2]);
								while (t != 0 ){ 
									Concept tqL2 = new NextFuture(cinvr);
									cinvr = tqL2;
									t--;	
								}
								
								ass = addABox(new ABoxConceptAssertion (cinvr,keyLi[1]));
								FOLocal.add(new ABoxConceptAssertion (cinvr,keyLi[1]));
								
								if (ass == false){
									System.out.println("duplicate: "+ cinvr.toString()+"("+keyLi[1]);
								}
							//	ShiftABox(new AboxConceptAssertion (cinvr,keyLi[1])); //the abstract is the same element
							
								ToList.add(cinvr);
								To.putIfAbsent(keyLi[1], ToList);
								ToList = To.get(keyLi[1]);
								ToList.add(cinvr);
								To.replace(keyLi[1], ToList);
					}  
				}
			}
			ABox.addAll(ConceptsAssertion);
		} else {
				ABox = null;
				System.out.println("Inconsistency on role assertions");
		}
	}

	/**
	 * Generate Abstracted QTL formula
	 * 
	 * @return Formula
	 */
	public Formula getAbstractABoxFormula(boolean r){
		ConjunctiveFormula qtl = new ConjunctiveFormula();
		
		for(ABoxConceptAssertion c: AbstractABox){
			Formula cf = conceptToFormula(c.c, r);
			cf.substitute(x, new Constant(c.value));
			qtl.addConjunct(cf);			
		}

		return qtl;
	}

	/**
	 * ABox as String to print
	 * 
	 * @param fmt a Format
	 * @return String
	 * @throws SymbolUndefinedException
	 */
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		// TODO Auto-generated method stub
		return this.toString();
	}

	/**
	 * 
	 * @param qR
	 * @param rigidAs
	 * @return
	 */
	public int getQAssertion(QuantifiedRole qR, Set<ABoxRoleAssertion> rigidAs) {
		int qrigid = 0;

		if (qR.getRole().getRefersTo() instanceof AtomicRigidRole) {
			for (ABoxRoleAssertion ri : rigidAs) {
				if (ri.ro.equals(qR.getRole()))
					qrigid++;
			}
		}
		if (qR.getRole().getRefersTo() instanceof AtomicLocalRole) {
			for (ABoxRoleAssertion ri : rigidAs) {
				if (ri.ro.equals(qR.getRole()))
					qrigid++;
			}
		}
		return 0;
	}
		
	@Override
	public List<Formula> getSubFormulae() {
		// TODO Auto-generated method stub
		return super.getSubFormulae();
	}

	@Override
	public int getArity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Formula toNNF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Formula negateToNNF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replaceSubFormula(Formula former, Formula current) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Variable> removeUniversals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void atomsToPropositions() throws NotGroundException {
		// TODO Auto-generated method stub

	}

	public int size() {
		// TODO Auto-generated method stub
		return super.getSubFormulae().size();
	}

}
