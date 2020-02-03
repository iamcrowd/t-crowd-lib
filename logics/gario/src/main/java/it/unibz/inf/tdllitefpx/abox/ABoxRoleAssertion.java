package it.unibz.inf.tdllitefpx.abox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.roles.AtomicRole;

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
	
//	@Override
//	public String toString(OutputFormat fmt) throws SymbolUndefinedException {	
//		return getRole().getRefersTo().toString(fmt) + "(" + 
//		getx() + "," + gety() + ")\\";
//	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return this.toString(); 
	}

}
