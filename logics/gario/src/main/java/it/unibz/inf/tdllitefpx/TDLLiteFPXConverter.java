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
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class TDLLiteFPXConverter {
	
	TBox tbox;
	Alphabet a;
	Variable x;
	
	ConjunctiveFormula cardinalities = new ConjunctiveFormula();
	ConjunctiveFormula rigidR = new ConjunctiveFormula();
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

		this.getFactorizedEpsilon();
		this.getExtendedFormula();
		Formula epsilonx = getEpsilonX();

		
		if(factorize){
			F = new ConjunctiveFormula(
					    new Always(
							new ConjunctiveFormula(epsilonx, 
												   new ConjunctiveFormula(cardinalities,
												   						  rigidR))).normalize(), 
						eps);
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

		/** 
	 * Gathering formula with the variable "x"
	*/
	public Formula getEpsilonX(){
		Formula F = new UniversalFormula(
				new ConjunctiveFormula(getFactorizedT(),epsX),x);

		System.out.println("GetEpsilonX "+F.toString());
		return F;
	}
	
	/**
	 * Gathering the formula with the variable "x" to avoid redundancy 
	 * formula when make it propositional
	*/
	public Formula getEpsilonWithoutX(){
		return eps;
	}

	@deprecated
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
	
	
	/* A reduced version (with less modal operators) :
		*
		*  with epsilon'(S) = (\box \forall x ((E1S(x)-> \box pS ))) /\ (pinvS E1SInv(x) -> E1S(ds))
		*
		* epsilon''(S) =(\box \forall x (E1SInv(x)-> \box pSinv )) /\ (pS -> E1SInv(dsinv))
	*/
	private void getFactorizedEpsilon(){

		for( Role s : tbox.getRoles()){

			System.out.println("s in getFactorizedEpsilon "+s.toString());

			if(s instanceof PositiveRole){

				System.out.println("s is PositiveRole in getFactorizedEpsilon "+s.toString());
				
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

				}
			}
		}
	
	/***
	 * Reduction to QTL1
	 * Assert cardinality axioms (2) and rigid roles axioms (3) as explained in the report.
	 */
	public void getExtendedFormula(){
		Set<QuantifiedRole> qRoles= tbox.getQuantifiedRoles1();

		System.out.println("set of qRoles in addExtensionConstraints:"+qRoles.toString());
		
		/* delta: + >qR \subseteq >q'R
		 * for q > q' and >qR, >q'R in T an thre's no q'' s.t. q>q''>q' and q''R \in T
		 */
		Map<Role,List<QuantifiedRole>> qRMap = new HashMap<Role, List<QuantifiedRole>>();
		
		System.out.println("qRMap in addExtensionConstraints "+qRMap.toString());

		for(QuantifiedRole qR : qRoles){

			System.out.println("qR in addExtensionConstraints "+qR.toString());

			List<QuantifiedRole> list = qRMap.get(qR.getRole());

			if(list == null){
				list = new ArrayList<QuantifiedRole>();
				qRMap.put(qR.getRole(),list);
			}
			list.add(qR);
			System.out.println("list of QuantifiedRole in AddExtension"+list.toString());
		}
		
		for(Entry<Role, List<QuantifiedRole>> e: qRMap.entrySet()){
			List<QuantifiedRole> qrL = e.getValue();

			System.out.println("qrL in AddExtension"+qrL.toString());

			Collections.sort(qrL, new Comparator<QuantifiedRole>() {
				@Override
				public int compare(QuantifiedRole o1, QuantifiedRole o2) {
					return o2.getQ()-o1.getQ();
				}
			});
			
			for(int i = 0;i < qrL.size() - 1;i++){
				cardinalities.add(new ImplicationFormula(
						conceptToFormula(qrL.get(i)),
						conceptToFormula(qrL.get(i + 1))));

				System.out.println("Extending TBox Checking (2) in formula"+qrL.get(i)+" "+qrL.get(i+1));
			}
		}
		
		/* G: + >qR \subseteq BOX >qR 
		 * for >qR \in T an R is rigid role
		 * 
		 * TODO: Avoid duplications
		 */
		for(QuantifiedRole qR : qRoles){

			System.out.println("Exending TBox Checking (3) in formula (roles)"+qR.toString());

			if(qR.getRole().getRefersTo() instanceof AtomicRigidRole){

				System.out.println("Exending TBox Checking (3) in formula (if roles are rigid)"+qR.toString());

				rigidR.add(new ImplicationFormula(
					conceptToFormula(qR), 
					new Always(conceptToFormula(qR)).normalize()));

					System.out.println("Exending TBox Checking (3) in formula (if roles are rigid)"+rigidR.toString());
				    
			}
		}
		
	}

	/**
	 * Not factorised Epsilon formulas 
	 * */	
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
	
}
