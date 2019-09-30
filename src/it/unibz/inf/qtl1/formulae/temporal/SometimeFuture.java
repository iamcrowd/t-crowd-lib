package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class SometimeFuture extends TemporalFormula {

	public SometimeFuture(Formula refersTo) {
		super(refersTo);

	}
	
	public String toString(){
		return "F"+refersTo;
	}

}
