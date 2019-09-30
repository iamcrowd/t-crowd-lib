package it.unibz.inf.qtl1.formulae.temporal;

import it.unibz.inf.qtl1.formulae.Formula;

public class Sometime extends TemporalFormula {
	public Sometime(Formula rTo){
		super(rTo);
	}
	
	public String toString(){
		return "OF"+refersTo;
	}
	
	public Formula normalize(){
		return new SometimePast(new SometimeFuture(refersTo));
	}
}
