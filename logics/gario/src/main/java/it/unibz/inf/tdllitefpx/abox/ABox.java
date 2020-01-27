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
	 * Create the list of Concepts Assertion
	 * @param r an ABox Role Assertion
	 */
	public void addABoxRoleAssertion(ABoxRoleAssertion r) {
		RolesAssertion.add(r);
		Set<String>successorR = new HashSet<String>();
		Set<String>successorL = new HashSet<String>();
				
		if (r.ro.getRefersTo() instanceof AtomicRigidRole){
			successorR.add(r.y);
			QRigid.putIfAbsent(r.ro.toString() + "_" + r.x, successorR);
				
			successorR = QRigid.get(r.ro.toString() + "_" + r.x);
			successorR.add(r.y);
			QRigid.replace(r.ro.toString() + "_" + r.x,successorR);	
		}	
			
		if (r.ro.getRefersTo() instanceof AtomicLocalRole){
			successorL.add(r.y);
			QLocal.putIfAbsent(r.ro.toString() + "_" + r.x +"_" + r.t, successorL);
				
			successorL = QLocal.get(r.ro.toString() + "_" + r.x + "_" + r.t);
			successorL.add(r.y);
			QLocal.replace(r.ro.toString() + "_" + r.x + "_" + r.t, successorL);
				
		}		
	}

	/**
	 * 
	 * @return
	 */
	public Set<Constant> getConstantsABox() {
		// isExtended = false; // TODO: Check if this is really the case
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
	 * 
	 * @return
	 */
	public Set<Role> getRolesABox() {
		HashSet<Role> roles = new HashSet<Role>();
		for (ABoxRoleAssertion r : RolesAssertion) {
			roles.add(r.ro);
		}
		return roles;
	}

	/**
	 * 
	 * @param qR
	 * @return
	 */
	public Map<String, Integer> getQRigidABox(Set<QuantifiedRole> qR){
		HashMap<String, Integer> qRAQ = new HashMap<String, Integer>();
		for (QuantifiedRole qr: qR){
			qRAQ.putIfAbsent(qr.getRole().toString(), qr.getQ());
			
			if(qRAQ.get(qr.getRole().toString())<qr.getQ()) {
				qRAQ.replace(qr.getRole().toString(), qr.getQ());
			}	
			
		}
		return qRAQ;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getStatsABox(){
		HashMap<String, String> stats = new HashMap<String, String>();

		stats.put("Concept_Assertion:", " " + ConceptsAssertion.size());
		stats.put("Roles_Assertion:", " " + RolesAssertion.size());
		return stats;
	}

	/**
	 * 
	 * @param tbox
	 */
	public void addExtensionConstraintsABox(TBox tbox){
		Set<QuantifiedRole> qRoles = tbox.getQuantifiedRoles();
		Map<String, Integer> qRolesQ = tbox.getQuantifiedRolesQ(qRoles);
		// Set <Role> Roles=getRolesABox();
		/*
		 * >= 2.Name(John) >= 1.Name(John) /*>= 2.NameInv(Kennedy) >= 1.NameInv(Kennedy)
		 */
		for(String keyR : QRigid.keySet()){
			String[]keyRi = keyR.split("_");		
			int Qtbox = qRolesQ.get(keyRi[0]);
			int Qtabox = QRigid.get(keyR).size();
				
			for(ABoxRoleAssertion r: RolesAssertion){
				if(r.ro.toString().equals(keyRi[0])){//ICI TEMPORAL ROLES ASSERTION
					int j = Math.min(Qtbox, Qtabox);
					while (j != 0 ){
						QuantifiedRole qRinv = new QuantifiedRole(r.ro.getInverse(), j);
						QuantifiedRole qR1 = new QuantifiedRole(r.ro, j);
				
						int i = r.t;
						Concept cr = (Concept)qR1;
						Concept cinvr = (Concept)qRinv;
						while (i != 0 ){
							Concept tqR1 = new NextFuture(cr);
							cr = tqR1;
							Concept tqR2 = new NextFuture(cinvr);
							cinvr = tqR2;
							i--;
						}
						addConceptsAssertion(new ABoxConceptAssertion (cr,r.getx().toString()));//eliminer la redondance ICI
						addConceptsAssertion(new ABoxConceptAssertion (cinvr,r.gety().toString()));
						j--;
					}
				}				
			}
		}
				
		Set<Role> Roles = getRolesABox();
		for (Role r: Roles){
			for(String keyL : QLocal.keySet()){
			    String[]keyLi=keyL.split("_");
			    Set<String> x=new HashSet();
			    int Qtbox=qRolesQ.get(keyLi[0]);
				int Qtaboxi=QLocal.get(keyL).size();
						
				int j=Math.min(Qtbox, Qtaboxi);
						
						
				if (r.toString().equals(keyLi[0])){
					while (j!=0) { //Cardinality
						QuantifiedRole qL=new QuantifiedRole(r, j);	
	    				QuantifiedRole qLinv=new QuantifiedRole(r.getInverse(), j);
	    				Concept cr= (Concept)qL;
	    				Concept cinvr= (Concept)qLinv;
	    					
	    				int t= Integer.parseInt(keyLi[2]);
	    				while (t!=0 ){ //States
	    					System.out.println("state:"+t);
	    					Concept tqL1= new NextFuture(cr);
	    					cr= tqL1;
	    					Concept tqL2= new NextFuture(cinvr);
	    					cinvr= tqL2;
				    		t--;	
	    				}
	    				addConceptsAssertion(new ABoxConceptAssertion (cr,keyLi[1]));
	    				
    					for (String y: QLocal.get(keyL)){
    						addConceptsAssertion(new ABoxConceptAssertion (cinvr,y));
    					}
    					
	    				j--;
					}
			    }
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public Formula getABoxFormula() {
		ConjunctiveFormula qtl = new ConjunctiveFormula();
		for (ABoxConceptAssertion c : ConceptsAssertion) {
			Formula cf = conceptToFormula(c.c);
			cf.substitute(x, new Constant(c.value));
			qtl.addConjunct(cf);
		}
		System.out.println("ABoxconceptFormula:" + qtl);
		return qtl;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	public Formula conceptToFormula(Concept c) {
		if (c instanceof AtomicConcept)
			return new Atom(c.toString(), x);
		else if (c instanceof QuantifiedRole) {
			QuantifiedRole qE = (QuantifiedRole) c;
			Atom atom = a.get("E" + qE.getQ() + qE.getRole().toString(), 1);
			atom.setArg(0, x);
			return atom;
		} else if (c instanceof QuantifiedRole) {
			QuantifiedRole qE = (QuantifiedRole) c;
			Atom atom = a.get("E" + qE.getQ() + qE.getRole().getInverse().toString(), 1);
			atom.setArg(0, x);
			return atom;
		} else if (c instanceof BottomConcept)
			return Bot.getStatic();
		else if (c instanceof NegatedConcept) {
			NegatedConcept nc = (NegatedConcept) c;
			return new NegatedFormula(conceptToFormula(nc.getRefersTo()));
		} else if (c instanceof ConjunctiveConcept) {
			ConjunctiveConcept cc = (ConjunctiveConcept) c;
			ConjunctiveFormula cf = new ConjunctiveFormula();
			for (Concept d : cc.getConjuncts()) {
				cf.addConjunct(conceptToFormula(d));
			}
			return cf;
		} else if (c instanceof TemporalConcept) {
			TemporalConcept d = (TemporalConcept) c;
			if (c instanceof NextFuture) {
				return new it.unibz.inf.qtl1.formulae.temporal.NextFuture(conceptToFormula(d.getRefersTo()));
			} else if (c instanceof NextPast) {
				return new it.unibz.inf.qtl1.formulae.temporal.NextPast(conceptToFormula(d.getRefersTo()));
			} else if (c instanceof AlwaysPast) {
				return new it.unibz.inf.qtl1.formulae.temporal.AlwaysPast(conceptToFormula(d.getRefersTo()));
			} else if (c instanceof AlwaysFuture) {
				return new it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture(conceptToFormula(d.getRefersTo()));
			} else if (c instanceof SometimePast) {
				return new it.unibz.inf.qtl1.formulae.temporal.SometimePast(conceptToFormula(d.getRefersTo()));
			} else if (c instanceof SometimeFuture) {
				return new it.unibz.inf.qtl1.formulae.temporal.SometimeFuture(conceptToFormula(d.getRefersTo()));
			}

		}

		System.err.println("Unexpected case " + c.getClass().toString());
		return null;
	}

	@Override
    public String toString(OutputFormat fmt) throws  SymbolUndefinedException {
		// TODO Auto-generated method stub
		return this.toString();
	}
/*	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		StringBuilder sb = new StringBuilder();
		
		for (ABoxConceptAssertion c : ConceptsAssertion) {
			sb.append(c.toString(fmt));
		}
		
		for (ABoxRoleAssertion r : RolesAssertion) {
			sb.append(r.toString(fmt));
		}
		
		return sb.toString();
	}
*/
	
	/**
	 * 
	 * @param qR
	 * @param rigidAs
	 * @return
	 */
	public int getQAssertion(QuantifiedRole qR, Set<ABoxRoleAssertion> rigidAs){ 
		int qrigid=0;
		int qlocal=0;
			
		if (qR.getRole().getRefersTo() instanceof AtomicRigidRole){
			for (ABoxRoleAssertion ri: rigidAs) {
				if (ri.ro.equals(qR.getRole())) qrigid++;
			}
			System.out.println("Qrigid:"+qrigid);
		}
		
		if (qR.getRole().getRefersTo() instanceof AtomicLocalRole ){
			for (ABoxRoleAssertion ri: rigidAs ){
				if (ri.ro.equals(qR.getRole())) qrigid++;
			}
			System.out.println("Qrigid:"+qrigid);
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
