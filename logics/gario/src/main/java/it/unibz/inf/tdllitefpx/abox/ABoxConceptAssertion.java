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
	Concept concept;
	Individual individual;

	int hash = 0;
	
	/**
	 * An ABox concept is a Concept instance and a String value as constant
	 * 
	 * @param c a Concept
	 * @param v a String
	 */
	public ABoxConceptAssertion(Concept c, String v){
		this.concept = c;
		this.individual = new Individual(v);
	}
	
	public Concept getConcept(){
		return this.concept;
	}

	public Individual getIndividual() {
		return individual;
	}
	
	public Constant getConstant(){
		return new Constant (this.individual.toString());
	}
	
	
	public Formula getFormula(){
		Variable x = new Variable("x");
		Formula fa = new Atom(concept.toString(), x);
		return fa;
	}
	

	public Formula makeAssertionPropositional(){
		Variable x = new Variable("x");
		Formula fa = this.getFormula();
		fa.substitute(x, new Constant(individual.toString()));
		return fa;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ABoxConceptAssertion){
			ABoxConceptAssertion co = (ABoxConceptAssertion) obj;
			return (co.concept.equals(this.concept) & co.individual.equals(this.individual));
		} else
			return false;	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = prime * hash + ((concept == null) ? 0 : concept.hashCode());
		hash = prime * hash + ((individual == null) ? 0 : individual.hashCode());

		return hash;
	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		return this.makeAssertionPropositional().toString();
	}



}
