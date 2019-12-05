package it.unibz.inf.tdllitefpx.abox;


import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.SometimeFuture;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.terms.Variable;

public class AboxConceptAssertion implements FormattableObj{
	Concept c;
	String value;
	
	public AboxConceptAssertion(Concept c, String v){
		// A(x) A(y) G A(maria)
		this.c = c;
		this.value = v;
	}

	public Formula getFormula() {
		Variable x = new Variable("x");
		Formula fa = new Atom(c.toString(), x);
		//System.out.println("insertion:"+fa);
		return fa;
		//UniversalFormula a1 = new UniversalFormula(conceptToFormula(this.c), x);
	}
	
	public Concept getAboxConceptAssertion() {
		return this.c;
	}
	
	public String getAboxValueAssertion() {
		return this.value;
	}
	
	public Formula makeAssertionPropositional(){
		Variable x = new Variable("x");
		Formula fa = this.getFormula();
		fa.substitute(x, new Constant(value));
		System.out.println("insertionltl:"+fa);
		return fa;
	}
	
	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		//return this.toString();
		
		return getAboxConceptAssertion().toString(fmt) +
		"(" + getAboxValueAssertion().toString() + ")";
	}

}