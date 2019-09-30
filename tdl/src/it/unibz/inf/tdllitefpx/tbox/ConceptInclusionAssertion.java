package it.unibz.inf.tdllitefpx.tbox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class ConceptInclusionAssertion implements FormattableObj{
	Concept lhs,rhs;
	
	public ConceptInclusionAssertion(Concept lhs, Concept rhs){
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Concept getLHS() { return lhs;}
	public Concept getRHS() { return rhs;}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		
		return getLHS().toString(fmt) +
		fmt.getSymbol(this)+
		getRHS().toString(fmt); 
		
	}

}
