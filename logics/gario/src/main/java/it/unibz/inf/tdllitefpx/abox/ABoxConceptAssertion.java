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
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;

public class ABoxConceptAssertion implements FormattableObj{
	Concept c;
	String value;
	
	public ABoxConceptAssertion(Concept c, String v){
		this.c = c;
		this.value = v;
		// A(x) A(y) G A(maria)
	}
	
	public Concept getConceptAssertion(){
		return this.c;
	}
	
	public Constant getConstant(){
		return new Constant (this.value);
	}
	
	
	public Formula getFormula(){
		Variable x = new Variable("x");
		Formula fa=new Atom(c.toString(), x);
		//Formula fc=conceptToFormula(c);
		//UniversalFormula fa = new UniversalFormula(fc, x);
		return fa;
	}
	

	public Formula makeAssertionPropositional(){
		Variable x = new Variable("x");
		Formula fa= this.getFormula();
		fa.substitute(x, new Constant(value));
		System.out.println("insertionltl:"+fa);
		return fa;
	}
	
	@Override
	
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return this.toString();
	}

}
