package it.unibz.inf.tdllitefpx.concepts.temporal;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class SometimeFuture extends TemporalConcept {

	public SometimeFuture(Concept refersTo) {
		super(refersTo);
	}
	
	public String toString(){ return "F " + refersTo.toString();}
}
