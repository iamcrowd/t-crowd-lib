package it.unibz.inf.tdllitefpx.abox;

import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class Type {
    Set<Concept> conceptSet = new HashSet<Concept>();

    public int hashCode() {
        return conceptSet.hashCode();
    }

    public void addConcept(Concept c) {
        conceptSet.add(c);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
 
        if (!(o instanceof Type)) {
            return false;
        }
         
        return this.conceptSet.equals(((Type) o).conceptSet);
    }
}
