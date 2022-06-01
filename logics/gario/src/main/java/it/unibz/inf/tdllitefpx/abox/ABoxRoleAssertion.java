package it.unibz.inf.tdllitefpx.abox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.roles.Role;

public class ABoxRoleAssertion implements FormattableObj{
	Role role;
	Individual source;
	Individual target;
	Integer timestamp;

	int hash = 0;

	/**
	 * An ABox role is a Role instance, x and y String values as constants and an i Integer
	 * as timestamp.
	 * 
	 * @param ro a Concept
	 * @param valuex a String
	 * @param valuey a String
	 * @param i an Integer
	 */
	public ABoxRoleAssertion (Role ro, String valuex, String valuey, Integer i){
		// R(x,y)^{i}
		this.role = ro;
		this.source = new Individual(valuex);
		this.target = new Individual(valuey);
		this.timestamp = i;
	}
	
	public  Set<Constant> getConstant(){
		Set<Constant> consts = new HashSet<Constant>();
		consts.add(new Constant(source.toString()));
		consts.add(new Constant(target.toString()));
		return consts;
	}

	public Role getRole() {
		return this.role;
	}
	
	public Constant getx(){
		return new Constant(this.source.toString());
	}

	public Constant gety(){
		return new Constant(this.target.toString());
	}
	
	public Integer getStamp() {
		return this.timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = prime * hash + ((role == null) ? 0 : role.hashCode());
		hash = prime * hash + ((timestamp == null) ? 0 : timestamp.hashCode());
		hash = prime * hash + ((source == null) ? 0 : source.hashCode());
		hash = prime * hash + ((target == null) ? 0 : target.hashCode());

		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ABoxRoleAssertion other = (ABoxRoleAssertion) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			return other.target == null;
		} else return target.equals(other.target);
	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return this.toString(); 
	}

	public String toString() {
		return "(" + this.role.toString() + "," + this.source + "," + this.target + "," + this.timestamp +"), ";
	}

}
