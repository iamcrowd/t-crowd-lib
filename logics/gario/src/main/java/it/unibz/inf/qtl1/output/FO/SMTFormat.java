package it.unibz.inf.qtl1.output.FO;

import it.unibz.inf.qtl1.output.FormulaOutputFormat;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.*;
import it.unibz.inf.qtl1.formulae.temporal.*;
import it.unibz.inf.qtl1.formulae.quantified.*;

import it.unibz.inf.qtl1.output.OutputSymbolType;

public class SMTFormat extends FormulaOutputFormat {
	public SMTFormat(){
		super();
		this.setDefault();
	}
	
	/***
	 * Defines a base interpretation of symbols for SMT black:
	 * and, or, not, implies ->, &, |, not, <->
	 * Existential and Universal quantifiers are expressed as Ex. and Ax.
	 * AlwaysFuture => always
	 * SometimeFuture => sometime 
	 * NextFuture, Until, Since  => next, until, since
	 * 
	 * All symbols are in Prefix notation except for Until and Since.
	 * 
	 * Only the following are surrounded by parenthesis: 
	 *  And, Or, Implication, Until and Since
	 *  
	 *  Note: This interpretation is derived from TRP++UC notation.
	 */
	public void setDefault(){
		// Simple
		setSymbol(ConjunctiveFormula.class.toString(), " & ");
		setSymbol(DisjunctiveFormula.class.toString(), " | ");
		setSymbol(NegatedFormula.class.toString(), " ! ");
		setSymbol(ImplicationFormula.class.toString(), " -> ");
		setSymbol(BimplicationFormula.class.toString(), " <-> ");
		
		
		setParenthesis(ConjunctiveFormula.class.toString(), true);
		setParenthesis(DisjunctiveFormula.class.toString(), true);
		setParenthesis(NegatedFormula.class.toString(), false);
		setParenthesis(ImplicationFormula.class.toString(), true);
		setParenthesis(BimplicationFormula.class.toString(), true);

		setSymbolType(NegatedFormula.class.toString(), OutputSymbolType.PREFIX);

		// Temporal
		
		setSymbol(AlwaysFuture.class.toString(), " always ");
		setSymbol(SometimeFuture.class.toString(), " sometime ");
		setSymbol(NextFuture.class.toString(), " next ");
		setSymbol(Since.class.toString(), " since ");
		setSymbol(Until.class.toString(), " until ");
		
		// Quantified
		setSymbol(ExistentialFormula.class.toString(), " exists ");
		setSymbol(UniversalFormula.class.toString(), " forall ");
		
		setParenthesis(Until.class.toString(), true);
		setParenthesis(Since.class.toString(), true);
		
		setSymbolType(AlwaysFuture.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(SometimeFuture.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(NextFuture.class.toString(), OutputSymbolType.PREFIX);

		// Atom
		setSymbol(Atom.class.toString().toLowerCase(), " ");
		setSymbolType(Atom.class.toString().toLowerCase(), OutputSymbolType.PREFIX);
		// Proposition
		setSymbol(Proposition.class.toString().toLowerCase(), " ");
		setSymbolType(Proposition.class.toString().toLowerCase(), OutputSymbolType.PREFIX);
		setParenthesis(Proposition.class.toString().toLowerCase(), false);
		
	}

}
