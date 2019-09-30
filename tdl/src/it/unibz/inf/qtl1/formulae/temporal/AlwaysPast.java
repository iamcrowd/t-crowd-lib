package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class AlwaysPast extends TemporalFormula {
	
	public AlwaysPast(Formula refersTo) {
		super(refersTo);

	}

	public String toString(){
		return "H"+refersTo;
	}
}
