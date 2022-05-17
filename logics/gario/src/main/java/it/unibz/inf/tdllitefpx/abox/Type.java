package it.unibz.inf.tdllitefpx.abox;

import java.util.Set;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class Type {
    Set<Concept> value;

    public int hashCode() {
        return value.hashCode();
    }

    public void addConcept(Concept c) {
        value.add(c);
    }
}
