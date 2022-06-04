package it.unibz.inf.tdllitefpx.abox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	//HashMap<String, Set<String>> QRigidinv = new HashMap<String, Set<String>>();

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
	
	TypeKeeper typeKeeper = new TypeKeeper();
	RoleMap roleMap = new RoleMap();


	Alphabet a = new Alphabet();
	Variable x = new Variable("x");

	private static final long serialVersionUID = 1L;

	/**
	 * Create the list of Concepts Assertion	
	 * @param c an ABox Concept Assertion
	 */
	public boolean addConceptsAssertion(ABoxConceptAssertion c) {

		boolean s = ConceptsAssertion.add(c);
		typeKeeper.addAssertion(c);

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
			roles.add(r.role);
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
			roles.add(r.role);
		
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
	 * @param ra an ABox Role Assertion
	 */
	public void addABoxRoleAssertion(ABoxRoleAssertion ra) {
		RolesAssertion.add(ra);
			
		if (ra.role.getRefersTo() instanceof AtomicRigidRole ) {
			roleMap.AddRigidTarget(new Source(ra.source, ra.role), ra.target);
			roleMap.AddRigidLocalizedTarget(new Source(ra.source, ra.role, ra.timestamp), ra.target);

			roleMap.AddRigidTarget(new Source(ra.target, ra.role.getInverse()), ra.source);
			roleMap.AddRigidLocalizedTarget(new Source(ra.target, ra.role.getInverse(), ra.timestamp), ra.source);
		} else {
			roleMap.AddLocalTarget(new Source(ra.source, ra.role, ra.timestamp), ra.target);
			roleMap.AddLocalTarget(new Source(ra.target, ra.role.getInverse(), ra.timestamp), ra.source);
		}
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

		boolean ass;
		for (Source source : roleMap.SourceToTargetsRigid.keySet()) {
			int q = Math.min(roleMap.SourceToTargetsRigid.get(source).size(), qRolesQ.get(source.role.toString()));

			Concept EqR = new QuantifiedRole(source.role, q);

			ass = addABox(new ABoxConceptAssertion(EqR, source.individual.name));
			FORigid.add(new ABoxConceptAssertion(EqR, source.individual.name));

			if (ass == false) {
				System.out.println("duplicate: "+ EqR.toString() + "(" + source.individual.name);
			}

			typeKeeper.addAssertion(source.individual.name, EqR);
		}

		for (Source source : roleMap.SourceToTargetsLocal.keySet()) {
			int q = Math.min(roleMap.SourceToTargetsLocal.get(source).size(), qRolesQ.get(source.role.toString()));

			Concept EqR = new QuantifiedRole(source.role, q);
			int timestamp = source.timestamp;

			while (timestamp != 0) {
				Concept tqL1 = new NextFuture(EqR);
				EqR = tqL1;
				timestamp--;
			}

			ass = addABox(new ABoxConceptAssertion(EqR, source.individual.name));
			FOLocal.add(new ABoxConceptAssertion(EqR, source.individual.name));

			if (ass == false) {
				System.out.println("duplicate: "+ EqR.toString() + "(" + source.individual.name);
			}

			typeKeeper.addAssertion(source.individual.name, EqR);
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
		if (!inconsistent){
			for(ABoxConceptAssertion c: ABox){
				Formula cf = conceptToFormula(c.concept, r);
				cf.substitute(x, new Constant(c.individual.toString()));
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
			} else if (c instanceof BottomConcept) {
				return Bot.getStatic();
			} else if (c instanceof NegatedConcept) {
				NegatedConcept nc = (NegatedConcept) c;
				return new NegatedFormula(conceptToFormula(nc.getRefersTo(), futur));
			} else if (c instanceof ConjunctiveConcept) {
				ConjunctiveConcept cc = (ConjunctiveConcept)c;
				ConjunctiveFormula cf = new ConjunctiveFormula();
				for (Concept d : cc.getConjuncts()) {
					cf.addConjunct(conceptToFormula(d, futur));
				}
				return cf;
			} else if (c instanceof TemporalConcept) {
				TemporalConcept d = (TemporalConcept) c;
				if (c instanceof NextFuture) {
					return new it.unibz.inf.qtl1.formulae.temporal.NextFuture(
							conceptToFormula(d.getRefersTo(), futur));
				} else if (c instanceof NextPast) {
					return new it.unibz.inf.qtl1.formulae.temporal.NextPast(
							conceptToFormula(d.getRefersTo(), futur));
				} else if (c instanceof AlwaysPast) {
					return new it.unibz.inf.qtl1.formulae.temporal.AlwaysPast(
							conceptToFormula(d.getRefersTo(), futur));
				} else if (c instanceof AlwaysFuture) {
					return new it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture(
							conceptToFormula(d.getRefersTo(), futur));
				} else if (c instanceof SometimePast) {
					return new it.unibz.inf.qtl1.formulae.temporal.SometimePast(
							conceptToFormula(d.getRefersTo(), futur));
				} else if (c instanceof SometimeFuture) {
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
			typeKeeper.computeAbstraction();

			System.out.println("Indv:"+typeKeeper.nIndividuals());
			System.out.println("New Indv:"+typeKeeper.nTypes());
	   
			if (typeKeeper.nIndividuals() != typeKeeper.nTypes()) {
				AbstractABox = typeKeeper.getAbstractAbox();
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
	 */
	public void addAbsABoxRoleAssertion(ABoxRoleAssertion ra){
		ShiftedRolesAssertion.add(ra);
		if (ra.role.getRefersTo() instanceof AtomicRigidRole){
			roleMap.AddRigidTarget(new Source(ra.source, ra.role), ra.target);
			roleMap.AddRigidLocalizedTarget(new Source(ra.source, ra.role, ra.timestamp), ra.target);
			roleMap.AddRigidTarget(new Source(ra.target, ra.role.getInverse()), ra.source);
			roleMap.AddRigidLocalizedTarget(new Source(ra.target, ra.role.getInverse(), ra.timestamp), ra.source);
		} else {
			roleMap.AddLocalTarget(new Source(ra.source, ra.role, ra.timestamp), ra.target);
			roleMap.AddLocalTarget(new Source(ra.target, ra.role.getInverse(), ra.timestamp), ra.source);
		}
	}



	/**
	 * Negated Role assertion in Abstracted ABox
	 *
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
						
		if (r.role.getRefersTo() instanceof AtomicRigidRole){
			QNegRigid.putIfAbsent(r.role.toString()+"_"+r.source, successorR);
			QNegRigid.putIfAbsent(r.role.getInverse().toString()+"_"+r.target, PredecessorR);
				
			successorR = QNegRigid.get(r.role.toString()+"_"+r.source);
			PredecessorR = QNegRigid.get(r.role.getInverse().toString()+"_"+r.target);
				
			successorR.add(r.target.toString());
			PredecessorR.add(r.source.toString());
			QNegRigid.replace(r.role.toString()+"_"+r.source,successorR);
			QNegRigid.replace(r.role.getInverse().toString()+"_"+r.target,PredecessorR);
				
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
			
			QNegLocal.putIfAbsent(r.role.toString()+"_"+r.source +"_"+r.timestamp, successorL);
			QNegLocal.putIfAbsent(r.role.getInverse().toString()+"_"+r.target +"_"+r.timestamp, PredecessorL);
			
			successorL = QNegLocal.get(r.role.toString()+"_"+r.source +"_"+r.timestamp);
			PredecessorL = QNegLocal.get(r.role.getInverse().toString()+"_"+r.target +"_"+r.timestamp);
			
			successorL.add(r.target.toString());
			PredecessorL.add(r.source.toString());
			QNegLocal.replace(r.role.toString()+"_"+r.source +"_"+r.timestamp,successorL); // XE1G1(a2) <=> G1_a2_1 (1: timeStamp)
			QNegLocal.replace(r.role.getInverse().toString()+"_"+r.source +"_"+r.timestamp,PredecessorL);
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
			
		System.out.println("");
		System.out.println("------ Shifted TDLITE ABOX");

		int GainP = RolesAssertion.size() - (ShiftedRolesAssertion.size());

		System.out.println("Gain_Rigid= "+GainP); //+"+GainN+"="+(GainP+GainN));

		Set<ABoxRoleAssertion> Inconsist = new HashSet<ABoxRoleAssertion>();
		Inconsist = ShiftedRolesAssertion;

		Inconsist.retainAll(ShiftedNegatedRolesAssertion);

		boolean ass;
		for (Source source : roleMap.SourceToTargetsRigid.keySet()) {
			int q = Math.min(roleMap.SourceToTargetsRigid.get(source).size(), qRolesQ.get(source.role.toString()));

			Concept EqR = new QuantifiedRole(source.role, q);

			ass = addABox(new ABoxConceptAssertion(EqR, source.individual.name));
			FORigid.add(new ABoxConceptAssertion(EqR, source.individual.name));

			if (ass == false) {
				System.out.println("duplicate: "+ EqR.toString() + "(" + source.individual.name);
			}

			typeKeeper.addAssertion(source.individual.name, EqR);
		}

		for (Source source : roleMap.SourceToTargetsLocal.keySet()) {
			int q = Math.min(roleMap.SourceToTargetsLocal.get(source).size(), qRolesQ.get(source.role.toString()));

			Concept EqR = new QuantifiedRole(source.role, q);
			int timestamp = source.timestamp;

			while (timestamp != 0) {
				Concept tqL1 = new NextFuture(EqR);
				EqR = tqL1;
				timestamp--;
			}

			ass = addABox(new ABoxConceptAssertion(EqR, source.individual.name));
			FOLocal.add(new ABoxConceptAssertion(EqR, source.individual.name));

			if (ass == false) {
				System.out.println("duplicate: "+ EqR.toString() + "(" + source.individual.name);
			}

			typeKeeper.addAssertion(source.individual.name, EqR);
		}

		ABox.addAll(ConceptsAssertion);
	}

	/**
	 * Extend the Abstracted ABox constraints given a Q
 	 *
	 */
	public void addExtensionConstraintsAbsABox(int Q){
		System.out.println("");
		System.out.println("------ Shifted TDLITE ABOX");
		System.out.println("**RolesAssertion:"+RolesAssertion.size());

		int GainP = RolesAssertion.size() - (ShiftedRolesAssertion.size());
		System.out.println("Gain_Rigid= "+GainP);

		System.out.println("");

		Set<ABoxRoleAssertion> Inconsist = new HashSet<ABoxRoleAssertion>();
		Inconsist=ShiftedRolesAssertion;

		Inconsist.retainAll(ShiftedNegatedRolesAssertion);
	
		boolean ass=true;
		if (inconsistent == false) {
			for (Source source : roleMap.SourceToTargetsRigid.keySet()) {
				int q = Math.min(roleMap.SourceToTargetsRigid.get(source).size(), Q);

				Concept EqR = new QuantifiedRole(source.role, q);

				ass = addABox(new ABoxConceptAssertion(EqR, source.individual.name));
				FORigid.add(new ABoxConceptAssertion(EqR, source.individual.name));

				if (ass == false) {
					System.out.println("duplicate: "+ EqR.toString() + "(" + source.individual.name);
				}

				typeKeeper.addAssertion(source.individual.name, EqR);
			}

			for (Source source : roleMap.SourceToTargetsLocal.keySet()) {
				int q = Math.min(roleMap.SourceToTargetsLocal.get(source).size(), Q);

				Concept EqR = new QuantifiedRole(source.role, q);
				int timestamp = source.timestamp;

				while (timestamp != 0) {
					Concept tqL1 = new NextFuture(EqR);
					EqR = tqL1;
					timestamp--;
				}

				ass = addABox(new ABoxConceptAssertion(EqR, source.individual.name));
				FOLocal.add(new ABoxConceptAssertion(EqR, source.individual.name));

				if (ass == false) {
					System.out.println("duplicate: "+ EqR.toString() + "(" + source.individual.name);
				}

				typeKeeper.addAssertion(source.individual.name, EqR);
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
			Formula cf = conceptToFormula(c.concept, r);
			cf.substitute(x, new Constant(c.individual.toString()));
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
				if (ri.role.equals(qR.getRole()))
					qrigid++;
			}
		}
		if (qR.getRole().getRefersTo() instanceof AtomicLocalRole) {
			for (ABoxRoleAssertion ri : rigidAs) {
				if (ri.role.equals(qR.getRole()))
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
