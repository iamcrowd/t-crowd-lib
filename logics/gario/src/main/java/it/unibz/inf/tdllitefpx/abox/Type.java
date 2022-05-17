package it.unibz.inf.tdllitefpx.abox;

import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class Type {
    Set<Concept> value = new HashSet<Concept>();

    public int hashCode() {
        return value.hashCode();
    }

    public void addConcept(Concept c) {
        value.add(c);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
 
        if (!(o instanceof Type)) {
            return false;
        }
         
        return this.value.equals(((Type) o).value);
    }
}
