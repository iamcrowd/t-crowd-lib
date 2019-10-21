package it.unibz.inf.qtl1.formulae.quantified;

import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.terms.Variable;

public class UniversalFormula extends QuantifiedFormula {
	
	public UniversalFormula(Formula subFormula, Variable qVariable) {
		super(subFormula, qVariable);
	}

	public String toString(){
		return "A"+qVar+" "+subF;
	}
	
	public Object clone(){
		UniversalFormula nf = new UniversalFormula((Formula)subF.clone(),qVar);
		return nf;
	}

	@Override
	public Formula negateToNNF() {
		return new ExistentialFormula(subF.negateToNNF(), qVar);
	}

	@Override
	public Formula toNNF() {
		return new UniversalFormula(subF.toNNF(), qVar);
	}
}
