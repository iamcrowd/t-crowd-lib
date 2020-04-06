package it.unibz.inf.qtl1.output.pltl;

import it.unibz.inf.qtl1.output.FormulaOutputFormat;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.*;
import it.unibz.inf.qtl1.formulae.temporal.*;
import it.unibz.inf.qtl1.formulae.quantified.*;

import it.unibz.inf.qtl1.output.OutputSymbolType;

public class PltlFormat extends FormulaOutputFormat {
	public PltlFormat(){
		super();
		this.setDefault();
	}
	
	/***
	 * Defines a base interpretation of symbols for TFOL:
	 * and, or, not, implies => &, |, ~, =>, <=>
	 * Existential and Universal quantifiers are expressed as Ex. and Ax.
	 * AlwaysFuture => G
	 * SometimeFuture => F 
	 * NextFuture, Until, Since  => X, U, S
	 * 
	 * All symbols are in Prefix notation except for Until and Since.
	 * 
	 * Only the following are surrounded by parenthesis: 
	 *  And, Or, Implication, Until and Since
	 *  
	 *  Note: This interpretation is derived from pltl notation.
	 */
	public void setDefault(){
		// Simple
		setSymbol(ConjunctiveFormula.class.toString(), " & ");
		setSymbol(DisjunctiveFormula.class.toString(), " | ");
		setSymbol(NegatedFormula.class.toString(), " ~ ");
		setSymbol(ImplicationFormula.class.toString(), " => ");
		setSymbol(BimplicationFormula.class.toString(), " <=> ");
		
		
		setParenthesis(ConjunctiveFormula.class.toString(), true);
		setParenthesis(DisjunctiveFormula.class.toString(), true);
		setParenthesis(NegatedFormula.class.toString(), false);
		setParenthesis(ImplicationFormula.class.toString(), true);
		setParenthesis(BimplicationFormula.class.toString(), true);

		setSymbolType(NegatedFormula.class.toString(), OutputSymbolType.PREFIX);

		// Temporal
		
		setSymbol(AlwaysFuture.class.toString(), " G ");
		setSymbol(SometimeFuture.class.toString(), " F ");
		setSymbol(NextFuture.class.toString(), " X ");
		setSymbol(Since.class.toString(), " S ");
		setSymbol(Until.class.toString(), " U ");
		
		setParenthesis(Until.class.toString(), true);
		setParenthesis(Since.class.toString(), true);
		
		setSymbolType(AlwaysFuture.class.toString(), OutputSymbolType.PREFIX);
		setSymbolType(SometimeFuture.class.toString(), OutputSymbolType.PREFIX);
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
