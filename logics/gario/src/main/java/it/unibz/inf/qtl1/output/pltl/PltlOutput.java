package it.unibz.inf.qtl1.output.pltl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.*;

import it.unibz.inf.qtl1.output.OutputDocument;
import it.unibz.inf.qtl1.output.NuSMVFormat;

import it.unibz.inf.qtl1.output.pltl.PltlFormat;


/***
 * Builds a pltl model to check SAT of the formula.
 * The formula is negated
 * @author gab
 *
 */
public class PltlOutput extends OutputDocument {
	
	public PltlOutput(Formula f) {
		super(f, new PltlFormat());
		fmt.setSymbol(ConjunctiveFormula.class.toString(), " & ");

	}

	public String getOutput(){
		return super.getOutput().replaceAll("\\{|\\}", "");
	}
	
	@Override
	public String getEnding() {
		return ")\n";
	}

	@Override
	public String getOpening() {
		Set<Proposition> set = ((Formula)f).getPropositions();
		StringBuilder s = new StringBuilder();
		
		boolean hasBot=false;
		
		List<Proposition> props = new ArrayList<Proposition>();
		props.addAll((((Formula)f).getPropositions()));
		Collections.sort(props);
		
		for(Proposition p : props){
			if(p.toString().equals("bot"))
				hasBot=true;
		}
		
		if(hasBot)
			s.append("~ ( bot & ");
		else
			s.append("~ ( ");
		
		return s.toString();
	}

}
