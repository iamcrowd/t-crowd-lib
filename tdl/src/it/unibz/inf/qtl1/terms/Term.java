package it.unibz.inf.qtl1.terms;

public abstract class Term implements Cloneable{
	String name;
	
	public Term(String name){
		this.name=name;
	}
	
	public String toString(){
		return name;
	}
	public String getName(){
		return name;
	}
	public abstract Object clone();
	
	public boolean equals(Object obj){
		if(this.getClass().equals(obj.getClass()))
			if (((Term)obj).name.equals(this.name))
				return true;
		return false;
	}
	
	

}
