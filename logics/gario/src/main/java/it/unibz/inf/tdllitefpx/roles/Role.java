package it.unibz.inf.tdllitefpx.roles;

public abstract class Role {
	AtomicRole refersTo;
	Role inverseOf;
	
	public Role getInverse(){ return this.inverseOf; }
	
	public AtomicRole getRefersTo(){ return refersTo;}
	public boolean equals(Object obj){
		if(obj instanceof Role)
			return ((Role)obj).toString().equals(this.toString());
		else
			return false;
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
	
}
