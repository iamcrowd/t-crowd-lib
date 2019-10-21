package org.gario.code.output;

import java.util.HashMap;

/***
 *  Defines how to represent the different operators.
 *  Provides a default implementation in NuSMV-like style.
 *  
 * @author Marco Gario
 *
 */
public abstract class OutputFormat {
	HashMap<String,String> symbol;
	HashMap<String,Boolean> parenthesis;
	HashMap<String,OutputSymbolType> symbolPosition;
	
	public OutputFormat(){
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
}
