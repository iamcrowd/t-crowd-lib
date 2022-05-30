package it.unibz.inf.qtl1.formulae;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.atoms.Top;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***
 * Represent a generalized conjunction.
 * 
 * @author Marco Gario
 *
 */
public class ConjunctiveFormula extends Formula {
	
	/*SortedSet<Formula> subFormulas = new TreeSet<Formula>(new Comparator<Formula>() {

		@Override
		public int compare(Formula o1, Formula o2) {
			return o1.toString().compareTo(o2.toString());
		}
	});
	*/
	List<Formula> subFormulas = new ArrayList<Formula>();

	
	public ConjunctiveFormula(){
		
	}
	public ConjunctiveFormula(Formula f1,Formula f2){
		if(f1 instanceof Atom && cloneAtoms)
			f1=(Atom)((Atom)f1).clone();
		if(f2 instanceof Atom && cloneAtoms)
			f2=(Atom)((Atom)f2).clone();
		
		addConjunct(f1);
		addConjunct(f2);
	}
	/***
	 * Adds a conjunct to the formula, flattening the structure if the conjunct is a 
	 * conjunctive formula (ie. (a & (b & a)) => ( a & b & a)). 
	 * Duplicates are mantained if Formula.removeDuplicates = false (default)
	 * @param f1
	 */
	public void addConjunct(Formula f1) {
		addConjunct(f1,Formula.verifyTopBot);
	}
	
	private void addConjunct(Formula  f1,boolean verifyTop){
		//TODO: Check wellformness
		f1=f1.removeEncapsulation();
		if( !verifyTop || !f1.isTop()){
			if(f1 instanceof ConjunctiveFormula){
				if(!Formula.removeDuplicates){
					subFormulas.addAll(f1.getSubFormulae());
				}else{
					subFormulas.addAll(new HashSet<Formula>(f1.getSubFormulae()));
				}
			}else if(f1 instanceof Atom){
				if(!(Formula.removeDuplicates && subFormulas.contains(f1))){
					f1=(Atom)((Atom)f1).clone();
					subFormulas.add(f1);
				}
			}
			else
				if(!(Formula.removeDuplicates && subFormulas.contains(f1))){
					subFormulas.add(f1);
				}
		}
	}
	
	public void add(Formula f){
		addConjunct(f);
	}
	public List<Formula> getSubFormulae(){
		if(returnRealSubformulae)
			return subFormulas;
		
		List<Formula> l=  new ArrayList<Formula>(subFormulas);
		return l;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof ConjunctiveFormula){
			ConjunctiveFormula o = (ConjunctiveFormula) obj;
			boolean result=true;
			if(o.getSubFormulae().size()!=this.subFormulas.size())
				return false;
			for(Formula f: this.subFormulas)
				result= result & o.getSubFormulae().contains(f);
			//TODO: Not strictly necessary since we have the check on size
			for(Formula f: o.getSubFormulae())
				result= result & this.subFormulas.contains(f);
			return result;
		}
		return false;
	}
	
	public int getArity(){ return subFormulas.size(); }
	public Set<Constant> getConstants(){
		HashSet<Constant> set=new HashSet<Constant>();
		for(Formula f : subFormulas)
			set.addAll(f.getConstants());
		return set;
	}
	
	public String toString(){
		String s="(";
		for(Formula f: subFormulas)
			s+=f+" & ";
		if(subFormulas.size()!=0)
			s=s.substring(0, s.length()-3);
		else
			s+=(new Top());
		s+=")";
		return s;
	}

	@Override
	public Set<Variable> removeUniversals() {
		Set<Variable> set = new HashSet<Variable>();
		for(Formula f: getSubFormulae()){
			set.addAll(f.removeUniversals());
			if(f instanceof UniversalFormula){
				UniversalFormula u = (UniversalFormula) f;
				set.add(u.getQuantifiedVar());
				subFormulas.remove(u);
				f=u.getSubFormulae().get(0);
				if(f instanceof ConjunctiveFormula)
					subFormulas.addAll(f.getSubFormulae());
				else
					subFormulas.add(f);
			}
		}
		return set;
	}
	
	public Object clone(){
		ConjunctiveFormula nf = new ConjunctiveFormula();
		for(Formula f:getSubFormulae())
			nf.addConjunct((Formula)f.clone(),false);
		return nf;
	}
	
	@Override
	public void atomsToPropositions() throws NotGroundException {
		for(Formula f: getSubFormulae()){
			if(f instanceof Atom){
				Atom a = (Atom)f;
				if(a.getArity()!=0){
					subFormulas.remove(a);
					subFormulas.add(a.atomToProposition());
				}
			}
			f.atomsToPropositions();
		}
	}
	@Override
	public void replaceSubFormula(Formula former, Formula current) {
		if(current instanceof ConjunctiveFormula ||
				current instanceof DisjunctiveFormula)
			if(current.getSubFormulae().size()==1)
				current=current.getSubFormulae().get(0);
		if(subFormulas.contains(former)){
			subFormulas.remove(former);
			this.addConjunct(current);
		}
	}
	
	public static Formula top(Proposition p){
		return new NegatedFormula(ConjunctiveFormula.bot(p));
	}
	
	public static Formula bot(Proposition p){
		return new ConjunctiveFormula( 
				p,
				new NegatedFormula(p));
	}
	@Override
	public boolean isBot() {
		//TODO Consider other cases....

		boolean bot=false;
		
		for(Formula f:getSubFormulae())
			if(f.isBot())
				bot=true;
		if(!bot){
			// No subformula is explicitely \bot. Let's verify if a & !a
			for(Formula fn:getSubFormulae()){
				if(fn instanceof NegatedFormula){
					for(Formula fp : getSubFormulae())
						if(fp.equals(fn.getSubFormulae().get(0))){
							return true;
						}
				}
			}
		}
		
		return bot;
		
	}
	@Override
	public boolean isTop() {
		//TODO Consider other cases....

		for(Formula f:getSubFormulae())
			if(!f.isTop()){
				return false;
			}
		return true;
	}
	
	@Override
	public Formula negateToNNF() {
		DisjunctiveFormula df = new DisjunctiveFormula();
		for(Formula f: subFormulas){
			df.addDisjunct(f.negateToNNF());
		}
		df.isNNF=true;
		return df;
	}
	
	@Override
	public Formula toNNF() {
		if(!this.isNNF){
			ConjunctiveFormula cf = new ConjunctiveFormula();
			for(Formula f: subFormulas){
				cf.addConjunct(f.toNNF());
			}
			cf.isNNF=true;
			return cf;
		}else{
			return this;
		}
	
	}
	
	@Override
	@Deprecated
	public Formula convertToNNF() {
		if(!this.isNNF){
			for(int i=0;i<subFormulas.size();i++){
				if(!subFormulas.get(i).isNNF){
					subFormulas.set(i,subFormulas.get(i).toNNF());
				}
			}
			this.isNNF=true;
			return this;
		}else
			return this;
	}
	
	@Override
	public Formula distribute() {
		/* Since disjunctions are not nested, there are two cases:
		 * - There are only literals (positive/negative atoms) = Nothing to do
		 * - There's at least one disjunction = distribute
		 */
		if(this.hasOnlyLiterals())
			return this;
		else{
			ConjunctiveFormula cf= new ConjunctiveFormula();
			for(Formula f: subFormulas){
				if(f.isLiteral())
					cf.add(f);
				else if(f instanceof DisjunctiveFormula){
					//System.err.println("distribute IN depth "+CNFFormula.cnt++);
					cf.add(f.distribute());
					//System.err.println("distribute OUT depth "+CNFFormula.cnt--);
				}else
					System.err.println("Distribute unimplemented for conj-"+f.getClass().toString());
			}
			Set<ConjunctiveFormula> set = new HashSet<ConjunctiveFormula>();
			set.add(new ConjunctiveFormula());
			for(Formula f: cf.subFormulas){
				if(f.isLiteral()){
					this.addToAll(set,f);
				}else{
					Set<ConjunctiveFormula> new_set = new HashSet<ConjunctiveFormula>();
					//System.err.println("depth "+CNFFormula.cnt +" building "+ f.getSubFormulae().size()+" copies");
					for(Formula sf : f.getSubFormulae()){
						Set<ConjunctiveFormula> copy = new HashSet<ConjunctiveFormula>();
						for(Formula elem:set) copy.add((ConjunctiveFormula) elem.clone());
						this.addToAll(copy, sf);
						new_set.addAll(copy);
					}
					set=new_set;
				}
			}
			DisjunctiveFormula df = new DisjunctiveFormula();
			for(Formula f: set){
				df.add(f);
			}
			return df;
		}
	}

	
	
	private void addToAll(Set<ConjunctiveFormula> set, Formula conjunct){
		for(ConjunctiveFormula cf: set)
			cf.addConjunct(conjunct);
	}
	
		
	public boolean hasOnlyLiterals(){
		for(Formula sub: this.subFormulas)
			if(!sub.isLiteral()){
				System.err.println("Is not a literal:"+sub);
				return false;
			}
		return true;
	}
}
