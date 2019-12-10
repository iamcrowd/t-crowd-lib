package it.unibz.inf.tdllitefpx.abox;

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
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;

public class ABox extends ConjunctiveFormula implements FormattableObj {

	Set<ABoxConceptAssertion> ConceptsAssertion = new HashSet<ABoxConceptAssertion>();
	Set<ABoxRoleAssertion> RolesAssertion = new HashSet<ABoxRoleAssertion>();
	Set<Formula> ABoxFormula = new HashSet<Formula>();

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
	}

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

	public Set<Role> getRolesABox() {
		HashSet<Role> roles = new HashSet<Role>();
		for (ABoxRoleAssertion r : RolesAssertion) {
			roles.add(r.ro);
		}
		return roles;
	}

	public Map<String, String> getStatsABox() {
		HashMap<String, String> stats = new HashMap<String, String>();

		stats.put("Concept_Assertion:", " " + ConceptsAssertion.size());
		stats.put("Roles_Assertion:", " " + RolesAssertion.size());
		return stats;
	}

	public void addExtensionConstraintsABox(TBox tbox) {
		Set<QuantifiedRole> qRoles = tbox.getQuantifiedRoles();
		// Set <Role> Roles=getRolesABox();
		/*
		 * >= 2.Name(John) >= 1.Name(John) /*>= 2.NameInv(Kennedy) >= 1.NameInv(Kennedy)
		 */
		for (QuantifiedRole qR : qRoles) {
			for (ABoxRoleAssertion r : RolesAssertion){
				if (qR.getRole() == r.ro) {
					QuantifiedRole qRinv = new QuantifiedRole(r.ro.getInverse(), qR.getQ());
					addConceptsAssertion(new ABoxConceptAssertion(qR, r.getx().toString()));
					addConceptsAssertion(new ABoxConceptAssertion(qRinv, r.gety().toString()));
					// System.out.println(" role ABox: "+qR.toString());
				}

			}
		}
	}

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
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		StringBuilder sb = new StringBuilder();
		
		for (ABoxConceptAssertion c : ConceptsAssertion) {
			sb.append(c.toString(fmt));
		}
		
		for (ABoxRoleAssertion r : RolesAssertion) {
			sb.append(r.toString(fmt));
		}
		
		return sb.toString();
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
