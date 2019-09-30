package it.unibz.inf.qtl1.output;

import it.unibz.inf.qtl1.formulae.CNFFormula;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;

/***
 * This class extends the {@link LatexDocument} to build a document in which
 * each conjunct of the formula is visualized on a new line. This makes it possible to
 * have more readable formulas that split on many pages.
 * 
 * @author Marco Gario
 *
 */
public class LatexDocumentCNF extends LatexDocument {

	public LatexDocumentCNF(Formula f) {
		super(f, new LatexFormat());
		fmt.setSymbol(ConjunctiveFormula.class.toString(), " \\land $ \\\\ \n $ ");
		fmt.setSymbol(CNFFormula.class.toString(), " \\land $ \\\\ \n $ ");
	}
	
	public String getOpening() {
		String matharray="\n"+"$";
		return super.getOpening() + matharray;
	}
	
	public String getEnding(){
		String matharray="$ ";
		return matharray+super.getEnding();
	}

}
