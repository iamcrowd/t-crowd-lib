package it.unibz.inf.tdllitefpx.abox;
import it.unibz.inf.tdllitefpx.*;

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

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Bot;
import it.unibz.inf.qtl1.formulae.Alphabet;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
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
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;

public class ABox extends ConjunctiveFormula implements FormattableObj {

	Set<ABoxConceptAssertion> ConceptsAssertion = new HashSet<ABoxConceptAssertion>();
	Set<ABoxRoleAssertion> RolesAssertion = new HashSet<ABoxRoleAssertion>();
	Set<Formula> ABoxFormula = new HashSet<Formula>();
	HashMap<String, Set<String>> QRigid = new HashMap<String, Set<String>>();
	HashMap<String, Set<String>> QLocal = new HashMap<String, Set<String>>();
	HashMap<String, Set<String>> QRigidinv = new HashMap<String, Set<String>>();
	
	Alphabet a = new Alphabet();
	Variable x = new Variable("x");

	private static final long serialVersionUID = 1L;

	/**
	 * Create the list of Concepts Assertion	
	 * @param c an ABox Concept Assertion
	 */
	public void addConceptsAssertion(ABoxConceptAssertion c) {
		ConceptsAssertion.add(c);
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
	 * Create the list of Role Assertion
	 * 
	 * @param r an ABox Role Assertion
	 */
	public void addABoxRoleAssertion(ABoxRoleAssertion r) {
		// Create the list of Role Assertions
		RolesAssertion.add(r);
		Set<String> successorR = new HashSet<String>();
		Set<String> successorL = new HashSet<String>();
		Set<String> PredecessorR = new HashSet<String>();
		Set<String> PredecessorL = new HashSet<String>();

		if (r.ro.getRefersTo() instanceof AtomicRigidRole) {
			successorR.add(r.y);
			PredecessorR.add(r.x);
			QRigid.putIfAbsent(r.ro.toString() + "_" + r.x, successorR);
			QRigid.putIfAbsent(r.ro.getInverse().toString() + "_" + r.y, PredecessorR);

			successorR = QRigid.get(r.ro.toString() + "_" + r.x);
			PredecessorR = QRigid.get(r.ro.getInverse().toString() + "_" + r.y);

			successorR.add(r.y);
			PredecessorR.add(r.x);
			QRigid.replace(r.ro.toString() + "_" + r.x, successorR);
			QRigid.replace(r.ro.getInverse().toString() + "_" + r.y, PredecessorR);

		}

		successorL.add(r.y);
		PredecessorL.add(r.x);
		QLocal.putIfAbsent(r.ro.toString() + "_" + r.x + "_" + r.t, successorL);
		QLocal.putIfAbsent(r.ro.getInverse().toString() + "_" + r.y + "_" + r.t, PredecessorL);

		successorL = QLocal.get(r.ro.toString() + "_" + r.x + "_" + r.t);
		PredecessorL = QLocal.get(r.ro.getInverse().toString() + "_" + r.y + "_" + r.t);

		successorL.add(r.y);
		PredecessorL.add(r.x);
		QLocal.replace(r.ro.toString() + "_" + r.x + "_" + r.t, successorL);
		QLocal.replace(r.ro.getInverse().toString() + "_" + r.x + "_" + r.t, PredecessorL);

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
	
	/**
	 * Stats for ABox
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getStatsABox() {
		HashMap<String, String> stats = new HashMap<String, String>();

		stats.put("Concept_Assertion:", " " + ConceptsAssertion.size());
		stats.put("Roles_Assertion:", " " + RolesAssertion.size());
		return stats;
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

				for (String keyL : QLocal.keySet()) {
					String[] keyLi = keyL.split("_");
					String index = keyLi[0].concat("_" + keyLi[1]);

					int Qtabox = QRigid.get(index).size();
					if (r.toString().equals(keyLi[0])) {
						int Qtbox = qRolesQ.get(keyLi[0]);
						int j = Math.min(Qtbox, Qtabox);

						while (j != 0) { // Cardinality
							QuantifiedRole qL = new QuantifiedRole(r, j);
							Concept cr = (Concept) qL;

							int t = Integer.parseInt(keyLi[2]);
							while (t != 0) {// States
								Concept tqL1 = new NextFuture(cr);
								cr = tqL1;
								t--;
							}
							addConceptsAssertion(new ABoxConceptAssertion(cr, keyLi[1]));
							j--;
						}
					} else if (r.getInverse().toString().equals(keyLi[0])) {
						int Qtbox = qRolesQ.get(r.toString());
						int j = Math.min(Qtbox, Qtabox);
						while (j != 0){ // Cardinality
							QuantifiedRole qLinv = new QuantifiedRole(r.getInverse(), j);

							Concept cinvr = (Concept) qLinv;

							int t = Integer.parseInt(keyLi[2]);
							while (t != 0){// States
								Concept tqL2 = new NextFuture(cinvr);
								cinvr = tqL2;
								t--;
							}
							addConceptsAssertion(new ABoxConceptAssertion(cinvr, keyLi[1]));
							j--;
						}
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

						while (j != 0){ // Cardinality
							QuantifiedRole qL = new QuantifiedRole(r, j);
							Concept cr = (Concept) qL;
							int t = Integer.parseInt(keyLi[2]);
							
							while (t != 0) {// States
								Concept tqL1 = new NextFuture(cr);
								cr = tqL1;
								t--;
							}
							addConceptsAssertion(new ABoxConceptAssertion(cr, keyLi[1]));
							j--;
						}
					} else if (r.getInverse().toString().equals(keyLi[0])) {
						int Qtbox = qRolesQ.get(r.toString());
						int j = Math.min(Qtbox, Qtaboxi);

						while (j != 0){ // Cardinality
							QuantifiedRole qLinv = new QuantifiedRole(r.getInverse(), j);
							Concept cinvr = (Concept) qLinv;
							int t = Integer.parseInt(keyLi[2]);
							
							while (t != 0) {// States
								Concept tqL2 = new NextFuture(cinvr);
								cinvr = tqL2;
								t--;
							}
							addConceptsAssertion(new ABoxConceptAssertion(cinvr, keyLi[1]));
							j--;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Generate QTL formula
	 * 
	 * @return Formula
	 */
	public Formula getABoxFormula(boolean futur) {
		ConjunctiveFormula qtl = new ConjunctiveFormula();
		for (ABoxConceptAssertion c : ConceptsAssertion) {
			Formula cf = conceptToFormula(c.c, futur);
			cf.substitute(x, new Constant(c.value));
			qtl.addConjunct(cf);
		}
		System.out.println("ABox: " + qtl);
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
			System.out.println("Qrigid:" + qrigid);
		}
		if (qR.getRole().getRefersTo() instanceof AtomicLocalRole) {
			for (ABoxRoleAssertion ri : rigidAs) {
				if (ri.ro.equals(qR.getRole()))
					qrigid++;
			}
			// System.out.println("Qrigid:"+qrigid);
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
