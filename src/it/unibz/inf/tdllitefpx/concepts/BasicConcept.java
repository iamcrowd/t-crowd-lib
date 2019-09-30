package it.unibz.inf.tdllitefpx.concepts;

import java.util.HashSet;
import java.util.Set;

public abstract class BasicConcept extends Concept {
	@Override
	public Set<Concept> getBasicConcepts() {
		Set<Concept> c = new HashSet<Concept>();
		if(!(this instanceof BottomConcept))
			c.add(this);
		return c;
	}
}
