package it.unibz.inf.tdllitefpx.concepts.temporal;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class AlwaysFuture extends TemporalConcept {

	public AlwaysFuture(Concept refersTo) {
		super(refersTo);
		// TODO Auto-generated constructor stub
	}

	public String toString(){ return "G " + refersTo.toString(); }
}
