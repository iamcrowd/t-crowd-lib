package it.unibz.inf.tdllitefpx.concepts;

import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.Set;

import org.gario.code.output.FormattableObj;

public abstract class Concept implements FormattableObj{
	public abstract Set<Role> getRoles();

	public boolean equals(Object obj){
		if(obj instanceof Concept){
			return ((Concept)obj).toString().equals(this.toString());
		}else
			return false;
			
	}
	
	public abstract Set<Concept> getBasicConcepts();
	
	public int hashCode(){
		return this.toString().hashCode();
	}
}
