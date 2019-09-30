package it.unibz.inf.tdllitefpx.output;


import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimeFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;

import org.gario.code.output.OutputFormat;

/***
 * Defines symbols to use in {@link LatexDocument}. Note that some of this symbols
 * are macros and not available in standard Latex packages. 
 * 
 * @author Marco Gario
 *
 */
public class LatexFormat extends OutputFormat {
	public LatexFormat(){
		super();
		init();
	}
	
	public void init(){
		// Simple
		setSymbol(NegatedConcept.class.toString(), " \\lnot ");
		setSymbol(BottomConcept.class.toString(), " \\bot ");
		
		// Quantified
		setSymbol(QuantifiedRole.class.toString(), " \\ge ");

		
		// Temporal

		setSymbol(AlwaysFuture.class.toString(), " \\Rbox ");
		setSymbol(AlwaysPast.class.toString(), " \\Lbox ");
		setSymbol(SometimeFuture.class.toString(), " \\Rdiamond ");
		setSymbol(SometimePast.class.toString(), " \\Ldiamond ");
		setSymbol(NextFuture.class.toString(), " \\Rnext ");
		setSymbol(NextPast.class.toString(), " \\Lnext ");
		
		setSymbol(TBox.class.toString(),"$\\\\ \n $");
		setSymbol(ConceptInclusionAssertion.class.toString()," \\sqsubseteq ");
		
		
	}

}
