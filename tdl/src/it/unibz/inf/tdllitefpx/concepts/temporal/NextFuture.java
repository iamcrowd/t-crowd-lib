package it.unibz.inf.tdllitefpx.concepts.temporal;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class NextFuture extends TemporalConcept {

	public NextFuture(Concept refersTo) {
		super(refersTo);
		// TODO Auto-generated constructor stub
	}

	public String toString(){ return "X " + refersTo.toString();}
}
