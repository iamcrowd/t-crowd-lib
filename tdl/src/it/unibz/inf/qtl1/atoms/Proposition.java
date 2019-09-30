package it.unibz.inf.qtl1.atoms;

import it.unibz.inf.qtl1.formulae.Formula;

/**
 * Subclass of {@link Atom}, is actually an atom of arity 0.
 * Is defined independently to allow easier operations on
 * propositional formulas.
 * 
 * @author Marco Gario
 *
 */
public class Proposition extends Atom implements Comparable<Proposition>{

	public Proposition(String name){
		super(name,0);
	}
	
	public Object clone(){
		if(Formula.cloneAtoms)
			return new Proposition(this.name);
		else 
			return this;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Proposition)
			return ((Proposition)obj).name.equals(name);
		return false;
	}
	
	public int hashCode() {
		return (name).hashCode();
	}

	@Override
	public int compareTo(Proposition o) {
			return o.name.compareTo(name);
	}
	
}
