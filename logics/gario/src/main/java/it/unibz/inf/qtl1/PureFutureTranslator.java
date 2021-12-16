package it.unibz.inf.qtl1;

import it.unibz.inf.qtl1.formulae.BimplicationFormula;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.DisjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysPast;
import it.unibz.inf.qtl1.formulae.temporal.NextFuture;
import it.unibz.inf.qtl1.formulae.temporal.NextPast;
import it.unibz.inf.qtl1.formulae.temporal.SometimeFuture;
import it.unibz.inf.qtl1.formulae.temporal.SometimePast;


/**
 * This class reduce the PLTL (Past LTL) formulae to LTL pure future.
 * 
 * @author gab
 *
 */
public class PureFutureTranslator extends NaturalTranslator{
	
	public PureFutureTranslator(Formula f) throws Exception{
		super(f);
	}

	
	/**
	 * One of the main method of the class that returns the reduction over natural of the original
	 * formula using only future operators.
	 * 
	 * QTL(Z) -> QTL(N) without past operators.
	 * 
	 * @see getTranslation method keeps Yesterday past operator.
	 * @return
	 */
	public Formula getPureFutureTranslation() {
		if(translation==null){
			ConjunctiveFormula cf = new ConjunctiveFormula(); 
			//Translate
			/*
			 * The translation can be divided into 3 parts:
			 * - ReFormulation
			 * - Propositional equivalence
			 * - Modal equivalence removing past operators
			 */
			cf.add(super.getReformulation());
			System.out.println("Rerformulation: "+ super.getReformulation());
			System.out.println("\n");
			
			cf.add(getModalEquivalences());
			System.out.println("Modal Equiv Pure Future: "+ getModalEquivalences());
			System.out.println("\n");
			
			cf.add(super.getAtomicEquivalences());
			System.out.println("Atomic Equiv: "+ super.getAtomicEquivalences());
			System.out.println("\n");
			
			translation = new UniversalFormula(cf, x);
		}
		return translation;
	}
	
	/**
	 * Modal Equivalences are the parts of the translation in which we 
	 * rewrite the temporal operators. (Line 3-8 of the translation)
	 * @return
	 */
	private Formula getModalEquivalences() {
		/*
		 * The Modal equivalence is stated by three groups of 
		 * conjunctions, one for each temporal operator (Next,
		 * Always, Sometime).
		 * 
		 * We use the following translation to replace Y past operator 
		 * in the original formulae. 
		 * Moreover, due to the elimination of Y, 
		 * we remove all formulae from nxt box part. 
		 * Intuitively, this is because we are firing these 
		 * formulas looking one point in time ahead.
		 * 
		 * Resulting formula is a big AlwaysFuture(conjunctive formula)
		 */
		
		ConjunctiveFormula cf = new ConjunctiveFormula();
		ConjunctiveFormula cF ;
		ConjunctiveFormula cP ;
		
		////////////////////////// Next
		// 1.
		// ( A /\ B)
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(NextFuture s : nxtF){				// X nxtsubF_ <-> nxtsubF_ 
			cF.add(new BimplicationFormula(
											new NextFuture(super.tr(s, true)),
											super.tr(s.getSubFormulae().get(0),true)
										  )
				  );
		}
		
		for(NextFuture s : nxtF){
			cF.add(new BimplicationFormula(
					super.tr(s, false),
					new NextFuture(super.tr(s.getSubFormulae().get(0),false))
					));
		}		
		
		for(NextPast s : nxtP){
			cP.add(new BimplicationFormula(
											new NextFuture(super.tr(s, false)),
											super.tr(s.getSubFormulae().get(0),false)
										  )
				  );
		}
		
		for(NextPast s : nxtP){
			cP.add(new BimplicationFormula(
					super.tr(s, true),
					new NextFuture(super.tr(s.getSubFormulae().get(0),true))
					));
		}
		
		
		if(cF.getSubFormulae().size() > 0 || cP.getSubFormulae().size() > 0)
			cf.add(new ConjunctiveFormula(cF,cP));
		
		
		
		///////////////////////// Sometime
		// 1.
		// ( A /\ B)
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(SometimeFuture s : diaF){
			cF.add(new BimplicationFormula(
											new NextFuture(super.tr(s, true)),
											new DisjunctiveFormula(
													super.tr(s,true),
													new NextFuture(super.tr(s.getSubFormulae().get(0),true))
												)
										  )
				  );
		}
		
		for(SometimeFuture s : diaF){
			cF.add(new BimplicationFormula(
											super.tr(s, false),
											new SometimeFuture(super.tr(s.getSubFormulae().get(0),false))
										  )
				  );
		}
		
		for(SometimePast s : diaP){
			cP.add(new BimplicationFormula(
											new NextFuture(super.tr(s, false)),
											new DisjunctiveFormula(
													super.tr(s,false),
													new NextFuture(super.tr(s.getSubFormulae().get(0),false))
												)
										   )
				  );
		}
		
		for(SometimePast s : diaP){
			cP.add(new BimplicationFormula(
											super.tr(s, true),
											new SometimeFuture(super.tr(s.getSubFormulae().get(0),true))
										  )
				  );
		}
		
		if(cF.getSubFormulae().size() > 0 || cP.getSubFormulae().size() > 0)
			cf.add(new ConjunctiveFormula(cF,cP));

		
		
		//////////////////////// Always 
		// 1.
		// ( A /\ B)
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(AlwaysFuture s : boxF){
			cF.add(new BimplicationFormula(
											new NextFuture(super.tr(s, true)),
											new ConjunctiveFormula(
													super.tr(s,true),
													new NextFuture(super.tr(s.getSubFormulae().get(0),true))
												)
										  )
				 );
		}
		
		for(AlwaysFuture s : boxF){
			cF.add(new BimplicationFormula(
											super.tr(s, false),
											new AlwaysFuture(super.tr(s.getSubFormulae().get(0),false))
										  )
				  );
		}
		
		for(AlwaysPast s : boxP){
			cP.add(new BimplicationFormula(
											new NextFuture(super.tr(s, false)),
											new ConjunctiveFormula(
													super.tr(s,false),
													new NextFuture(super.tr(s.getSubFormulae().get(0),false))
											)
										  )
				  );
		}
		
		for(AlwaysPast s : boxP){
			cP.add(new BimplicationFormula(
											super.tr(s, true),
											new AlwaysFuture(super.tr(s.getSubFormulae().get(0),true))
										  )
				  );
		}
		
		if (cF.getSubFormulae().size() > 0 || cP.getSubFormulae().size() > 0)
			cf.add(new ConjunctiveFormula(cF,cP));
		
		
		return new AlwaysFuture(cf);

	}
	
}
