package it.unibz.inf.qtl1.formulae;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.quantified.ExistentialFormula;
import it.unibz.inf.qtl1.formulae.quantified.QuantifiedFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.Always;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysPast;
import it.unibz.inf.qtl1.formulae.temporal.NextFuture;
import it.unibz.inf.qtl1.formulae.temporal.NextPast;
import it.unibz.inf.qtl1.formulae.temporal.Since;
import it.unibz.inf.qtl1.formulae.temporal.Sometime;
import it.unibz.inf.qtl1.formulae.temporal.SometimeFuture;
import it.unibz.inf.qtl1.formulae.temporal.SometimePast;
import it.unibz.inf.qtl1.formulae.temporal.TemporalFormula;
import it.unibz.inf.qtl1.formulae.temporal.Until;
import it.unibz.inf.qtl1.output.FormulaOutputFormat;
import it.unibz.inf.qtl1.output.OutputSymbolType;
import it.unibz.inf.qtl1.output.SymbolUndefinedException;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Term;
import it.unibz.inf.qtl1.terms.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Is the main class of the library. It is esigned to handle Temporal 
 * First Order Logic Formulas. Thus it is also able to represent any fragment of this logic,
 * in particular FOL, LTL and propositional logic.
 * 
 * @author Marco Gario
 *
 */
public abstract class Formula implements Cloneable{
	
	public abstract Set<Constant> getConstants();
	public abstract List<Formula> getSubFormulae();
	
	public abstract int getArity();
	public abstract boolean equals(Object obj);
	
	public static boolean cloneAtoms=true;
	public static boolean returnRealSubformulae=false;
	public static boolean removeDuplicates = true; //changed to true 20 april 2020
	public static boolean verifyTopBot = true;
	
	/* TODO: Implement negated version 
	   to have direct access to the negated version */
	
	public static Map<String, Boolean> getFlags(){
		Map<String, Boolean> map = new HashMap<String, Boolean>(5);
		map.put("cloneAtoms",cloneAtoms);
		map.put("returnRealSubformulae",returnRealSubformulae);
		map.put("removeDuplicates",removeDuplicates);
		map.put("verifyTopBot",verifyTopBot);
		return map;
	}
		
	/**
	 * Allows to modify the behaviour of the library to enhance performances
	 * cloneAtoms: Specify whether we want new instances of the atoms 
	 * when making a copy of a formula. For memory usage reasons this 
	 * is in general not what we want. Nevertheless when applying some operators 
	 * like substitution of a free variable, we might want to work on different instances 
	 * of the same atom. (Default true). 
	 * returnRealSubformulae: When calling getSubformulae we will receive a list of formulae.
	 * By default (false) this is a copy of the subformulae list, meaning that if we modify this
	 * list, we are not changing the real subformulae list. This option only has effect on 
	 * conjunctions and disjunctions.
	 * removeDuplicates: Usually conjunctions and disjunctions are treated as lists.
	 * Therefore they might contain duplicates. (Default false).
	 * verifyTopBot: Simple syntactic contradictions can be easily verified. In particular
	 * the case in which the same literal appears both positive and negative literal in 
	 * a formula. (Default true)
	 */
	public static boolean setFlags(Map<String, Boolean> flags){
		for(Entry<String, Boolean> e: flags.entrySet()){
			if(e.getKey().equals("cloneAtoms"))
				cloneAtoms=e.getValue();
			else if(e.getKey().equals("returnRealSubformulae"))
				returnRealSubformulae=e.getValue();
			else if(e.getKey().equals("removeDuplicates"))
				removeDuplicates=e.getValue();
			else if(e.getKey().equals("verifyTopBot"))
				verifyTopBot=e.getValue();
			else
				return false;
		}
		return true;
	}
	
	protected boolean isNNF=false;
	/**
	 * Returns the formula converted in NNF
	 * @return
	 */
	public abstract Formula toNNF();
	/**
	 * Converts the current formula in NNF
	 * @return
	 */
	public Formula convertToNNF(){
		System.err.println("convertToNNF unimplemented for "+this.getClass().toString());
		return null;
	}
	//TODO: TEST NNF
	/** 
	 * Negates the formula and returns the NNF
	 * @return
	 */
	public abstract Formula negateToNNF();
	
	
	public Formula toCNF(){
		if(this.isValidCNF()){
			return this;
		}else
			return new CNFFormula(this);
	}
	
	@Deprecated
	public void renameVar(Variable oldName,Variable newName){
		assert(false);
		for(Formula f:getSubFormulae())
			f.renameVar(oldName, newName);
		
	}
	
	/***
	 * Returns all variables appearing in the formula
	 * @return
	 */
	public Set<Variable> getVariables(){
		Set<Variable> l = new HashSet<Variable>();
		for(Formula f:getSubFormulae()){
			l.addAll(f.getVariables());
		}
		return l;
	}
	
	/***
	 * Returns all the Propositions (Atoms/0) appearing 
	 * in the formula
	 * @return
	 */
	public Set<Proposition> getPropositions(){
		Set<Proposition> set=null;
		
		if(this instanceof Proposition){
		    set = new HashSet<Proposition>();
			set.add((Proposition)this);
		}else{
			for(Formula f:getSubFormulae()){
				if(set==null)
					set=f.getPropositions();
				else
					set.addAll(f.getPropositions());
			}
		}
		return set;
	}
	
	/***
	 * Renames variables that have the same name.
	 * Note: Currently this function renames variables globally,
	 *       and only works with sentences 
	 * @throws Exception if the formula is not a sentence
	 */
	public void makeUniqueVar() throws Exception{
		makeUniqueVar(new HashSet<Variable>());
	}
	
	public void makeUniqueVar(Set<Variable> inUse) throws Exception{
		// TODO: 
		if(this.getFreeVars().size()!=0){
//			throw new Exception("This version only works with sentences.");
		}
		for(Formula f:getSubFormulae()){
			f.makeUniqueVar(inUse);
			inUse.addAll(f.getVariables());
		}
	}
	
	/***
	 *  Substitutes the free occurrences of var
	 *  with t.
	 *  
	 * @param var 
	 * @param t 
	 */
	public void substitute(Variable var, Term t){
		for(Formula f:getSubFormulae())
			f.substitute(var,t);
	}
	
	/***
	 * Returns the free variables appearing in
	 * the formula
	 * @return
	 */
	public Set<Variable> getFreeVars(){
		return getFreeVars(new HashSet<Variable>());
	}
	public Set<Variable> getFreeVars(Set<Variable> boundVars){
		Set<Variable> freeVar= new HashSet<Variable>();
		Set<Variable> newBound = boundVars;
		
		if(this instanceof QuantifiedFormula){
			newBound = new HashSet<Variable>();
			newBound.add(((QuantifiedFormula)this).getQuantifiedVar());
		}else if (this instanceof Atom)
			for(Variable v: ((Atom)this).getVariables()){
				if(!newBound.contains(v))
					freeVar.add(v);
			}
		
		for(Formula f: getSubFormulae()){
			freeVar.addAll(f.getFreeVars(newBound));
		}
		
		return freeVar;
		
	}
	
	/***
	 * Given a sentence, without existential quantifiers, a grounded copy is
	 * returned.
	 * 
	 * @return
	 * @throws ExistentialFormulaException If there is an Existential quantifier 
	 * 	(explicit or implicit)
	 * @throws Exception (see. {@link #makeUniqueVar} )
	 * 
	 */
	public Formula makeGround() throws ExistentialFormulaException,Exception{
		return makeGround(this.getConstants());
	}
	
	public Formula makeGround(Set<Constant> constants) throws ExistentialFormulaException,Exception{
		if(this.hasExistential())
			throw new ExistentialFormulaException();
		
		Formula base = (Formula) this.clone();
		base.makeUniqueVar();
		//Set<Constant> constants = base.getConstants();

		return base.makeGroundR(constants);
		
	}
	
	private Formula makeGroundR(Set<Constant> constants){

		for(Formula f: this.getSubFormulae())
			replaceSubFormula(f,f.makeGroundR(constants));
		
		if(this instanceof UniversalFormula){
			ConjunctiveFormula groundF= new ConjunctiveFormula();
			
			for(Constant c :constants){
				Formula copy = (Formula) this.getSubFormulae().get(0).clone();
				copy.substitute(((UniversalFormula)this).getQuantifiedVar(), c);
				groundF.addConjunct(copy);
			}
			return groundF;
		}
		return this;
		
	}
	/***
	 * Generic function to substitute a subformula in a formula. This function
	 * applies only at top most level, and doesn't replace sub-subformulas.
	 * (ie. given F=" a & (b | (a & c))", F.replaceSubFormula("a","d") returns "d & (b | (a & c))")  
	 * @param former
	 * @param current
	 */
	public abstract void replaceSubFormula(Formula former, Formula current); 
	
	
	/***
	 * Removes all universal quantifiers appearing in the formula and returns the set of variables
	 * that become free. It is in general used in conjunction with makeUniqueVar.
	 */
	public abstract Set<Variable> removeUniversals();
	
	/***
	 * Returns whether the formula has Existential quantifiers
	 * either explicit or implicit.
	 * @return
	 */
	public boolean hasExistential(){
		if(this instanceof ExistentialFormula)
			return true;
		else
			for(Formula t: getSubFormulae())
				if(this instanceof NegatedFormula)
					return t.hasUniversal();
				else if(t.hasExistential())
					return true;
		
		return false;
	}
	
	/***
	 * Returns whether the formula has Universal quantifiers
	 * either explicit or implicit.
	 * @return
	 */
	public boolean hasUniversal() {
		if(this instanceof UniversalFormula)
			return true;
		else
			for(Formula t: getSubFormulae())
				if(this instanceof NegatedFormula)
					return t.hasExistential();
				else if(t.hasUniversal())
					return true;
		
		return false;
	}
	
	/***
 	 * Returns a formatted version of the formula.
       Note: This method is overridden by Atom, in which toString is used
				this is because formatting of atoms/functions/variables is not that common
				and in case it was necessary, it would probably require some complicated rule.
				Nevertheless an useful extension could be upper-case Variables and lower-case
				constants. (Datalog/Prolog style)
	 * @param format An instance of {@link FormulaOutputFormat}
	 * @return 
	 * @throws SymbolUndefinedException If in format there's no definition 
	 * 		for some symbol appearing in the formula
	 */
	public String getFormattedFormula(FormulaOutputFormat format) throws SymbolUndefinedException{
		return getSBFormattedFormula(format).toString();
	}
	public StringBuilder getSBFormattedFormula(FormulaOutputFormat format) throws SymbolUndefinedException{
		StringBuilder out = new StringBuilder();
		if(format.hasParenthesis(this))
			out.append("(");
		
		Iterator<Formula> fIt = getSubFormulae().iterator();
		Formula f;
		while(fIt.hasNext()){
			f=fIt.next();
			if(format.getSymbolPosition(this)==OutputSymbolType.PREFIX){
				if(this instanceof QuantifiedFormula){
					out.append(format.getSymbol(this).replaceFirst("_x_",
							((QuantifiedFormula)this).getQuantifiedVar().toString() )
							);
				}else
					out.append(format.getSymbol(this));
				
			}
			
			
			out.append(f.getSBFormattedFormula(format));
			
			if(format.getSymbolPosition(this)==OutputSymbolType.INFIX && fIt.hasNext()){
				out.append(format.getSymbol(this));
				if(this instanceof QuantifiedFormula)
					out.append(((QuantifiedFormula)this).getQuantifiedVar().toString());
			}
				
			if(format.getSymbolPosition(this)==OutputSymbolType.POSTFIX){
				out.append(format.getSymbol(this));
				if(this instanceof QuantifiedFormula)
					out.append(((QuantifiedFormula)this).getQuantifiedVar().toString());
			}
		}
		
		if(format.hasParenthesis(this))
			out.append(")");
		
		return out;
	}
	/*** 
	 * Returns a copy of the formula. All the subformulas are cloned.
	 */

	public abstract Object clone();
	
	/***
	 * Substitutes atoms with a proposition.
	 * @throws NotGroundException: This operation makes sense only on formulas
	 * without variables.
	 */
	public abstract void atomsToPropositions() throws NotGroundException;
	
	/***
	 * Build the propositional counter-part of the formula, in general LTL,
	 * by building the grounding and then converting atoms to propositions.
	 * @return
	 * @throws Exception 
	 */
	public Formula makePropositional(Set<Constant> constants) throws Exception{
		//Formula propF = this.makeGround(Set<Constant> constants); ancien
		Formula propF = this.makeGround(constants);
		//System.out.println("propF1: "+propF);
		propF.atomsToPropositions();
		//System.out.println("propF2: "+propF);
		return propF;
	}
	
	public Formula makePropositional() throws Exception{
		Formula propF = this.makeGround();
		//System.out.println("propF1: "+propF);
		propF.atomsToPropositions();
		//System.out.println("propF2: "+propF);
		return propF;
	}
	/**
	 * Verifies whether the formula belongs to the propositional logic 
	 * fragment. 
	 * @return
	 */
	public boolean isPropositional(){
		if(	this instanceof Proposition ||
			this instanceof ConjunctiveFormula ||
			this instanceof DisjunctiveFormula ||
			this instanceof NegatedFormula ||
			this instanceof ImplicationFormula ||
			this instanceof BimplicationFormula){
			
			for(Formula s: this.getSubFormulae()){
				if(!(s.isPropositional()))
					return false;
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * This function returns true if the formula is trivially unsat.
	 * Returns false if the value is unknown. 
	 * Note that !isBot != isTop in general
	 * @return
	 */
	public boolean isBot(){
		//System.err.println("Unimplemented isBot");
		return false;
	}
	public boolean isTop(){
		//System.err.println("Unimplemented isTop");
		return false;
	}
	
	/***
	 * Returns the first _real_ formula in the Formula.
	 * ie, (and (or (b))) = b 
	 * @return
	 */
	public Formula removeEncapsulation(){
		if (this instanceof ConjunctiveFormula || this instanceof DisjunctiveFormula)
			if(this.getSubFormulae().size()==1)
				return this.getSubFormulae().get(0).removeEncapsulation();
		
		return this;
	}
	
	public boolean isValidCNF(){
		boolean oldReal = Formula.returnRealSubformulae;
		Formula.returnRealSubformulae=true;
		
		boolean ret=isValidCNF_();
		
		Formula.returnRealSubformulae=oldReal;
		
		return ret;
		
	}
	private boolean isValidCNF_(){
		if(this instanceof ConjunctiveFormula){
			for(Formula f: this.getSubFormulae()){
				if(!f.isValidCNFClause()){
					return false;
				}
			}
			return true;
		}else{
			return this.isValidCNFClause();
		}
		
	}
	
	
	public boolean isValidCNFClause(){
		if(this instanceof Atom)
			return true;
		else if(this instanceof NegatedFormula){
			if(((NegatedFormula)this).getRefersTo() instanceof Atom)
				return true;
		}else if(this instanceof DisjunctiveFormula){
			for(Formula df: this.getSubFormulae()){
				boolean valid=false;
				if(df instanceof Atom)
					valid=true;
				else if(df instanceof NegatedFormula){
					if(((NegatedFormula)df).getRefersTo() instanceof Atom)
						valid=true;
				}
				if(!valid)
					return false;
			}
			return true;
		}
		
		return false;
	}
	
	public boolean isLiteral(){
		if(this instanceof Atom)
			return true;
		else if((this instanceof NegatedFormula) &&
				((NegatedFormula)this).refersTo instanceof Atom)
			return true;
		else
			return false;
			
	}
	
	
	public Formula distribute(){
		System.err.println("distribute: Unimplemented for type:"+this.getClass());
		return null;
	}
	
	public Formula makeTemporalStrict(){
		/* 
		 * We replace the operators in order to make the formula strict,
		 * ie. it doesn't take into account the current moment of time.
		 * 
		 * Box -> Next Box
		 * Diamond -> Next Diamond
		 * Until -> Next Until
		 * 
		 */
		
		Formula f = null;
		
		for(Formula s: this.getSubFormulae()){
			Formula strict = s.makeTemporalStrict();
			if(strict != s)
				this.replaceSubFormula(s, strict);
		}
		
		if(this instanceof TemporalFormula){
			if(this instanceof AlwaysFuture ||	
				this instanceof SometimeFuture || 
				this instanceof Until){
					
				f =  new NextFuture(this);
				// TODO: Replace subformulas
					
			
			}else if(this instanceof AlwaysPast ||
					this instanceof SometimePast ||
					this instanceof Since){
			
				f = new NextPast(this);
			}else if(this instanceof NextPast ||
					this instanceof NextFuture ||
					this instanceof Always ||
					this instanceof Sometime){
				f = this;
			}/*else if(this instanceof Always) {
				f = new AlwaysFuture(new AlwaysPast(this));
			}*/else{
				System.err.println("makeTemporalStrict: undefined for "+this.getClass().getName());
			}
		}else{
			f = this;
		}
		
		return f;
	}

	public boolean isOneVariable(){
		//TODO: Is this enough?
		if(this instanceof Atom){
			Atom at = (Atom) this;
			if(at.getVariables().size()>1)
				return false;
			else
				return true;
		}else{
			for(Formula s : this.getSubFormulae()){
				if(!s.isOneVariable())
					return false;
			}
			return true;
		}
		
	}
	
	public boolean isSentence(){
		if(this.getFreeVars().size()!=0)
			return false;
		else
			return true;
	}
	
}