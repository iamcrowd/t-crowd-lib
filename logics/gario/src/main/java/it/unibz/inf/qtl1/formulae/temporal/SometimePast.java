package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class SometimePast extends TemporalFormula {

	public SometimePast(Formula refersTo) {
		super(refersTo);
	}
	
	public String toString(){
		return "O"+refersTo;
	}

}
