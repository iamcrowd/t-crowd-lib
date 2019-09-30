package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class NextPast extends TemporalFormula {
	
	public NextPast(Formula refersTo) {
		super(refersTo);
	}

	public String toString(){
		return "Y"+refersTo;
	}
}
