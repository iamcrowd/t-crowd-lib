package it.unibz.inf.qtl1;

import java.util.LinkedList;
import java.util.List;

import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.Alphabet;
import it.unibz.inf.qtl1.formulae.BimplicationFormula;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.DisjunctiveFormula;
import it.unibz.inf.qtl1.formulae.ExistentialFormulaException;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.ImplicationFormula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.Always;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysPast;
import it.unibz.inf.qtl1.formulae.temporal.NextFuture;
import it.unibz.inf.qtl1.formulae.temporal.NextPast;
import it.unibz.inf.qtl1.formulae.temporal.Sometime;
import it.unibz.inf.qtl1.formulae.temporal.SometimeFuture;
import it.unibz.inf.qtl1.formulae.temporal.SometimePast;
import it.unibz.inf.qtl1.formulae.temporal.TemporalFormula;
import it.unibz.inf.qtl1.terms.Variable;


public class NaturalTranslator {
	Alphabet a;
	int modalCnt=0;
	Formula original;
	Formula translation=null;
	Variable x = new Variable("x");
	static final String past_post = "P";
	static final String future_post = "F";
	
	List<NextFuture> nxtF = new LinkedList<NextFuture>();
	List<NextPast> nxtP = new LinkedList<NextPast>();
	List<AlwaysFuture> boxF = new LinkedList<AlwaysFuture>();
	List<AlwaysPast> boxP = new LinkedList<AlwaysPast>();
	List<SometimeFuture> diaF = new LinkedList<SometimeFuture>();
	List<SometimePast> diaP = new LinkedList<SometimePast>();
	List<Atom> atoms = new LinkedList<Atom>();
	
	
	/**
	 * The constructor performs basic checks to verify that the translation
	 * is syntactically possible (Existential, universal operators, sentence
	 * and one variable).
	 * Moreover it remove the abbreviations used in the formula (Always,Sometime).
	 * In this sense the constructor "modifies" the original formula.
	 * 
	 * @param f
	 * @throws Exception
	 */
	public NaturalTranslator(Formula f) throws Exception{
		this.original = f;
		//Existential
		if(original.hasExistential())
			throw new Exception("The translation is not defined for existential quantifiers");

		//Sentence
		if(!original.isSentence())
			throw new Exception("The formula should be a sentence");

		//One Variable
		if(!original.isOneVariable())
			throw new Exception("The formula should be one variable");

		//Universal
		// Discard the outermost Quantifier 
		if(original instanceof UniversalFormula)
				original=original.getSubFormulae().get(0);
		if(original.hasUniversal())
				throw new Exception("The translation is not defined for universal quantifiers");
		
		a= new Alphabet();
		removeAbbr();
		findSubformulae(original);

	}

	/**
	 * Returns the original formula after applying some preprocessing.
	 * The formula is equivalent to the original one, but not necessarily 
	 * the same.
	 * @return
	 */
	public Formula getOriginalFormula(){ return original;}
	
	/**
	 * Remove the abbreviations like Sometime and Always from the
	 * formula. 
	 */
	private void removeAbbr(){
		original = _removeAbbr(original);
	}
	
	private Formula _removeAbbr(Formula f){
		if(f instanceof Sometime)
			return _removeAbbr(((Sometime)f).normalize());
		else if (f instanceof Always)
			return _removeAbbr(((Always)f).normalize());
		else{
			Formula original[] = new Formula[f.getSubFormulae().size()];
			Formula subs[] = new Formula[f.getSubFormulae().size()];
			int i=0;
			for(Formula s:f.getSubFormulae()){
				Formula n = _removeAbbr(s);
				if(n!=s){
					original[i]=s;
					subs[i]=n;
					i++;
				}
			}
			while((--i)>=0)
				f.replaceSubFormula(original[i], subs[i]);
			return f;
		}
	}
	
	/**
	 * The main method of the class that returns the translation over natural
	 * of the original formula.
	 * 
	 * @return
	 */
	public  Formula getTranslation() {
		if(translation==null){
			ConjunctiveFormula cf = new ConjunctiveFormula(); 
			//Translate
			/*
			 * The translation can be divided into 3 parts:
			 * - ReFormulation
			 * - Propositional equivalence
			 * - Modal equivalence
			 */
			cf.add(getReformulation());
			cf.add(getModalEquivalences());
			cf.add(getAtomicEquivalences());
			
			translation = new UniversalFormula(cf, x);
		}
		return translation;
	}

	private void findSubformulae(Formula f){
		
		if(f instanceof NextFuture)
			nxtF.add((NextFuture) f);
		if(f instanceof NextPast)
			nxtP.add((NextPast) f);
		if(f instanceof SometimeFuture)
			diaF.add((SometimeFuture) f);
		if(f instanceof SometimePast)
			diaP.add((SometimePast) f);
		if(f instanceof AlwaysFuture)
			boxF.add((AlwaysFuture) f);
		if(f instanceof AlwaysPast)
			boxP.add((AlwaysPast) f);
		if(f instanceof Atom)
			atoms.add((Atom)f);
			
		for(Formula s:f.getSubFormulae()){
			findSubformulae(s);
		}
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
		 * Each group is then divided into two blocks, one for
		 * the Past and one for the Future.
		 * 
		 */
		
		ConjunctiveFormula cf = new ConjunctiveFormula();
		ConjunctiveFormula cF ;
		ConjunctiveFormula cP ;
		// Next
		// 1.
		// nxt box ( A /\ B)
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(NextFuture s : nxtF){
			cF.add(new BimplicationFormula(
					tr(s, true),
					new NextPast(tr(s.getSubFormulae().get(0),true))
					));
		}
		
		for(NextPast s : nxtP){
			cP.add(new BimplicationFormula(
					tr(s, false),
					new NextPast(tr(s.getSubFormulae().get(0),false))
					));
		}
		if(cF.getSubFormulae().size()>0 || cP.getSubFormulae().size()>0)
			cf.add(new NextFuture(new AlwaysFuture(
				new ConjunctiveFormula(cF,cP))));
		
		// 2.
		// box ( A /\ B)
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(NextFuture s : nxtF){
			cF.add(new BimplicationFormula(
					tr(s, false),
					new NextFuture(tr(s.getSubFormulae().get(0),false))
					));
		}
		
		for(NextPast s : nxtP){
			cP.add(new BimplicationFormula(
					tr(s, true),
					new NextFuture(tr(s.getSubFormulae().get(0),true))
					));
		}
		if(cF.getSubFormulae().size()>0 || cP.getSubFormulae().size()>0)
			cf.add(new AlwaysFuture(new ConjunctiveFormula(cF,cP)));
		
		// Sometime
		// 1.
		// nxt box ( A /\ B)
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(SometimeFuture s : diaF){
			cF.add(new BimplicationFormula(
					tr(s, true),
					new DisjunctiveFormula(
							new NextPast(tr(s,true)),
							tr(s.getSubFormulae().get(0),true)
					)));
		}
		
		for(SometimePast s : diaP){
			cP.add(new BimplicationFormula(
					tr(s, false),
					new DisjunctiveFormula(
							new NextPast(tr(s,false)),
							tr(s.getSubFormulae().get(0),false)
					)));
		}
		
		if(cF.getSubFormulae().size()>0 || cP.getSubFormulae().size()>0)
			cf.add(new NextFuture(new AlwaysFuture(
					new ConjunctiveFormula(cF,cP))));
		
		// 2.
		// box ( A /\ B) 
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(SometimeFuture s : diaF){
			cF.add(new BimplicationFormula(
					tr(s, false),
					new SometimeFuture(tr(s.getSubFormulae().get(0),false))
					));
		}
		
		for(SometimePast s : diaP){
			cP.add(new BimplicationFormula(
					tr(s, true),
					new SometimeFuture(tr(s.getSubFormulae().get(0),true))
					));
		}
		if(cF.getSubFormulae().size()>0 || cP.getSubFormulae().size()>0)
			cf.add(new AlwaysFuture(new ConjunctiveFormula(cF,cP)));
		
		
		// Always 
		// 1.
		// nxt box ( A /\ B)
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(AlwaysFuture s : boxF){
			cF.add(new BimplicationFormula(
					tr(s, true),
					new ConjunctiveFormula(
							new NextPast(tr(s,true)),
							tr(s.getSubFormulae().get(0),true)
					)));
		}
		
		for(AlwaysPast s : boxP){
			cP.add(new BimplicationFormula(
					tr(s, false),
					new ConjunctiveFormula(
							new NextPast(tr(s,false)),
							tr(s.getSubFormulae().get(0),false)
					)));
		}
		if(cF.getSubFormulae().size()>0 || cP.getSubFormulae().size()>0)
			cf.add(new NextFuture(new AlwaysFuture(
				new ConjunctiveFormula(cF,cP))));
		
		// 2.
		// box ( A /\ B) 
		cF = new ConjunctiveFormula();
		cP = new ConjunctiveFormula();
		
		for(AlwaysFuture s : boxF){
			cF.add(new BimplicationFormula(
					tr(s, false),
					new AlwaysFuture(tr(s.getSubFormulae().get(0),false))
					));
		}
		
		for(AlwaysPast s : boxP){
			cP.add(new BimplicationFormula(
					tr(s, true),
					new AlwaysFuture(tr(s.getSubFormulae().get(0),true))
					));
		}
		if(cF.getSubFormulae().size()>0 || cP.getSubFormulae().size()>0)
			cf.add(new AlwaysFuture(new ConjunctiveFormula(cF,cP)));
		
		return cf;
	}


	/**
	 * Atomic Equivalences are the equivalences between past and future
	 * relations (Line 2)
	 * @return
	 */
	private Formula getAtomicEquivalences() {
		// We Build an equality starting from the Future atoms
		ConjunctiveFormula cf = new ConjunctiveFormula();
		
		/* TODO: Since we have the atom list, we could build the +-
		 * atoms in the preprocessing and deal here only with the alphabet
		 */
		for(String s: a.keySet()){
			if(s.substring(s.length()-1).equals(future_post)){
				Atom future = a.get(s);
				Atom past = a.get(s.substring(0,s.length()-1)+past_post);
				cf.add(new BimplicationFormula(future,past));
			}
		}
		for(Atom a: atoms)
			cf.add(new BimplicationFormula(tr(a,false),tr(a,true)));
		return cf;
	}

	/**
	 * The reformulation is the translation of the original formula wrt to 
	 * the future (Line 1)
	 * 
	 * @return
	 */
	private Formula getReformulation() {
		return trFuture(original);
	}


	private Formula trFuture(Formula f){
		return tr(f,false);
	}
	
	private Formula trPast(Formula f){
		return tr(f,true);
	}
	
	/**
	 * Corresponds to the \xi operator. 
	 * The parameter "past" express whether we are talking about
	 * \xi + or - .
	 * @param f
	 * @param past
	 * @return
	 */
	private Formula tr(Formula f,boolean past){
		/*
		 * We return the translation of each subformula.
		 * Particular cases are Atoms and 
		 * modal/temporal formulas, that get translated into
		 * new propositional variables.
		 * 
		 * Rf(x)  	- R(x)
		 * Rf(c)	- R(c)
		 * ![a]  	- !a
		 * [a]o[b] 	- [a o b]
		 * Sf(x)	- [O f]  
		 */
		String post="";
		if(past)
			post=past_post;
		else
			post=future_post;
				
		if(f instanceof Atom){ 
			Atom fAtom = (Atom)f;
			Atom newAtom;
			// We return the requested atom, but we build both the future and the past
			
			newAtom = a.get(fAtom.getName()+future_post);
			if(newAtom==null){
				Atom newPAtom = a.get(fAtom.getName()+past_post,fAtom.getArity());
				Atom newFAtom = a.get(fAtom.getName()+future_post,fAtom.getArity());
				for(int i=0; i<fAtom.getArity(); i++){
					newPAtom.setArg(i, fAtom.getArg(i));
					newFAtom.setArg(i, fAtom.getArg(i));
				}
				if(past){
					newAtom=newPAtom;
				}else{
					newAtom=newFAtom;
				}
			}
			return newAtom;
		}else if(f instanceof TemporalFormula){
			Atom atom = a.get(f.toString()+post);
			if(atom==null){
				String name="";
				if(f instanceof AlwaysFuture || f instanceof AlwaysPast)
					name ="BOX";
				else if (f instanceof SometimeFuture || f instanceof SometimePast)
					name ="DIAMOND";
				else if (f instanceof NextFuture || f instanceof NextPast)
					name ="NXT";
				atom = new Atom(name+""+(modalCnt++)+post,x);
				a.put(f.toString()+post, atom);
				
			}
			return atom;
		}else if(f instanceof ConjunctiveFormula){
			ConjunctiveFormula cf = new ConjunctiveFormula();
			for(Formula s: f.getSubFormulae()){
				cf.add(tr(s,past));
			}
			return cf;
		}else if(f instanceof DisjunctiveFormula){
			DisjunctiveFormula cf = new DisjunctiveFormula();
			for(Formula s: f.getSubFormulae()){
				cf.add(tr(s,past));
			}
			return cf;
		}else if(f instanceof NegatedFormula){
			return new NegatedFormula(tr(f.getSubFormulae().get(0),past));
		}else if(f instanceof ImplicationFormula){
			return new ImplicationFormula(
							tr(f.getSubFormulae().get(0),past),
							tr(f.getSubFormulae().get(1),past));
		}else if(f instanceof BimplicationFormula){
			return new BimplicationFormula(
					tr(f.getSubFormulae().get(0),past),
					tr(f.getSubFormulae().get(1),past));
		}else{
			System.err.println("tr not implemented for " + f.getClass().toString());
			return null;
		}
	}
	@Deprecated
	public Formula getEntitySatisfiabilityConstraint(List<Atom> eAtoms){
		
		DisjunctiveFormula df = new DisjunctiveFormula();
		for(Atom a : eAtoms){
			df.add(trFuture(a));
			df.add(trPast(a));
		}
		
		Formula constraint=null;
		DisjunctiveFormula d = new DisjunctiveFormula();
		if(eAtoms.get(0)!=null){
			Variable x = (Variable) eAtoms.get(0).getArg(0);
			Formula f = new UniversalFormula(df, x);
			try {
				constraint= f.makeGround(translation.getConstants());
				constraint.atomsToPropositions();
				//Go from CNF to disjunction
				for(Formula conjunct :constraint.getSubFormulae())
					d.add(conjunct);
					
			} catch (ExistentialFormulaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new SometimeFuture(d);
	}
}
