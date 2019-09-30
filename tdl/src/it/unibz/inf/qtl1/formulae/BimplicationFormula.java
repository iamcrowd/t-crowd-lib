package it.unibz.inf.qtl1.formulae;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Represents a biimplication <->
 * @author Marco Gario
 *
 */
public class BimplicationFormula extends Formula{
	Formula lhs,rhs;
	
	public BimplicationFormula(Formula left, Formula right){
		super();
		Formula antecedent=left.removeEncapsulation();
		Formula consequent=right.removeEncapsulation();
		
		if(antecedent instanceof Atom && cloneAtoms)
			this.lhs=(Atom)((Atom)antecedent).clone();
		else
			this.lhs=antecedent;	
		if(consequent instanceof Atom && cloneAtoms)
			this.rhs=(Atom)((Atom)consequent).clone();
		else
			this.rhs=consequent;
	}
	public List<Formula> getSubFormulae(){
		List<Formula> l=  new ArrayList<Formula>();
		l.add(lhs);
		l.add(rhs);
		return l;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof BimplicationFormula){
			BimplicationFormula o = (BimplicationFormula) obj;
			if(o.lhs.equals(lhs) && o.rhs.equals(rhs))
				return true;
		}
		return false;
	}
	public int getArity(){ return 2; }
	public Set<Constant> getConstants(){
		HashSet<Constant> set=new HashSet<Constant>();
		set.addAll(lhs.getConstants());
		set.addAll(rhs.getConstants());
		
		return set;
	}
	
	public String toString(){
		return "("+ lhs +" <-> " + rhs+")";
	}
	
	public Set<Variable> removeUniversals() {
		Set<Variable> set = new HashSet<Variable>();
		set.addAll(lhs.removeUniversals());
		
		if(lhs instanceof UniversalFormula){
			UniversalFormula u = (UniversalFormula) lhs;
			set.add(u.getQuantifiedVar());
			lhs=u.getSubFormulae().get(0);
		}

		set.addAll(rhs.removeUniversals());
		if(rhs instanceof UniversalFormula){
			UniversalFormula u = (UniversalFormula) rhs;
			set.add(u.getQuantifiedVar());
			rhs=u.getSubFormulae().get(0);
		}

		return set;
	}
	public Object clone(){
		BimplicationFormula nf = new BimplicationFormula((Formula)lhs.clone(),(Formula) rhs.clone());
		return nf;
		
	}
	public void atomsToPropositions() throws NotGroundException {
		if(lhs instanceof Atom)
			lhs= ((Atom)lhs).atomToProposition() ;
		if(rhs instanceof Atom)
			rhs= ((Atom)rhs).atomToProposition() ;
		lhs.atomsToPropositions();
		rhs.atomsToPropositions();
				
	}
	
	public void replaceSubFormula(Formula former, Formula current) {
		if(current instanceof ConjunctiveFormula ||
				current instanceof DisjunctiveFormula)
			if(current.getSubFormulae().size()==1)
				current=current.getSubFormulae().get(0);
		if(former.equals(lhs))
			lhs=current;
		else if (former.equals(rhs))
			rhs=current;
		
	}
	@Override
	public boolean isBot() {
		//TODO : more cases
		return (lhs.isBot() && rhs.isTop()) || (lhs.isTop() && rhs.isBot());
	}
	@Override
	public boolean isTop() {
		return (lhs.isTop() && rhs.isTop()) || (lhs.isBot() && rhs.isBot());
	}
	
	public ConjunctiveFormula asConjunction(){
		return new ConjunctiveFormula(new ImplicationFormula(lhs, rhs), new ImplicationFormula(rhs, lhs));
	}
	
	@Override
	public Formula negateToNNF() {
		return asConjunction().negateToNNF();
	}
	@Override
	public Formula toNNF() {
		//return asConjunction().toNNF();
		// !(A <-> B) = (A v !B) /\ (!A v B)
		return new ConjunctiveFormula(
				new DisjunctiveFormula(lhs.negateToNNF(), rhs),
				new DisjunctiveFormula(rhs.negateToNNF(), lhs));
		
	}
	
	@Override
	public Formula toCNF(){
		Formula lhsNNF = lhs.toNNF();
		Formula rhsNNF = rhs.toNNF();
		
		Formula cnf_l = new DisjunctiveFormula(
				(new NegatedFormula(lhsNNF)).toNNF(),
				rhsNNF);
		
		cnf_l.distribute();
		
		Formula cnf_r = new DisjunctiveFormula(
				(new NegatedFormula(rhsNNF)).toNNF(),
				lhsNNF);
		
		
		return new ConjunctiveFormula(cnf_r.distribute(),cnf_l.distribute());
	}
}
