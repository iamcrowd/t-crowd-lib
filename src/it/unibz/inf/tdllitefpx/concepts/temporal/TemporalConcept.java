package it.unibz.inf.tdllitefpx.concepts.temporal;

import java.util.Set;

import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.roles.Role;

public abstract class TemporalConcept extends Concept{
	Concept refersTo;
	
	public TemporalConcept(Concept refersTo){
		this.refersTo = refersTo;
	}
	
	public Concept getRefersTo(){return refersTo;}
	
	@Override
	public Set<Role> getRoles() {
		return refersTo.getRoles();
	}
	
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return fmt.getSymbol(this) + refersTo.toString(fmt);
	}
	
	@Override
	public Set<Concept> getBasicConcepts() {
		return refersTo.getBasicConcepts();
	}
}
