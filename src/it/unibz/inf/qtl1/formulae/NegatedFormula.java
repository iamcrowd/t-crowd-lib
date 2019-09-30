package it.unibz.inf.qtl1.formulae;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NegatedFormula extends Formula {
	Formula refersTo;
	
	public NegatedFormula(Formula refersTo){
		
		refersTo=refersTo.removeEncapsulation();
		
		if(refersTo instanceof Atom && cloneAtoms)
			refersTo=(Atom)((Atom)refersTo).clone();
		
		this.refersTo=refersTo;
	}
	
	public Formula getRefersTo(){ return refersTo;}
	
	public List<Formula> getSubFormulae(){
		List<Formula> l=  new ArrayList<Formula>();
		l.add(refersTo);
		return l;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof NegatedFormula)
			if(refersTo.equals(((NegatedFormula)obj).refersTo))
				return true;
		return false;
	}
	public int getArity(){ return 1; }
	public Set<Constant> getConstants(){
		return refersTo.getConstants();
	}
	public String toString(){
		return "!"+refersTo;
	}
	
	public Set<Variable> removeUniversals() {
		Set<Variable> set = new HashSet<Variable>();
		set.addAll(refersTo.removeUniversals());
		if(refersTo instanceof UniversalFormula){
			UniversalFormula u = (UniversalFormula) refersTo;
			set.add(u.getQuantifiedVar());
			refersTo=u.getSubFormulae().get(0);
		}
		return set;
	}
	public Object clone(){
		NegatedFormula nf = new NegatedFormula((Formula)refersTo.clone());
		return nf;
		
	}

	public void atomsToPropositions() throws NotGroundException {
		if(refersTo instanceof Atom)
			refersTo= ((Atom)refersTo).atomToProposition() ;
		refersTo.atomsToPropositions();
				
	}
	public void replaceSubFormula(Formula former, Formula current) {
		if(current instanceof ConjunctiveFormula ||
				current instanceof DisjunctiveFormula)
			if(current.getSubFormulae().size()==1)
				current=current.getSubFormulae().get(0);
		if(refersTo.equals(former))
			refersTo=current;
		
	}
	@Override
	public boolean isBot() {
		//TODO: Extend
		return refersTo.isTop();
	}
	@Override
	public boolean isTop() {
		// TODO: Extend
		return refersTo.isBot();
	}

	@Override
	public Formula negateToNNF() {
		return refersTo.toNNF();
	}

	@Override
	public Formula toNNF() {
		if( refersTo instanceof Proposition)
			return this;
		return refersTo.negateToNNF();
	}
	
	@Override
	public Formula convertToNNF() {
		return this.toNNF();
	}
}
