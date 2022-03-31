package it.unibz.inf.tdllitefpx.tbox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.tdllitefpx.roles.Role;

public class RoleInclusionAssertion implements FormattableObj{
	Role lhs,rhs;
	
	public RoleInclusionAssertion(Role lhs, Role rhs){
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Role getLHS() { return lhs;}
	public Role getRHS() { return rhs;}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {		
		return getLHS().toString(fmt) +
		fmt.getSymbol(this)+
		getRHS().toString(fmt); 
		
	}

}
