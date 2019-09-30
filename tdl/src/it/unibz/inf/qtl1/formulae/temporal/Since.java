package it.unibz.inf.qtl1.formulae.temporal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.DisjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;

public  class Since extends TemporalFormula {
	
	Formula lhs,rhs;
	
	public Since(Formula lhs, Formula rhs) {
		super(null);
		lhs.removeEncapsulation();
		rhs.removeEncapsulation();
		if(lhs instanceof Atom && cloneAtoms)
			this.lhs=(Atom)((Atom)lhs).clone();
		else
			this.lhs=lhs;
		if(rhs instanceof Atom && cloneAtoms)
			this.rhs=(Atom)((Atom)rhs).clone();
		else		
			this.rhs=rhs;
	}

	public List<Formula> getSubFormulae(){
		List<Formula> l=  new ArrayList<Formula>();
		l.add(lhs);
		l.add(rhs);
		return l;
	}
	
	public boolean equals(Object obj){
		if(obj.getClass().equals(this.getClass())){
			Since o = (Since) obj;
			if(o.lhs.equals(this.lhs) && o.rhs.equals(this.rhs))
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
		return "("+lhs+" S "+rhs+")";
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
		Since nf = new Since((Since)lhs.clone(),(Since) rhs.clone());
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
}
