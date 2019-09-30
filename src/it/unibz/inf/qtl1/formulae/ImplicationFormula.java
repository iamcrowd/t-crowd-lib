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

public class ImplicationFormula extends Formula{
	Formula lhs,rhs;
	
	public ImplicationFormula(Formula antecedent, Formula consequent){
		super();
		antecedent=antecedent.removeEncapsulation();
		consequent=consequent.removeEncapsulation();
		
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
		if(obj instanceof ImplicationFormula){
			ImplicationFormula o = (ImplicationFormula) obj;
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
		return "("+ lhs +" -> " + rhs+")";
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
		ImplicationFormula nf = new ImplicationFormula((Formula)lhs.clone(),(Formula) rhs.clone());
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
		//TODO : Extend
		return lhs.isTop() && rhs.isBot();
	}
	@Override
	public boolean isTop() {
		//TODO : Extend
		return lhs.isBot() || (lhs.isTop() && rhs.isTop());
	}
	
	public DisjunctiveFormula asDisjunction(){
		return new DisjunctiveFormula(new NegatedFormula(lhs), rhs);
	}
	
	@Override
	public Formula negateToNNF() {
		return asDisjunction().negateToNNF();
	}
	@Override
	public Formula toNNF() {
		return asDisjunction().toNNF();
		
		
	}
}
