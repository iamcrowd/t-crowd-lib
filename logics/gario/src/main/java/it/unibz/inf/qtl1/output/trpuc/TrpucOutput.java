package it.unibz.inf.qtl1.output.trpuc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.*;

import it.unibz.inf.qtl1.output.OutputDocument;

import it.unibz.inf.qtl1.output.trpuc.TrpucFormat;


/***
 * Builds a pltl model to check SAT of the formula.
 * The formula is negated
 * @author gab
 *
 */
public class TrpucOutput extends OutputDocument {
	
	public TrpucOutput(Formula f) {
		super(f, new TrpucFormat());
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
			s.append("not ( bot & ");
		else
			s.append("not ( ");
		
		return s.toString();
	}

}
