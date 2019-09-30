package it.unibz.inf.qtl1.output;

import it.unibz.inf.qtl1.formulae.BimplicationFormula;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.DisjunctiveFormula;
import it.unibz.inf.qtl1.formulae.ImplicationFormula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.formulae.quantified.ExistentialFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.*;

/***
 * Defines symbols to use in {@link LatexDocument}. Note that some of this symbols
 * are macros and not available in standard Latex packages. 
 * 
 * @author Marco Gario
 *
 */
public class LatexFormat extends FormulaOutputFormat {
	public LatexFormat(){
		super();
		init();
	}
	
	public void init(){
		this.setDefault();
		// Simple
		setSymbol(ConjunctiveFormula.class.toString(), " \\land ");
		setSymbol(DisjunctiveFormula.class.toString(), " \\lor ");
		setSymbol(NegatedFormula.class.toString(), " \\lnot ");
		setSymbol(ImplicationFormula.class.toString(), " \\rightarrow ");
		setSymbol(BimplicationFormula.class.toString(), " \\leftrightarrow ");
		
		// Quantified
		setSymbol(ExistentialFormula.class.toString(), " \\exists _x_.");
		setSymbol(UniversalFormula.class.toString(), " \\forall _x_. ");
		
		// Temporal

		setSymbol(AlwaysFuture.class.toString(), " \\Rbox ");
		setSymbol(AlwaysPast.class.toString(), " \\Lbox ");
		setSymbol(SometimeFuture.class.toString(), " \\Rdiamond ");
		setSymbol(SometimePast.class.toString(), " \\Ldiamond ");
		setSymbol(NextFuture.class.toString(), " \\Rnext ");
		setSymbol(NextPast.class.toString(), " \\Lnext ");
		setSymbol(Since.class.toString(), " \\mathcal{S} ");
		setSymbol(Until.class.toString(), " \\mathcal{U} ");
		setSymbol(Always.class.toString()," \\SVbox " );
		setSymbol(Sometime.class.toString()," \\SVdiamond " );
		
	}

}
