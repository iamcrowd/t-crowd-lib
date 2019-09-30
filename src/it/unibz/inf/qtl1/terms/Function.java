package it.unibz.inf.qtl1.terms;

import it.unibz.inf.qtl1.NotGroundException;

import java.util.HashSet;
import java.util.Set;


public class Function extends Term {
	Term[] terms;
	int arity=0;
	
	public Function(String name,int arity) {
		super(name);
		this.arity=arity;
		terms = new Term[arity];
		for(int i=0;i<arity;i++)
			setArg(i,new Variable("_"));
	}
	
	public void setArg(int index, Term value){
		if (index<arity)
			terms[index]=value;
	}
	
	public Term[] getArgs(){
		return terms.clone();
	}
	
	@Deprecated
	public void renameVar(Variable oldName,Variable newName){
		assert(false);
		for(int i=0;i<arity;i++){
			if(terms[i].equals(oldName))
				terms[i]=newName;
		}
	}
	
	public boolean equals(Object obj){
		
		if(super.equals(obj)){
			Function o = (Function) obj;
			if(o.arity!=this.arity)
				return false;
				
			for(int i=0;i<arity;i++)
				if(!o.getArgs()[i].equals(terms[i]))
					return false;
			return true;
		}
		return false;
	}
	
	public Object clone(){
		Function n = new Function(this.name, arity);
		for(int i=0;i<arity;i++)
			n.setArg(i, terms[i]);
		
		return n;
			
		
	}
	
	public String toString(){
		String s="";
		s+=name;
		if(arity!=0){
			s+="(";
			for(Term t: terms)
				s+=t+",";
			s=s.substring(0, s.length()-1);
			s+=")";
		}
		return s;
	}

	public Set<Variable> getVariables() {
		Set<Variable> l = new HashSet<Variable>();
		for(Term t:terms)
			if(t instanceof Variable)
				l.add((Variable)t);
			else if(t instanceof Function)
				l.addAll(((Function)t).getVariables());
		return l;
	}
	
	public void substitute(Variable var, Term t) {
		for(int i=0;i<arity;i++)
			if(terms[i].equals(var))
				setArg(i, t);
			else if (terms[i] instanceof Function)
				((Function)terms[i]).substitute(var,t);
	}

	public Term functionToConstant() throws NotGroundException{
		if(getVariables().size()!=0)
			throw new NotGroundException();
		String name=this.name+"--";
		for(Term t : terms){
			Function f = (Function) t;
			if(f.arity==0)
				name+=f.toString()+"-";
			else
				name+=f.functionToConstant().toString()+"-";
		}
		Constant n = new Constant(name);
		return n;
	}
	
}
