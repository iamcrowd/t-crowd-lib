package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class Always extends TemporalFormula{

	public Always(Formula rTo) {
		super(rTo);
	}
	
	public String toString(){
		return "GH"+refersTo;
	}

	public Formula normalize(){
		return new AlwaysFuture(new AlwaysPast(refersTo));
	}
}
