package it.unibz.inf.qtl1.formulae.quantified;

import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.terms.Variable;

public class ExistentialFormula extends QuantifiedFormula {

	public ExistentialFormula(Formula subFormula, Variable qVariable) {
		super(subFormula, qVariable);
	}

	public String toString(){
		return "E"+qVar+" "+subF;
	}

	public Object clone(){
		ExistentialFormula nf = new ExistentialFormula((Formula)subF.clone(),qVar);
		return nf;
	}

	@Override
	public Formula negateToNNF() {
		return new UniversalFormula(subF.negateToNNF(), qVar);
	}

	@Override
	public Formula toNNF() {
			return new ExistentialFormula(subF.toNNF(), qVar);
	}
}
