package it.unibz.inf.tdllitefpx;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Bot;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.Alphabet;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.ImplicationFormula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.Always;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.ConjunctiveConcept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimeFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.concepts.temporal.TemporalConcept;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;

public class TDLLiteFPXConverter {
	
	TBox tbox;
	Alphabet a;
	Variable x;
	
	ConjunctiveFormula epsX = new ConjunctiveFormula();
	ConjunctiveFormula eps = new ConjunctiveFormula();
	
	public TDLLiteFPXConverter(TBox tbox){
		this.tbox = tbox;
		a = new Alphabet();
		x = new Variable("X");
		
	}
	
	public Formula getFormula(){
		return getFormula(true);
	}

	public Formula getFormulaToRemovePast(){

		Formula F = new UniversalFormula(
							new ConjunctiveFormula(new Always(getFactorizedT()).normalize(),
													epsX),x);

		System.out.println("Formula for pure future in Converter"+F.toString());							
		return F; //new Always(getEpsilonX());
	}
	/**
	 * Returns a K+ given an Extended TBox T*. 
	 * If factorize is set to true, the formula returned will
	 * have only 1 outermost universal quantifier, and the rest
	 * will be quantifier free. Moreover less box operators will be used
	 * 
	 * @param factorize
	 * @return
	 */
	public Formula getFormula(boolean factorize){
		/* The result formula has the following structure
		 * F = T /\ e
		 * 
		 * where
		 * 
		 * T = /\ \boxPF \forall x ( C1(x) -> C2(x) ) 
		 * 		(For each concept inclusion in T*)
		 * 
		 * e = /\ epsilon(s)    (For each role S)
		 *    
		 */
		
		/* supprimer pour Eviter la redondance des formules
	 	
	 	if(!tbox.isExtended())
		tbox.addExtensionConstraints();
		
		*/
/*		if(factorize){
			return new UniversalFormula(
					new ConjunctiveFormula(
							new Always(getFactorizedT()),
							getFactorizedEpsilon()), x);*/
		Formula F;
		Formula epsilon = getFactorizedEpsilon();
		Formula epsilonx = getEpsilonX();

		if(factorize){
			F = new ConjunctiveFormula(new Always(epsilonx).normalize(), eps);
		}else {
			F = new ConjunctiveFormula(getT(), 
									  getEpsilon());
		}
		return F;
	}
	
	
	private Formula getT(){
		
		ConjunctiveFormula out = new ConjunctiveFormula();
		for(ConceptInclusionAssertion ci: tbox){
			out.add(new Always(new UniversalFormula(
						new ImplicationFormula(
								conceptToFormula(ci.getLHS()),
								conceptToFormula(ci.getRHS())), new Variable("X"))));
		}
		return out;
	}
	
	private Formula getFactorizedT(){
		/*
		 * Returns \box \bigwedge c1->c2
		 */
		
		ConjunctiveFormula out = new ConjunctiveFormula();
		for(ConceptInclusionAssertion ci: tbox){
			/* 
			 * Special cases are Top -> A and B -> bot 
			 */
			if(ci.getLHS() instanceof NegatedConcept && 
					((NegatedConcept)ci.getLHS()).getRefersTo() instanceof BottomConcept){
				out.add(conceptToFormula(ci.getRHS()));
			}else if(ci.getRHS() instanceof BottomConcept){
				out.add(new NegatedFormula(conceptToFormula(ci.getLHS())));
			}else
				out.add(new ImplicationFormula(
								conceptToFormula(ci.getLHS()),
								conceptToFormula(ci.getRHS())));
		}
		return out;
	}
	
	public Formula conceptToFormula(Concept c){
		if(c instanceof AtomicConcept)
			return new Atom(c.toString(), x);
		else if(c instanceof QuantifiedRole){
			QuantifiedRole qE = (QuantifiedRole)c;
			Atom atom = a.get("E"+qE.getQ()+qE.getRole().toString(),1);
			atom.setArg(0, x);
			return atom;
		}
		else if(c instanceof BottomConcept)
			return Bot.getStatic();
		else if(c instanceof NegatedConcept){
			NegatedConcept nc = (NegatedConcept) c;
			return new NegatedFormula(conceptToFormula(nc.getRefersTo()));
		}
		else if(c instanceof ConjunctiveConcept){
			ConjunctiveConcept cc = (ConjunctiveConcept)c;
			ConjunctiveFormula cf = new ConjunctiveFormula();
			for(Concept d : cc.getConjuncts()){
				cf.addConjunct(conceptToFormula(d));
			}
			return cf;
		}
		else if(c instanceof TemporalConcept){
			TemporalConcept d = (TemporalConcept) c;
			if(c instanceof NextFuture){
				return new it.unibz.inf.qtl1.formulae.temporal.NextFuture(
						conceptToFormula(d.getRefersTo()));
			}else if(c instanceof NextPast){
				return new it.unibz.inf.qtl1.formulae.temporal.NextPast(
						conceptToFormula(d.getRefersTo()));
			}else if(c instanceof AlwaysPast){
				return new it.unibz.inf.qtl1.formulae.temporal.AlwaysPast(
						conceptToFormula(d.getRefersTo()));
			}else if(c instanceof AlwaysFuture){
				return new it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture(
						conceptToFormula(d.getRefersTo()));
			}else if(c instanceof SometimePast){
				return new it.unibz.inf.qtl1.formulae.temporal.SometimePast(
						conceptToFormula(d.getRefersTo()));
			}else if(c instanceof SometimeFuture){
				return new it.unibz.inf.qtl1.formulae.temporal.SometimeFuture(
						conceptToFormula(d.getRefersTo()));
			}else if(c instanceof it.unibz.inf.tdllitefpx.concepts.temporal.Always){
				return new it.unibz.inf.qtl1.formulae.temporal.Always(
						conceptToFormula(d.getRefersTo()));
			}
		}
		
		System.err.println("Unexpected case "+c.getClass().toString());
		return null;
			
			
	}
	private Formula getEpsilon(){
		/* Given S the name of the role:
		 * 
		 * epsilon(S) = ((pS -> E1S(ds)) /\ (pS -> E1SInv(dsinv))) /\
		 * 				(\box \forall x ((E1S(x)-> \box ps ) /\ E1SInv(x) -> \box ps))
		 * 
		 * epsilon = /\ S \in Roles epsilon(S)
		 * 
		 * 
		 * 
		 */
		ConjunctiveFormula cf = new ConjunctiveFormula();
		for( Role r : tbox.getRoles()){
			if(r instanceof PositiveRole)
				cf.add(getEpsilon(r));
		}
		return cf;
	}
	
	
	//gathering formula with the variable "x"
	public Formula getEpsilonX(){
		Formula F = new UniversalFormula(
				new ConjunctiveFormula(getFactorizedT(),epsX),x);
		return F;
	}
	
	//gathering formula with the variable "x" to avoid redundancy formula when make it propositional
	public Formula getEpsilonWithoutX(){
		return eps;
	}
	
	private Formula getFactorizedEpsilon(){

		/* A reduced version (with less modal operators) :
		*
		*  with epsilon'(S) = (\box \forall x ((E1S(x)-> \box pS ))) /\ (pinvS E1SInv(x) -> E1S(ds))
		*
		* epsilon''(S) =(\box \forall x (E1SInv(x)-> \box pSinv )) /\ (pS -> E1SInv(dsinv))
		*/

		ConjunctiveFormula eps1 = new ConjunctiveFormula();
		ConjunctiveFormula eps2 = new ConjunctiveFormula();

		for( Role s : tbox.getRoles()){

			if(s instanceof PositiveRole){
				
				Proposition pS = (Proposition) a.get("P"+s.toString(),0);
				Proposition pinvS = (Proposition) a.get("Pinv"+s.toString(),0);
				Role SInv = s.getInverse();

				Concept E1S = new QuantifiedRole(s, 1);
				Concept E1SInv = new QuantifiedRole(SInv, 1);

				Constant ds = new Constant("d"+s.toString());
				Constant dsinv = new Constant("d"+SInv.toString());



				Formula fE1S_ds = conceptToFormula(E1S);
				fE1S_ds.substitute(x, ds);

				Formula fE1SInv_ds = conceptToFormula(E1SInv);
				fE1SInv_ds.substitute(x, dsinv);


				epsX.add(new ImplicationFormula(conceptToFormula(E1SInv),
					 						new Always(conceptToFormula(E1SInv)).normalize()
										   )
					);

				epsX.add(new ImplicationFormula(conceptToFormula(E1S),
					 						new Always(pS).normalize()
										   )
					);

				epsX.add(new ImplicationFormula(conceptToFormula(E1SInv),
					 						new Always(pinvS).normalize()
										   )
					);

				eps.add(new ImplicationFormula(
						pinvS,
						fE1S_ds));
			
				eps.add(new ImplicationFormula(
						pS,
						fE1SInv_ds));
			
				// old stuff		 

				eps1.add(new ImplicationFormula(conceptToFormula(E1S),
					    new Always(pS)));
				eps1.add(new ImplicationFormula(
					pinvS,
					fE1S_ds));

				eps2.add(new ImplicationFormula( conceptToFormula(E1SInv),
					 new Always(pinvS)));
				eps2.add(new ImplicationFormula(
					pS,
					fE1SInv_ds));

				}
			}
				return new ConjunctiveFormula(eps1, eps2);
		}
	
	
	/**
	 * Not factorised Epsilon formulas */	
	private Formula getEpsilon(Role s){
		 /*
		  *  epsilon(S) =
		  *  A. (\box \forall x ((E1S(x)-> \box pS )) /\ (pinvS -> E1S(ds))
		  *  B. (\box \forall x ((E1SInv(x)-> \box pSinv )) /\ (pS -> E1SInv(dsinv))
		  */
		 
		Proposition pS = (Proposition) a.get("p"+s.toString(),0);
		Proposition pinvS = (Proposition) a.get("pinv"+s.toString(),0);

		Role SInv = s.getInverse();
		Concept E1S = new QuantifiedRole(s, 1);
		Concept E1SInv = new QuantifiedRole(SInv, 1);
		
		Constant ds = new Constant("d"+s.toString());
		Constant dsinv = new Constant("d"+SInv.toString());
		
		Formula fE1S_ds = conceptToFormula(E1S);
		fE1S_ds.substitute(x, ds);
		
		Formula fE1SInv_ds = conceptToFormula(E1SInv);
		fE1SInv_ds.substitute(x, dsinv);
	
		
		UniversalFormula fA = new UniversalFormula(
				new ConjunctiveFormula(
						new Always(new ImplicationFormula(
								conceptToFormula(E1S), 
								new Always(pS))),
						new ImplicationFormula(
								pinvS, 
								fE1S_ds)),
				x);
		UniversalFormula fB = new UniversalFormula(
				new ConjunctiveFormula(
						new Always(new ImplicationFormula(
								conceptToFormula(E1SInv), 
								new Always(pinvS))),
						new ImplicationFormula(
								pS, 
								fE1SInv_ds)),
				x);
		
		ConjunctiveFormula eps = new ConjunctiveFormula(fA,fB);

		return eps;
	}	

	private Formula getFactorizedEpsilon1(){
		/* A reduced version (with less modal operators) : 
		 * 		
		 * 
		 *  	/\_all S epsilon'(S) /\ 
		 *  	\box /\_all S epsilon''(S)
		 *  
		 *   with epsilon'(S) = ((pS -> E1S(ds)) /\ (pS -> E1SInv(dsinv)))
		 * 		
		 * 		  epsilon''(S) =((E1S(x)-> \box ps ) /\ E1SInv(x) -> \box ps)
		 */
		ConjunctiveFormula eps1 = new ConjunctiveFormula();
		ConjunctiveFormula eps2 = new ConjunctiveFormula();
		
		for( Role s : tbox.getRoles()){
			if(s instanceof PositiveRole){
				Proposition pS = (Proposition) a.get("p"+s.toString(),0);
				Role SInv = s.getInverse();
				
				Concept E1S = new QuantifiedRole(s, 1);
				Concept E1SInv = new QuantifiedRole(SInv, 1);
				
				Constant ds = new Constant("d"+s.toString());
				Constant dsinv = new Constant("d"+SInv.toString());
				
				Formula fE1S_ds = conceptToFormula(E1S);
				fE1S_ds.substitute(x, ds);
				
				Formula fE1SInv_ds = conceptToFormula(E1SInv);
				fE1SInv_ds.substitute(x, dsinv);
				
				eps1.add(new ImplicationFormula(
								pS,
								fE1S_ds));
				eps1.add(new ImplicationFormula(
								pS,
								fE1SInv_ds));
				
				eps2.add(new ImplicationFormula(
								conceptToFormula(E1S), 
								new Always(pS)));
				eps2.add(new ImplicationFormula(
								conceptToFormula(E1SInv),
								new Always(pS)));
			}
		}
		
		return new ConjunctiveFormula(eps1, eps2);
	}
	
	private Formula getEpsilon1(Role s){
		 /*
		  *  epsilon(S) =
		  *  A.  ((pS -> E1S(ds)) /\ (pS -> E1SInv(dsinv))) /\
		  *  B.  (\box \forall x ((E1S(x)-> \box ps ) /\ E1SInv(x) -> \box ps))
		  *  
		  */
		 
		Proposition pS = (Proposition) a.get("p"+s.toString(),0);
		Role SInv = s.getInverse();
		
		Concept E1S = new QuantifiedRole(s, 1);
		Concept E1SInv = new QuantifiedRole(SInv, 1);
		
		Constant ds = new Constant("d"+s.toString());
		Constant dsinv = new Constant("d"+SInv.toString());
		
		Formula fE1S_ds = conceptToFormula(E1S);
		fE1S_ds.substitute(x, ds);
		
		Formula fE1SInv_ds = conceptToFormula(E1SInv);
		fE1SInv_ds.substitute(x, dsinv);
		
		ConjunctiveFormula fA = new ConjunctiveFormula(
				new ImplicationFormula(
						pS,
						fE1S_ds),
				new ImplicationFormula(
						pS,
						fE1SInv_ds));
		
		Always fB = new Always(new UniversalFormula(
				new ConjunctiveFormula(
						new ImplicationFormula(
								conceptToFormula(E1S), 
								new Always(pS)),
						new ImplicationFormula(
								conceptToFormula(E1SInv),
								new Always(pS))),
				x));
		
		ConjunctiveFormula eps = new ConjunctiveFormula(fA,fB);
		
		return eps;
	}
	
	private Formula getEpsilon2(Role s){
		 /*
		  *  epsilon(S) =
		  *  A.  ((pS -> E1S(ds)) /\ (pS -> E1SInv(dsinv))) /\
		  *  B.  (\box \forall x ((E1S(x)-> \box ps ) /\ E1SInv(x) -> \box ps))
		  *  
		  */
		 
		Proposition pS = (Proposition) a.get("p"+s.toString(),0);
		Role SInv = s.getInverse();
		
		Concept E1S = new QuantifiedRole(s, 1);
		Concept E1SInv = new QuantifiedRole(SInv, 1);
		
		Constant ds = new Constant("d"+s.toString());
		Constant dsinv = new Constant("d"+SInv.toString());
		
		Formula fE1S_ds = conceptToFormula(E1S);
		fE1S_ds.substitute(x, ds);
		
		Formula fE1SInv_ds = conceptToFormula(E1SInv);
		fE1SInv_ds.substitute(x, dsinv);
		
		ConjunctiveFormula fA = new ConjunctiveFormula(
				new ImplicationFormula(
						pS,
						fE1S_ds),
				new ImplicationFormula(
						pS,
						fE1SInv_ds));
		
		Always fB = new Always(new UniversalFormula(
				new ConjunctiveFormula(
						new ImplicationFormula(
								conceptToFormula(E1S), 
								new Always(pS)),
						new ImplicationFormula(
								conceptToFormula(E1SInv),
								new Always(pS))),
				x));
		
		ConjunctiveFormula eps = new ConjunctiveFormula(fA,fB);
		
		return eps;
	}
	
	
}
