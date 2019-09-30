package it.unibz.inf.qtl1.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.*;



/***
 * Builds a NuSMV model to check SAT of the formula.
 * The formula is negated and included in an LTLSPEC !(..) statemnt
 * @author Marco Gario
 *
 */
public class NuSMVOutput extends OutputDocument {

	String opening;
	
	public NuSMVOutput(Formula f) {
		super(f, new NuSMVFormat());
		opening="MODULE main\n"+
				"VAR"+"\n";
		
		fmt.setSymbol(ConjunctiveFormula.class.toString(), " & \n\t");

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
		StringBuilder s = new StringBuilder(set.size()*5*": boolean;\n".length()+opening.length());
		s.append(opening);
		
		boolean hasBot=false;
		
		List<Proposition> props = new ArrayList<Proposition>();
		
		props.addAll((((Formula)f).getPropositions()));
		
		Collections.sort(props);
		
		//for(Proposition p : ((Formula)f).getPropositions()){
		for(Proposition p : props){
			s.append("\t"+p+" :boolean;\n");
			if(p.toString().equals("bot"))
				hasBot=true;
		}
		if(hasBot)
			s.append("\n\nLTLSPEC !( bot & ");
		else
			s.append("\n\nLTLSPEC !( ");
		return s.toString();
	}

}
