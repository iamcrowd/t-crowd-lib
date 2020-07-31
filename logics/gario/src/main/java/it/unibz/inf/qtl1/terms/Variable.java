package it.unibz.inf.qtl1.terms;

public class Variable extends Term {
	//boolean bound=false;
	
	//public void setBound(){ bound=true;}
	//public boolean isBound(){ return bound;}
	
	public Variable(String name) {
		super(name);
	}
	
	public Object clone(){
		Variable v=new Variable(name);
		return v;
	}
	
	
	
}
