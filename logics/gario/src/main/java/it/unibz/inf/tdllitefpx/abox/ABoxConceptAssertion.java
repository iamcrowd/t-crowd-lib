package it.unibz.inf.tdllitefpx.abox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.tdllitefpx.concepts.Concept;

public class ABoxConceptAssertion implements FormattableObj{
	Concept c;
	String value;
	
	/**
	 * An ABox concept is a Concept instance and a String value as constant
	 * 
	 * @param c a Concept
	 * @param v a String
	 */
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
		Formula fa = new Atom(c.toString(), x);
		return fa;
	}
	

	public Formula makeAssertionPropositional(){
		Variable x = new Variable("x");
		Formula fa = this.getFormula();
		fa.substitute(x, new Constant(value));
		return fa;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ABoxConceptAssertion){
			ABoxConceptAssertion co = (ABoxConceptAssertion) obj;
			return (co.c.equals(this.c) & co.value.equals(this.value));
		} else
			return false;	
	}
	
	public int hashCode(){
		return this.getConceptAssertion().hashCode();
	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return this.makeAssertionPropositional().toString();
	}



}
