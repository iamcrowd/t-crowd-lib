package it.unibz.inf.tdllitefpx.roles;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

public abstract class AtomicRole implements FormattableObj{
	String name;
	
	public AtomicRole(String name){
		this.name = name;
	}
	
	public int hashCode(){
		return this.name.hashCode();
	}
	
	public boolean equals(Object o){
		if(o instanceof AtomicRole){
			if(((AtomicRole)o).name == this.name)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {		
		return this.name; 
		
	}
}
