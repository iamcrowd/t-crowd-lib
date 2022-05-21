package it.unibz.inf.tdllitefpx.roles;

import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

public class PositiveRole extends Role {
	
	public PositiveRole(AtomicRole refersTo, NegativeRole inverseOf) {
		this.refersTo = refersTo;
		this.inverseOf = inverseOf;
	}
	
	public PositiveRole(AtomicRole refersTo) {
		System.out.println("#");
		this.refersTo = refersTo;
		this.inverseOf = new NegativeRole(refersTo,this);
	}
	
	public String toString(){
		return refersTo.name;
	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		// TODO Auto-generated method stub
		return null;
	}
}
