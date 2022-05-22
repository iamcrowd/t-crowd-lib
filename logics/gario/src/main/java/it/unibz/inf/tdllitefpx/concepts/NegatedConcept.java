package it.unibz.inf.tdllitefpx.concepts;

import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.Set;

import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

public class NegatedConcept extends Concept {
	Concept refersTo;
	
	public NegatedConcept(Concept refersTo) {
		this.refersTo = refersTo;
	}

	public Concept getRefersTo() {
		return refersTo;
	}

	@Override
	public Set<Role> getRoles() {
		return refersTo.getRoles();
	}
	
	public String toString(){ return  "!"+refersTo.toString();	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return fmt.getSymbol(this) + refersTo.toString(fmt);
	}
	
	@Override
	public Set<Concept> getBasicConcepts() {
		return refersTo.getBasicConcepts();
	}

	@Override
	public int hashCode() {
		return (refersTo.hashCode() + 3079);
	}
}
