package it.unibz.inf.tdllitefpx.roles;

import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

public class NegativeRole extends Role{
	
	public NegativeRole(AtomicRole refersTo){
		this.refersTo = refersTo;
		this.inverseOf = new PositiveRole(refersTo,this); 
	}
	
	public NegativeRole(AtomicRole refersTo,PositiveRole inverseOf){
		this.refersTo = refersTo;
		this.inverseOf = inverseOf;
	}
	
	public String toString(){return refersTo.name+"Inv";}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
