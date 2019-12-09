package it.unibz.inf.tdllitefpx.abox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import java.util.HashSet;
import java.util.Set;

import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.roles.Role;

public class ABoxRoleAssertion implements FormattableObj{
	Role ro;
	String x;
	String y;
	
	public ABoxRoleAssertion (Role ro, String valuex, String valuey){
		// R(x,y)
		this.ro = ro;
		this.x = valuex;
		this.y = valuey;
	}
	
	public  Set<Constant> getConstant(){
		Set<Constant> consts = new HashSet<Constant>();
		consts.add(new Constant(x));
		consts.add(new Constant(y));
		return consts;
	}

	
	public Constant getx(){
		return new Constant(this.x);
	}

	public Constant gety(){
		return new Constant(this.y);
	}
	
	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {	
		return this.toString(); 
	}
}
