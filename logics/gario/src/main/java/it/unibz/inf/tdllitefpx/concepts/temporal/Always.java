package it.unibz.inf.tdllitefpx.concepts.temporal;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class Always extends TemporalConcept {

	public Always(Concept refersTo) {
		super(refersTo);
		// TODO Auto-generated constructor stub
	}

	public String toString(){ return "HG " + refersTo.toString(); }
}
