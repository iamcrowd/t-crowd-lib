package it.unibz.inf.tdllitefpx.concepts;

import java.util.HashSet;
import java.util.Set;

import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.tdllitefpx.roles.*;

public class QuantifiedRole extends BasicConcept {
	Integer q;
	Role role;
	
	public QuantifiedRole(Role role,int value){
		this.role=role;
		this.q=value;
	}
	
	public String toString(){
		return ">= "+q+"."+role.toString();
	}
	
	public Role getRole(){return role;}
	public int getQ(){return q;}
	
	public Set<Role> getRoles(){
		HashSet<Role> s = new HashSet<Role>();
		s.add(role);
		s.add(role.getInverse());
		return s;
	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return fmt.getSymbol(this) + q+"."+role.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = prime * hash + ((role == null) ? 0 : role.hashCode());
		hash = prime * hash + ((q == null) ? 0 : q.hashCode());

		return hash;
	}
}
