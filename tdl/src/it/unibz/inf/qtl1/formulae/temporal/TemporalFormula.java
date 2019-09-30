package it.unibz.inf.qtl1.formulae.temporal;

import java.lang.reflect.Constructor;
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

public abstract class TemporalFormula extends Formula {
	Formula refersTo;
	
	public TemporalFormula(Formula refersTo){
		if(refersTo!=null) refersTo=refersTo.removeEncapsulation();
		if(refersTo instanceof Atom && cloneAtoms)
			refersTo=(Atom)((Atom)refersTo).clone();

		this.refersTo=refersTo;
	}
	
	public List<Formula> getSubFormulae(){
		List<Formula> l=  new ArrayList<Formula>();
		l.add(refersTo);
		return l;
	}
	
	public int getArity(){ return 1; }
	public Set<Constant> getConstants(){
		return refersTo.getConstants();
	}
	
	public boolean equals(Object obj){
		if(obj.getClass().equals(this.getClass())){
			if(((TemporalFormula)obj).refersTo.equals(this.refersTo))
				return true;
		}
		return false;
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
		try {
			Class<?>[] ArgsClass = new Class<?>[] {Formula.class};
			Object[] Args = new Object[] {refersTo.clone()};
			Constructor<?> ArgsConstructor = this.getClass().getConstructor(ArgsClass);
	        Formula f = (Formula) ArgsConstructor.newInstance(Args);
	        //System.err.println(this);
	        if(this.toString().equals(refersTo.toString()))
	        	assert(false);
	        return f;
	        
	      } catch (Exception e) {
	          System.out.println(e);
	      }
		
		return null;
	}
	public void atomsToPropositions() throws NotGroundException {
		if(refersTo instanceof Atom)
			refersTo= ((Atom)refersTo).atomToProposition() ;
		refersTo.atomsToPropositions();
		
	}
	public void replaceSubFormula(Formula former, Formula current) {
		if(refersTo.equals(former)){
			if(current instanceof ConjunctiveFormula ||
					current instanceof DisjunctiveFormula)
				if(current.getSubFormulae().size()==1)
					current=current.getSubFormulae().get(0);
			refersTo=current;
		}
	}
	
	@Override
	public Formula negateToNNF() {
		System.err.println("negateToNNF Unimplemented for "+ this.getClass().toString());
		return null;
	}

	@Override
	public Formula toNNF() {
		//TODO: implement
		System.err.println("negateToNNF Unimplemented for " +this.getClass().toString());
		return null;
	}
	
}

/**
;; FUTURE
| "X" ltl_expr             ;; next state
| "G" ltl_expr             ;; globally
| "F" ltl_expr             ;; finally
| ltl_expr "U" ltl_expr    ;; until
| ltl_expr "V" ltl_expr    ;; releases
;; PAST
| "Y" ltl_expr             ;; previous state
| "Z" ltl_expr             ;; not previous state not
| "H" ltl_expr             ;; historically
| "O" ltl_expr             ;; once 
| ltl_expr "S" ltl_expr    ;; since
| ltl_expr "T" ltl_expr    ;; triggered
**/

