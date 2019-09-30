package it.unibz.inf.tdllitefpx.concepts.temporal;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class SometimePast extends TemporalConcept {

	public SometimePast(Concept refersTo) {
		super(refersTo);
	}

	public String toString(){ return "O " + refersTo.toString();}
}
