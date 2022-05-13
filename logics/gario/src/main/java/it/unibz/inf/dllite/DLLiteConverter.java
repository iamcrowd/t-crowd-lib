package it.unibz.inf.dllite;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Bot;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.Alphabet;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.ImplicationFormula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;
import it.unibz.inf.tdllitefpx.TDLLiteNFPXConverter;
import it.unibz.inf.tdllitefpx.abox.ABox;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Future;


public class DLLiteConverter extends TDLLiteNFPXConverter{
	
//	TBox tbox;
//	ABox abox;
//	Boolean abs;
//	Alphabet a;
//	Variable x;
	
	ConjunctiveFormula cardinalities = new ConjunctiveFormula();
	ConjunctiveFormula rigidR = new ConjunctiveFormula();
	ConjunctiveFormula epsX = new ConjunctiveFormula();
	HashMap<Role, Formula> RolesAndConstsMap = new HashMap<Role, Formula>();
	
	public DLLiteConverter(TBox tbox){
		super(tbox);		
	}


	/**
	 * Gets the Constants 'dr' associated to a Role
	 * 
	 * For each role R, two constants are created: dR (role domain) and dRInv (role range)
	 * 
	 * @param role
	 * @return
	 */
	public Formula getFormulaByRole(Role role){
		return RolesAndConstsMap.get(role);
	}

	/**
	 * Returns a K+ given an TBox T. 
	 * If factorize is set to true, the formula returned will
	 * have only 1 outermost universal quantifier, and the rest
	 * will be quantifier free. Moreover less box operators will be used
	 * 
	 * The result formula has the following structure
	 * F = T /\ e
	 * 
	 * where
	 * 
	 * T = /\ \forall x ( C1(x) -> C2(x) )  			(1)
	 * 		(For each concept inclusion in T*)
	 * 
	 * 	   /\ \forall x ( Eq'S(x) -> EqS(x) )  		(2)
	 * 		(for each q,q' \in Qrt with q <! q'. 
	 * 		 there is no q'' in Qrt with q < q'' < q')
	 * 
	 * 	   /\ \forall x ( EqS(x) -> EqS(x) )  (3)
	 * 		(for each global/ridig role s \in Qrt) 
	 * 
	 * e = /\ epsilon(s)    (For each role S) 				(4)
	 *    
	 *
	 * @param factorize
	 * @return
	 */
	public Formula getFormula(){
		Formula F;

		this.getFactorizedEpsilon();
		this.getExtendedFormula();

		F = new ConjunctiveFormula(this.getFactorizedT(), cardinalities);
		F = new UniversalFormula(
								new ConjunctiveFormula(F,
										new ConjunctiveFormula(epsX, 
															   rigidR)),
						 super.x); 
		return F;
	}

	/**
	 * Returns a K+ given an TBox T. 
	 * If factorize is set to true, the formula returned will
	 * have only 1 outermost universal quantifier, and the rest
	 * will be quantifier free. Moreover less box operators will be used
	 * 
	 * The result formula has the following structure
	 * F = T /\ e
	 * 
	 * where
	 * 
	 * T = /\ \forall x ( C1(x) -> C2(x) )  			(1)
	 * 		(For each concept inclusion in T*)
	 * 
	 * 	   /\ \forall x ( Eq'S(x) -> EqS(x) )  		(2)
	 * 		(for each q,q' \in Qrt with q <! q'. 
	 * 		 there is no q'' in Qrt with q < q'' < q')
	 * 
	 * 	   /\ \forall x ( EqS(x) -> EqS(x) )  (3)
	 * 		(for each global/ridig role s \in Qrt) 
	 * 
	 * e = /\ epsilon(s)    (For each role S) 				(4)
	 *    
	 *
	 * @param factorize
	 * @return
	 */
	public Formula getFormula(Set<String> unsatRoles){
		Formula F;

		this.getFactorizedEpsilon(unsatRoles);
		this.getExtendedFormula();

		F = new ConjunctiveFormula(this.getFactorizedT(), cardinalities);
		F = new UniversalFormula(
								new ConjunctiveFormula(F,
										new ConjunctiveFormula(epsX, 
															   rigidR)),
						 super.x); 
		return F;
	}

	/* 
	 *  \forall x ((E1R(x)-> E1Rinv(drinv)) (4)
	 *
	*/
	private void getFactorizedEpsilon(){

		for(Role s : super.tbox.getRoles()){

			if(s instanceof PositiveRole){
				
				//Proposition pS = (Proposition) super.a.get("P"+s.toString(),0);
				//Proposition pinvS = (Proposition) super.a.get("Pinv"+s.toString(),0);
				Role SInv = s.getInverse();
				Concept E1S = new QuantifiedRole(s, 1);
				Concept E1SInv = new QuantifiedRole(SInv, 1);

				Constant ds = new Constant("d"+s.toString());
				Constant dsinv = new Constant("d"+SInv.toString());

				Formula fE1S_ds = conceptToFormula(E1S);
				fE1S_ds.substitute(super.x, ds);

				Formula fE1SInv_ds = conceptToFormula(E1SInv);
				fE1SInv_ds.substitute(super.x, dsinv);

				RolesAndConstsMap.putIfAbsent(s, new ConjunctiveFormula(fE1S_ds, fE1SInv_ds));
	
				epsX.add(new ImplicationFormula(
											conceptToFormula(E1S),
											fE1SInv_ds)
						);

				epsX.add(new ImplicationFormula(
											conceptToFormula(E1SInv),
											fE1S_ds)
						);

			}
		}
	}

	/*  
	 * It removes \forall x ((E1R(x)-> E1Rinv(drinv)) (4) for SAT roles
	 * and adds \forall x ((\not E1R(x) && not E1Rinv(x)) for UNSAT roles
	 *
	*/
	private void getFactorizedEpsilon(Set<String> unsatRoles){

		epsX = new ConjunctiveFormula();

		if (!unsatRoles.isEmpty()){

			for(Role s : super.tbox.getRoles()){

				if(s instanceof PositiveRole){

					if (unsatRoles.contains(s.toString())) {
				
						Role SInv = s.getInverse();
						Concept E1S = new QuantifiedRole(s, 1);
						Concept E1SInv = new QuantifiedRole(SInv, 1);
	
						epsX.add(new ConjunctiveFormula(
											new NegatedFormula(conceptToFormula(E1S)),
											new NegatedFormula(conceptToFormula(E1SInv)))
							);

					}
				}
			}
		}
	}

	/***
	 * Reduction to QTL1
	 * Assert cardinality axioms (2)
	 * 
	 *     /\ \forall x ( Eq'R(x) -> EqR(x) )  		(2)
	 * 		(for each q,q' \in Qrt with q <! q'. 
	 * 		 there is no q'' in Qrt with q < q'' < q')
	 * 
	 */
	public void getExtendedFormula(){
		Set<QuantifiedRole> qRoles= super.tbox.getQuantifiedRoles1();
		
		/* 
		 * (2)
		 */
		Map<Role,List<QuantifiedRole>> qRMap = new HashMap<Role, List<QuantifiedRole>>();

		for(QuantifiedRole qR : qRoles){

			List<QuantifiedRole> list = qRMap.get(qR.getRole());

			if(list == null){
				list = new ArrayList<QuantifiedRole>();
				qRMap.put(qR.getRole(),list);
			}
			list.add(qR);
		}
		
		for(Entry<Role, List<QuantifiedRole>> e: qRMap.entrySet()){
			List<QuantifiedRole> qrL = e.getValue();

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
			}
		}
		
		/* 
		 * (3)
		 * @TODO: Avoid duplications
		 */

/*		Set<Role> roles = super.tbox.getRoles();

		for(QuantifiedRole qR : qRoles){
			if(qR.getRole().getRefersTo() instanceof AtomicRigidRole){
				roles.remove(qR.getRole());

				rigidR.add(new ImplicationFormula(
							conceptToFormula(qR), 
							conceptToFormula(qR)
							)
						);   
			}
		} 
		
		for (Role role : roles) {
			if(role.getRefersTo() instanceof AtomicRigidRole){
				QuantifiedRole qRoleRem = new QuantifiedRole(role, 1);

				rigidR.add(new ImplicationFormula(
								conceptToFormula(qRoleRem), 
								conceptToFormula(qRoleRem)
								)
							);
			}				
		} */
		
	}
	
	private Formula getFactorizedT(){
		/*
		 * Returns \box \bigwedge c1->c2
		 * If we put the outer always here, we left out of the scope to epsilon(x) formulas
		 */
		
		ConjunctiveFormula out = new ConjunctiveFormula();
		for(ConceptInclusionAssertion ci: super.tbox){
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


	/**
	 * Converting TDLLite concetps into QTL1 formulas
	 * 
	 * @param c a TDLlite Concept
	 * @return a QTL1 formula
	 */
	public Formula conceptToFormula(Concept c){
		if(c instanceof AtomicConcept)
			return new Atom(c.toString(), super.x);
		else if(c instanceof QuantifiedRole){
			QuantifiedRole qE = (QuantifiedRole)c;
			Atom atom = super.a.get("E"+qE.getQ()+qE.getRole().toString(),1);
			atom.setArg(0, super.x);
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
	
	
}
