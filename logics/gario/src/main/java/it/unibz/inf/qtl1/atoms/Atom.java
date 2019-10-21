package it.unibz.inf.qtl1.atoms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.output.FormulaOutputFormat;
import it.unibz.inf.qtl1.output.SymbolUndefinedException;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Function;
import it.unibz.inf.qtl1.terms.Term;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;

/**
 * Provides a simple representation of a FOL atom.
 * This class extends {@link Formula} and is a superclass for 
 * {@link Proposition}
 * @author Marco Gario
 *
 */
public class Atom extends Formula{
	Term[] terms;
	String name;
	int arity;
	
	/**
	 * Builds an unary atom
	 * @param name
	 * @param t
	 */
	public Atom(String name,Term t){
		this(name,1);
		this.setArg(0, t);
	}

	/** 
	 * Builds a binary atom
	 */
	public Atom(String name,Term t1,Term t2){
		this(name,2);
		this.setArg(0, t1);
		this.setArg(1, t2);
	}
	
	/** 
	 * Builds an atom of the given arity.
	 * The arguments of the atom will be set to
	 * "_", the anonymous element.
	 * They should be later modified with setArg
	 *  
	 * @param name
	 * @param arity
	 */
	public Atom(String name,int arity){
		super();
		this.name=name;
		this.arity=arity;
		terms = new Term[arity];
		for(int i=0;i<arity;i++)
			setArg(i,new Variable("_"));
	}
	
	/**
	 * Builds a copy of the atom original
	 * @param original
	 */
	public Atom(Atom original){
		super();
		this.name=original.name;
		this.arity=original.arity;
		this.terms = original.terms.clone();
	}
	public List<Formula> getSubFormulae(){
		List<Formula> l=  new ArrayList<Formula>();
		return l;
	}
	public boolean equals(Object obj){
		if(obj instanceof Atom){
			Atom o = (Atom) obj;
			if(o.name.equals(this.name)){
				if(o.arity!=this.arity)
					return false;
			
				for(int i=0;i<arity;i++)
					if(!o.terms[i].equals(terms[i]))
						return false;
				return true;
			}
		}
		return false;
	}
	
	public String getName(){ return name; }
	//TODO: Find a better name to avoid confusion with formula's arity 
	public int getArity(){ return arity; }
	
	/**
	 * Specifies a term for an atom. eg. R(x,y) can be obtained
	 * from R(y,y) by calling setArg(0,x). Note that index goes from
	 * 0 to arity-1.
	 * 
	 * @param index
	 * @param value
	 */
	public void setArg(int index, Term value){
		if (index<arity)
			terms[index]=value;
	}
	
	public Term getArg(int index){
		if (index<arity)
			return terms[index];
		return null;
	}
	
	public Set<Constant> getConstants(){
		HashSet<Constant> set=new HashSet<Constant>();
		for(Term t: terms)
			if (t instanceof Constant)
				set.add((Constant) t);
		return set;
	}
	
	public Set<Variable> getVariables(){
		Set<Variable> l = new HashSet<Variable>();
		for(Term t:terms)
			if(t instanceof Variable)
				l.add((Variable)t);
			else if(t instanceof Function)
				l.addAll(((Function)t).getVariables());
		return l;
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
	
	public Formula buildGrounding(){
		return null;
	}
	
	public Object clone(){
		if(Formula.cloneAtoms)
			return new Atom(this);
		else
			return this;
	}

	@Deprecated
	public void renameVar(Variable oldName,Variable newName){
		assert(false);
		for(int i=0;i<arity;i++){
			if(terms[i].equals(oldName))
				terms[i]=newName;
			else if(terms[i] instanceof Function)
				((Function)terms[i]).renameVar(oldName, newName);
		}
		
	}

	/**
	 * Substitutes all the occurrences of var inside the atom
	 * for the given term t.
	 * Note that this substitution is applied recursively to functions
	 * too: the term f(x) will be influenced by a substitution on x.
	 */
	public void substitute(Variable var, Term t) {
		for(int i=0;i<arity;i++)
			if(terms[i].equals(var))
				setArg(i, t);
			else if (terms[i] instanceof Function)
				((Function)terms[i]).substitute(var,t);
	}
	
	public Set<Variable> removeUniversals(){
		return new HashSet<Variable>();
	}

	public String getFormattedFormula(FormulaOutputFormat format) throws SymbolUndefinedException{
		return this.toString();
	}
	
	@Override
	public StringBuilder getSBFormattedFormula(FormulaOutputFormat format) throws SymbolUndefinedException{
		return new StringBuilder(this.toString());
	}
	
	/**
	 * If the atom contains no variables, this method returns a propositional
	 * variable that is unique give the atom name and arguments.
	 * Eg. R(c1,c2) -> R_c1_c2
	 * @return
	 * @throws NotGroundException
	 */
	public Proposition atomToProposition() throws NotGroundException{
		if(getVariables().size()!=0)
			throw new NotGroundException();
		String name=this.name+"_";
		
		if(arity!=0)
			name+="{";
		
		for(Term t : terms){
			Function f = (Function) t;
			if(f instanceof Constant)
				name+=f.toString()+"-";
			else
				name+=f.functionToConstant().toString()+"-";
		}
		name=name.substring(0,name.length()-1);
		
		if(arity!=0)
			name+="}";
		
		Proposition n = new Proposition(name);
		return n;
	}
	@Override
	public void atomsToPropositions() throws NotGroundException {
		
	}
	
	public int hashCode() {
		return (name+"/"+arity).hashCode();
	}
	@Override
	public void replaceSubFormula(Formula f, Formula makeGroundR) {
		
	}

	// An atom cannot be true or false, unless it is bottom or top. 
	@Override
	public boolean isBot() {
		if(this.equals(new Bot()))
			return true;
		else
			return false;
	}
	@Override
	public boolean isTop() {
		if(this.equals(new Top()))
			return true;
		else
			return false;
	}
	@Override
	public Formula negateToNNF() {
		return new NegatedFormula(this);
	}
	@Override
	public Formula toNNF() {
		return this;
	}
	
	
}
