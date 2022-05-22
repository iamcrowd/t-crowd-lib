package it.unibz.inf.tdllitefpx.concepts;

import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.HashSet;
import java.util.Set;

import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

public class BottomConcept extends BasicConcept {
	public String toString(){return "_|_";}
	
	public Set<Role> getRoles(){return new HashSet<Role>();}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return fmt.getSymbol(this);
	}

	@Override
	public int hashCode() {
		return 1;
	}

}
