package it.unibz.inf.tdllitefpx.roles;

public class PositiveRole extends Role {
	
	public PositiveRole(AtomicRole refersTo, NegativeRole inverseOf) {
		this.refersTo = refersTo;
		this.inverseOf = inverseOf;
	}
	
	public PositiveRole(AtomicRole refersTo) {
		this.refersTo = refersTo;
		this.inverseOf = new NegativeRole(refersTo,this);
	}
	
	public String toString(){return refersTo.name;}
}
