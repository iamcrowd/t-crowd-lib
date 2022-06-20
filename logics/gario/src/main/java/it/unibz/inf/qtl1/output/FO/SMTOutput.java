package it.unibz.inf.qtl1.output.FO;

import it.unibz.inf.qtl1.formulae.*;

import it.unibz.inf.qtl1.output.OutputDocument;


/***
 * Builds a pltl model to check SAT of the formula.
 * The formula is negated
 * @author gab
 *
 */
public class SMTOutput extends OutputDocument {
	
	public SMTOutput(Formula f) {
		super(f, new SMTFormat());
		fmt.setSymbol(ConjunctiveFormula.class.toString(), " & ");
	}

	public String getOutput(){
		return super.getOutput().replaceAll("\\{|\\}", "");
	}
	
	@Override
	public String getEnding() {
		return "";
	}

	@Override
	public String getOpening() {
		return "forall x.\n";
	}
	
/*		
		Set<Proposition> set = ((Formula)f).getPropositions();*/
//		StringBuilder s = new StringBuilder();
		
/*		boolean hasBot=false;
		
		List<Proposition> props = new ArrayList<Proposition>();
		props.addAll((((Formula)f).getPropositions()));
		Collections.sort(props);
		
		for(Proposition p : props){
			if(p.toString().equals("bot"))
				hasBot=true;
		}
		
		if(hasBot)
			s.append("( bot & ");
		else
			s.append("( ");*/
		
	//	return s.toString();
//	}

}
