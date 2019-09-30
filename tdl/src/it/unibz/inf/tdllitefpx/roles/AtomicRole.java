package it.unibz.inf.tdllitefpx.roles;

public abstract class AtomicRole {
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
}
