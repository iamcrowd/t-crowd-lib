package it.unibz.inf.qtl1.output;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.*;
import it.unibz.inf.qtl1.formulae.temporal.*;
import it.unibz.inf.qtl1.formulae.quantified.*;

import java.util.HashMap;

/***
 *  Defines how to represent the different operators.
 *  Provides a default implementation in NuSMV-like style.
 *  
 * @author Marco Gario
 *
 */
public class FormulaOutputFormat {
	HashMap<String,String> symbol;
	HashMap<String,Boolean> parenthesis;
	HashMap<String,OutputSymbolType> symbolPosition;
	
	public FormulaOutputFormat(){
		symbol=new HashMap<String, String>();
		parenthesis=new HashMap<String, Boolean>();
		symbolPosition=new HashMap<String, OutputSymbolType>();
	}
	
	/*** 
	 * Returns the symbol associated to the class the obj belongs to
	 * (ie obj.getClass())
	 * 
	 * @param obj
	 * @return 
	 * @throws SymbolUndefinedException
	 */
	public String getSymbol(Object obj) throws SymbolUndefinedException{
		if(symbol.containsKey(obj.getClass().toString())){
			return symbol.get(obj.getClass().toString());
		}else
			throw new SymbolUndefinedException(obj.getClass().toString());
	}
	
	/***
	 * Returns wheter the symbol is Prefix, Infix or Postfix.
	 * Defaults to Infix
	 * @param obj
	 * @return
	 * @throws SymbolUndefinedException
	 */
	public OutputSymbolType getSymbolPosition(Object obj) throws SymbolUndefinedException{
		if(symbolPosition.containsKey(obj.getClass().toString())){
			return symbolPosition.get(obj.getClass().toString());
		}else
			return OutputSymbolType.INFIX;
	}
	
	/***
	 * Returns whether the symbol should be surrounded by parenthesis.
	 * Defaults to false 
	 * @param obj
	 * @return
	 */
	public boolean hasParenthesis(Object obj){
		if(parenthesis.containsKey(obj.getClass().toString()))
			return ((Boolean)parenthesis.get(obj.getClass().toString())).booleanValue();
		else
			return false;
	}
	
	public void setSymbol(String className,String symbol){
		this.symbol.put(className, symbol);
	}
	public void setParenthesis(String className,boolean value){
		this.parenthesis.put(className,new Boolean(value));
	}
	/***
	 * Specify the type of notation fot a given type: Infix,Prefix or Postfix
	 * @param className
	 * @param type
	 */
	public void setSymbolType(String className,OutputSymbolType type){
		this.symbolPosition.put(className,type);
	}
	/***
	 * Defines a base interpretation of symbols for TFOL:
	 * and, or, not, implies => &, |, !, ->
	 * Existential and Universal quantifiers are expressed as Ex. and Ax.
	 * AlwaysFuture, AlwaysPast Always => G, H, H G
	 * SometimeFuture, SometimePast, Sometime => F, O, O F
	 * NextFuture, NextPast, Until, Since  => X, Y, U, S
	 * 
	 * All symbols are in Prefix notation except for Until and Since.
	 * 
	 * Only the following are surrounded by parenthesis: 
	 *  And, Or, Implication, Until and Since
	 *  
	 *  Note: This interpretation is derived from NuSMV notation.
	 */
	public void setDefault(){
		// Simple
		setSymbol(ConjunctiveFormula.class.toString(), " & ");
		setSymbol(DisjunctiveFormula.class.toString(), " | ");
		setSymbol(NegatedFormula.class.toString(), " !");
		setSymbol(ImplicationFormula.class.toString(), " -> ");
		setSymbol(BimplicationFormula.class.toString(), " <-> ");
		
		
		setParenthesis(ConjunctiveFormula.class.toString(), true);
		setParenthesis(DisjunctiveFormula.class.toString(), true);
		setParenthesis(NegatedFormula.class.toString(), false);
		setParenthesis(ImplicationFormula.class.toString(), true);
		setParenthesis(BimplicationFormula.class.toString(), true);

		setSymbolType(NegatedFormula.class.toString(), OutputSymbolType.PREFIX);
		// Quantified
		setSymbol(ExistentialFormula.class.toString(), " E");
		setSymbol(UniversalFormula.class.toString(), " A");
		
		setParenthesis(ExistentialFormula.class.toString(), false);
		setParenthesis(UniversalFormula.class.toString(), false);
		
		setSymbolType(ExistentialFormula.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(UniversalFormula.class.toString(), OutputSymbolType.PREFIX);
		// Temporal
		
		setSymbol(Always.class.toString()," G ");
		setSymbol(AlwaysFuture.class.toString(), " G ");
		setSymbol(AlwaysPast.class.toString(), " H ");
		setSymbol(Sometime.class.toString(), " O F ");
		setSymbol(SometimeFuture.class.toString(), " F ");
		setSymbol(SometimePast.class.toString(), " O ");
		setSymbol(NextFuture.class.toString(), " X ");
		setSymbol(NextPast.class.toString(), " Y ");
		setSymbol(Since.class.toString(), " S ");
		setSymbol(Until.class.toString(), " U ");
		
		setParenthesis(Until.class.toString(), true);
		setParenthesis(Since.class.toString(), true);
		
		setSymbolType(Always.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(AlwaysPast.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(AlwaysFuture.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(Sometime.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(SometimePast.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(SometimeFuture.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(NextPast.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(NextFuture.class.toString(), OutputSymbolType.PREFIX);

		// Atom
		setSymbol(Atom.class.toString(), " ");
		setSymbolType(Atom.class.toString(), OutputSymbolType.PREFIX);
		// Proposition
		setSymbol(Proposition.class.toString(), " ");
		setSymbolType(Proposition.class.toString(), OutputSymbolType.PREFIX);
		setParenthesis(Proposition.class.toString(), false);
		
	}
}
