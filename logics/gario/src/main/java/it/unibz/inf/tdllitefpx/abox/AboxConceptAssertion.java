package it.unibz.inf.tdllitefpx.abox;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;
import org.gario.code.output.SymbolUndefinedException;

import it.unibz.inf.tdllitefpx.concepts.Concept;

public class AboxConceptAssertion implements FormattableObj{
	
	
	
	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException {
		
		return "to be implemented";
		
	/*	return getLHS().toString(fmt) +
		fmt.getSymbol(this)+
		getRHS().toString(fmt); */
		
	}

}