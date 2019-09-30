package it.unibz.inf.qtl1.formulae.quantified;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.DisjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Term;
import it.unibz.inf.qtl1.terms.Variable;

public abstract class QuantifiedFormula extends Formula {
	Formula subF;
	Variable qVar;
	
	public QuantifiedFormula(Formula subFormula,Variable qVariable){
		subFormula=subFormula.removeEncapsulation();
		
		if(subFormula instanceof Atom && cloneAtoms)
			subFormula=(Atom)((Atom)subFormula).clone();
		
		this.qVar=qVariable;
		this.subF=subFormula;
	}
	
	/***
	 * Removes the universal quantifiers in the subformula
	 * @return The variables that become unbound by removing the Quantifier
	 */
	public Set<Variable> removeUniversals() {
		Set<Variable> set = new HashSet<Variable>();

		set.addAll(subF.removeUniversals());
		if(subF instanceof UniversalFormula){
			UniversalFormula u = (UniversalFormula) subF;
			set.add(u.getQuantifiedVar());
			subF=u.getSubFormulae().get(0);
		}
		return set;
	}
	public Variable getQuantifiedVar(){return qVar;}
	public Set<Constant> getConstants(){
		return subF.getConstants();
	}
	public List<Formula> getSubFormulae(){
		List<Formula> l=  new ArrayList<Formula>();
		l.add(subF);
		return l;
	}
	
	public void makeUniqueVar(Set<Variable> inUse){
		if (inUse.contains(qVar)){
			Variable newV = new Variable(qVar.getName()+"_"+inUse.size());
			inUse.add(newV);
			subF.substitute(qVar, newV);
			qVar=newV;
		}
	}
	
	@Deprecated
	public void renameVar(Variable oldName,Variable newName){
		assert(false);
		
		if(oldName.equals(oldName))
			qVar=newName;
		subF.renameVar(oldName, newName);
	}
	
	public void substitute(Variable var, Term t){
		if(!var.equals(qVar)){
			subF.substitute(var, t);
		}
    }
	
	public boolean equals(Object obj){
		if(obj.getClass().equals(this.getClass())){
			QuantifiedFormula o = (QuantifiedFormula)obj;
			if(subF.equals(o.subF) && qVar.equals(o.qVar))
				return true;
		}
		return false;
	}
	public int getArity(){ return 1; }
	

	public void atomsToPropositions() throws NotGroundException {
		if(subF instanceof Atom)
			subF= ((Atom)subF).atomToProposition() ;
		subF.atomsToPropositions();
	}

	public void replaceSubFormula(Formula former, Formula current) {
		if(current instanceof ConjunctiveFormula ||
				current instanceof DisjunctiveFormula)
			if(current.getSubFormulae().size()==1)
				current=current.getSubFormulae().get(0);
		if(subF.equals(former))
			subF=current;
		
	}
	
	
}
