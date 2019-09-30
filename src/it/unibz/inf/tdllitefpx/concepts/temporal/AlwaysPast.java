package it.unibz.inf.tdllitefpx.concepts.temporal;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class AlwaysPast extends TemporalConcept {

	public AlwaysPast(Concept refersTo) {
		super(refersTo);
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){ return "H " + refersTo.toString();}

}
