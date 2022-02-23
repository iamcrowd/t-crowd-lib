package it.unibz.inf.tdllitefpx.abox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.roles.Role;

public class ABoxRoleAssertion implements FormattableObj{
	Role ro;
	String x;
	String y;
	Integer t;
	
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
		this.ro = ro;
		this.x = valuex;
		this.y = valuey;
		this.t = i;
	}
	
	public  Set<Constant> getConstant(){
		Set<Constant> consts = new HashSet<Constant>();
		consts.add(new Constant(x));
		consts.add(new Constant(y));
		return consts;
	}

	public Role getRole() {
		return this.ro;
	}
	
	public Constant getx(){
		return new Constant(this.x);
	}

	public Constant gety(){
		return new Constant(this.y);
	}
	
	public Integer getStamp() {
		return this.t;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ro == null) ? 0 : ro.hashCode());
		result = prime * result + ((t == null) ? 0 : t.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
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
		if (ro == null) {
			if (other.ro != null)
				return false;
		} else if (!ro.equals(other.ro))
			return false;
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	public boolean equals2(Object obj){
		if(obj instanceof ABoxConceptAssertion){
			ABoxRoleAssertion r = (ABoxRoleAssertion) obj;
			return (r.ro.toString() == (this.ro.toString()) && r.x == (this.x) && r.y == (this.y) && r.t == (this.t) );
		} else
			return false;
			
	}

	public int hashCode2(){
		return this.ro.hashCode();
	}
	
//	@Override
//	public String toString(OutputFormat fmt) throws SymbolUndefinedException {	
//		return getRole().getRefersTo().toString(fmt) + "(" + 
//		getx() + "," + gety() + ")\\";
//	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return this.toString(); 
	}

	public String toString() {
		String c = "(" + this.ro.toString() + "," + this.x + "," + this.y + "," +this.t +"), ";
		return c; 
	
	}

}
