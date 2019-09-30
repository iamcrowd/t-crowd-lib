import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.ImplicationFormula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture;
import it.unibz.inf.qtl1.formulae.temporal.SometimeFuture;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;


public class Ex01 {

	public static void main(String[] args) throws Exception{
		String fileName="/ram/highlander";
		
		Formula f = unsatEx();
		Formula ltl = f.makePropositional();
		
		System.out.println(f);
		System.out.println(ltl);
		System.out.println("Saving in "+fileName);
		(new LatexDocumentCNF(f)).toFile(fileName+".tex");
		(new LatexDocumentCNF(ltl)).toFile(fileName+"_ltl.tex");
		(new NuSMVOutput(ltl)).toFile(fileName+".smv");
		
		
	}

	public static Formula unsatEx(){
		/***
		 * Everybody dies sooner or later.
		 * Highlanders don't die (for natural reasons...)
		 * 
		 * SAT -> If there is no Highlander
		 * 
		 * Ax. F Die(x) & Ax. (Highlander(x) -> G(!Die(x))) & Highlander(McLeod)
		 * 
		 * UNSAT
		 */
		
		ConjunctiveFormula f = new ConjunctiveFormula();
		Variable x = new Variable("x");
		Atom die = new Atom("Die",x);
		Constant mcleod = new Constant("McLeod");
		Atom highlander_x = new Atom("Highlander",x);
		Atom highlander_mcloed = new Atom("Highlander",mcleod);
		
		
		UniversalFormula c1 = new UniversalFormula(new SometimeFuture(die), x);
		UniversalFormula c2 = new UniversalFormula(
				new ImplicationFormula(highlander_x, new AlwaysFuture( new NegatedFormula(die)))
				, x);
		
		
		f.addConjunct(c1);
		f.addConjunct(c2);
		f.addConjunct(highlander_mcloed);
		
		return f;
	}
}
