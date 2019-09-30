package it.unibz.inf.qtl1.formulae;

import java.util.List;
import java.util.Map;

import it.unibz.inf.qtl1.atoms.Proposition;

@Deprecated
public class CNFFormula extends ConjunctiveFormula {

	public String getStats() {
		StringBuilder sb =new StringBuilder();
		
		sb.append("Is CNF: "+isValidCNF()+"\n");
		sb.append("Clauses: "+this.subFormulas.size()+"\n");
		sb.append("Propositions: "+this.getPropositions().size()+"\n");
		sb.append("Biggest clause: "+ this.biggestClauseSize());
		return sb.toString();
	}
	
	public int biggestClauseSize() {
		boolean oldReal = Formula.returnRealSubformulae;
		Formula.returnRealSubformulae=true;
		
		int max = biggestClauseSize_();
		
		Formula.returnRealSubformulae=oldReal;
		return max;
		
	}
	public int biggestClauseSize_() {
		int max=0;
		
		for(Formula f: subFormulas)
			if(f.getSubFormulae().size()>max) max =f.getSubFormulae().size();
		
		return max;
	}

	
	public CNFFormula(Formula f,boolean equisat){
		super();
		Formula fNNF = f.toNNF().removeEncapsulation();
	
		Map<String, Boolean> flags = Formula.getFlags();
		
		if(fNNF.isValidCNF())
			this.add(fNNF);
		else{
			if(equisat){
				this.buildEquiSatCNF(fNNF);
			}else
				this.add(buildCNF(fNNF));
		}
		
		Formula.setFlags(flags);
	}
	
	private void buildEquiSatCNF(Formula f) {
		if(f instanceof ConjunctiveFormula){
			for(Formula s: ((ConjunctiveFormula)f).subFormulas){
				if(s.isValidCNFClause())
					this.add(s);
				else
					this.tseitin(s);
			}
		}else
			this.tseitin(f);
	}
	
	private Proposition tseitin(Formula f){
		Formula.removeDuplicates= false;
		//Formula.removeDuplicates= true;
		Formula.returnRealSubformulae=true;
		Formula.cloneAtoms=false;
		Formula.verifyTopBot=false;
		
		
		if(f.isLiteral())
			return null;
		else{
			List<Formula> subs = f.getSubFormulae();
			for(int i=0;i<subs.size();i++){
				Proposition p = tseitin(subs.get(i));
				if(p!=null)
					subs.set(i, p);
			}
			
			Proposition clauseVar = getNewProposition();
			//BimplicationFormula b = new BimplicationFormula(f, clauseVar);
			this.addConjunct(tseitinAddEquivalence(clauseVar,f));
			//this.addConjunct(b);
			return clauseVar;
		}
	}
	
	private Formula tseitinAddEquivalence(Proposition clauseVar, Formula f) {
		if(f instanceof ConjunctiveFormula){
				//TODO: FIX
			return null;
		}
		else if (f instanceof DisjunctiveFormula){
			/* !Eq \/ f   /\
			 * Eq \/ !f(0) /\
			 * Eq \/ !f(1) ... 
			 */
			this.add(new DisjunctiveFormula(new NegatedFormula(clauseVar),f));
			for(Formula s:f.getSubFormulae())
				this.add(new DisjunctiveFormula(clauseVar,new NegatedFormula(s)));
			return null;
		}
		else{
			System.err.println("tseitinEquvalence not implemented for "+f.getClass().toString());
			return null;
		}
		
	}

	private int clauseCnt=0;
	private Proposition getNewProposition(){ return new Proposition(("Eq_Clause_"+this.clauseCnt++));}
	
	
	public CNFFormula(Formula f){
		this(f,false);
	}
	
	public ConjunctiveFormula buildCNF(Formula f){
			
		ConjunctiveFormula cf= new ConjunctiveFormula();

		Formula.removeDuplicates= true;
		Formula.removeDuplicates= false;
		Formula.returnRealSubformulae=true;
		Formula.cloneAtoms=false;
		
			
		if(f instanceof ConjunctiveFormula){
			for(Formula s:f.getSubFormulae()){
				if(s.isLiteral()) 
					cf.add(s);
				else{
					Formula sd = s.distribute();
					cf.add(sd);
				}
			}
		}else{
			cf.add(f.distribute());
		}

		return cf;	
		
	}
	

}
