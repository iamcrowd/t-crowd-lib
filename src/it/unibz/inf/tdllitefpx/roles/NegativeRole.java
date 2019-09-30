package it.unibz.inf.tdllitefpx.roles;

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
	
}
