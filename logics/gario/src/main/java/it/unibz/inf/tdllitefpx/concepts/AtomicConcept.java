package it.unibz.inf.tdllitefpx.concepts;

import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.HashSet;
import java.util.Set;

import org.gario.code.output.OutputFormat;

public class AtomicConcept extends BasicConcept {
	String name;
	int hash;
	
	public AtomicConcept(String name){
		this.name = name;
		hash = name.hashCode();
	}
	
	public String toString(){return name;}
	
	public Set<Role> getRoles(){return new HashSet<Role>();}

	@Override
	public String toString(OutputFormat fmt) {
		return toString();
	}

	@Override
	public int hashCode() {
		return hash;
	}
}
