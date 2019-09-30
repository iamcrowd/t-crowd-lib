package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class AlwaysFuture extends TemporalFormula {

	public AlwaysFuture(Formula refersTo) {
		super(refersTo);
	}
	
	public String toString(){
		return "G"+refersTo;
	}
	
}
