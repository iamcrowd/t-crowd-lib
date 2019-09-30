package it.unibz.inf.tdllitefpx.concepts.temporal;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class NextPast extends TemporalConcept {

	public NextPast(Concept refersTo) {
		super(refersTo);
	}

	public String toString(){ return "Y " + refersTo.toString();}
}
