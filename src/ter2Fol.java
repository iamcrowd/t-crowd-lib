
import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.ExampleTDL;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;


public class ter2Fol {
	public static void main(String[] args) throws Exception{
		
		String fileName="tdl2ltl";
		
		TBox tBox = new ExampleTDL().getTBox();
		
		System.out.println("Saving in "+fileName);
		
		System.out.println("Original TBox...");
		(new LatexOutputDocument(tBox)).toFile(fileName+"_tbox.tex");
		
		
		TDLLiteFPXConverter converter = new TDLLiteFPXConverter(tBox);
		Formula qtl = converter.getFormula(true);
		
		System.out.println("QTL translation...");
		(new LatexDocumentCNF(qtl)).toFile(fileName+"_qtl.tex");
	
		Formula qtlN =  qtl.makeTemporalStrict();
		qtlN =  (new NaturalTranslator(qtl)).getTranslation();

		System.out.println("QTL over N translation...");
		(new LatexDocumentCNF(qtlN)).toFile(fileName+"_qtlN.tex");

		Formula ltl = qtlN.makePropositional();

		System.out.println("LTL over N translation...");
		(new LatexDocumentCNF(ltl)).toFile(fileName+"_ltl.tex");
		
		System.out.println("LTL Formula:");
		System.out.println("Num of Propositions: "+ltl.getPropositions().size());
		System.out.println("Num of Constants: "+ltl.getConstants().size());

		
		System.out.println("NuSMV file...");
		(new NuSMVOutput(ltl)).toFile(fileName+".smv");
	
		System.out.println("Done!");
		// ./NuSMV -bmc -dcx
	
	
	

		
	}
	
	

}
















