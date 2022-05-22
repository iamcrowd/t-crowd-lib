package it.unibz.inf.tdllitefpx.roles;

import org.gario.code.output.FormattableObj;

public abstract class Role implements FormattableObj {
	AtomicRole refersTo;
	Role inverseOf;
	
	public Role getInverse(){ return this.inverseOf; }
	
	public AtomicRole getRefersTo(){ 
		return refersTo;
	}

/*	public boolean equals(Object obj){
		if(obj instanceof Role)
			return ((Role)obj).toString().equals(this.toString());
		else
			return false;
	}
*/
	public boolean equals(Object obj){
		if(obj instanceof Role)
			return ((Role)obj).refersTo.name.equals(this.refersTo.name);
		else
			return false;
	}
	
}
