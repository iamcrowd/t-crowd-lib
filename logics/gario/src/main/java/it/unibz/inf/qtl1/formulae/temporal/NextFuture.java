package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class NextFuture extends TemporalFormula {

	public NextFuture(Formula refersTo) {
		super(refersTo);

	}
	public String toString(){
		return "X"+refersTo;
	}

}
