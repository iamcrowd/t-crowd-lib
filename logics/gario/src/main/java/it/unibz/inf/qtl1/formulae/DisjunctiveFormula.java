package it.unibz.inf.qtl1.formulae;

import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***
 * Represent a generalized disjunction. 
 * 
 * @author Marco Gario
 *
 */
public class DisjunctiveFormula extends Formula{

	List<Formula> subFormulas = new ArrayList<Formula>();
	
	public DisjunctiveFormula(){}
	public DisjunctiveFormula(Formula f1,Formula f2){
		if(f1 instanceof Atom && cloneAtoms)
			f1=(Atom)((Atom)f1).clone();
		if(f2 instanceof Atom && cloneAtoms)
			f2=(Atom)((Atom)f2).clone();
		
		
		addDisjunct(f1);
		addDisjunct(f2);
	}
	
	public void addDisjunct(Formula f1){ addDisjunct(f1, verifyTopBot);}
	
	private void addDisjunct(Formula f1,boolean verifyBot){
		//TODO: Check wellformness
		f1=f1.removeEncapsulation();
		if(!verifyBot || !f1.isBot()){
			if(f1 instanceof DisjunctiveFormula){
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
			}else
				if(!(Formula.removeDuplicates && subFormulas.contains(f1))){
					subFormulas.add(f1);
				}
		}
	}
	public void add(Formula f){
		addDisjunct(f);
	}
	public List<Formula> getSubFormulae(){
		if(returnRealSubformulae)
			return subFormulas;
			
		List<Formula> l=  new ArrayList<Formula>(subFormulas);
		return l;
			
	}
	
	public List<Formula> getDirectSubFormulae(){
		return subFormulas;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof DisjunctiveFormula){
			DisjunctiveFormula o = (DisjunctiveFormula) obj;
			boolean result=true;
			if(o.getSubFormulae().size()!=this.subFormulas.size())
				return false;
			for(Formula f: this.subFormulas)
				result= result & o.getSubFormulae().contains(f);
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
			s+=f+" | ";
		if(subFormulas.size()==0)
			s+="bot";
		else
			s=s.substring(0, s.length()-3);
		s+=")";
		return s;
	}
	
	public Set<Variable> removeUniversals() {
		Set<Variable> set = new HashSet<Variable>();
		for(Formula f: getSubFormulae()){
			set.addAll(f.removeUniversals());
			if(f instanceof UniversalFormula){
				UniversalFormula u = (UniversalFormula) f;
				set.add(u.getQuantifiedVar());
				subFormulas.remove(u);
				f=u.getSubFormulae().get(0);
				if(f instanceof DisjunctiveFormula)
					subFormulas.addAll(f.getSubFormulae());
				else
					subFormulas.add(f);
			}
		}
		return set;
	}
	public Object clone(){
		DisjunctiveFormula nf = new DisjunctiveFormula();
		for(Formula f:getSubFormulae())
			nf.addDisjunct((Formula)f.clone(),false);
		return nf;
	}
	
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

	public void replaceSubFormula(Formula former, Formula current) {
		if(current instanceof ConjunctiveFormula ||
				current instanceof DisjunctiveFormula)
			if(current.getSubFormulae().size()==1)
				current=current.getSubFormulae().get(0);
		if(subFormulas.contains(former)){
			subFormulas.remove(former);
			this.addDisjunct(current);
		}
		
	}
	@Override
	public boolean isBot() {
		//TODO Consider other cases....
	
		for(Formula f:getSubFormulae())
			if(!f.isBot()){
				return false;
			}

		return true;
	}
	@Override
	public boolean isTop() {
		//TODO Consider other cases....
	
		boolean top=false;
		for(Formula f:getSubFormulae())
			if(f.isTop())
				top=true;
		
		if(!top){
			// No subformula is explicitely \top. Let's verify if (a | !a)
			for(Formula fn:getSubFormulae()){
				if(fn instanceof NegatedFormula){
					for(Formula fp : getSubFormulae())
						if(fp.equals(fn.getSubFormulae().get(0))){
							return true;
						}
							
				}
			}
		}
		return top;
	}

	@Override
	public Formula negateToNNF() {
		ConjunctiveFormula cf = new ConjunctiveFormula();
		for(Formula f: subFormulas){
			cf.addConjunct(f.negateToNNF());
		}
		cf.isNNF=true;
		return cf;
	}
	@Override
	public Formula toNNF() {
		if(!this.isNNF){
			DisjunctiveFormula df = new DisjunctiveFormula();
			for(Formula f: subFormulas){
				df.addDisjunct(f.toNNF());
			}
			df.isNNF=true;
			return df;
		}else
			return this;
	}
	
	@Override
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
		
		if(this.hasOnlyLiterals())
			return this;
		else{
			DisjunctiveFormula df= new DisjunctiveFormula();
			for(Formula f: subFormulas){
				if(f.isLiteral())
					df.add(f);
				else if(f instanceof ConjunctiveFormula){
					//System.err.println("distribute depth "+CNFFormula.cnt++);
					df.add(f.distribute());
					//System.err.println("distribute OUT depth "+CNFFormula.cnt--);
				}else
					System.err.println("Distribute unimplemented for disj-"+f.getClass().toString());
			}
			Set<DisjunctiveFormula> set = new HashSet<DisjunctiveFormula>();
			set.add(new DisjunctiveFormula());
			for(Formula f: df.subFormulas){
				if(f.isLiteral()){
					this.addToAll(set,f);
				}else{
					Set<DisjunctiveFormula> new_set = new HashSet<DisjunctiveFormula>();
					//System.err.println("depth "+CNFFormula.cnt +" building "+ f.getSubFormulae().size()+" copies");
					for(Formula sf : f.getSubFormulae()){
						Set<DisjunctiveFormula> copy = new HashSet<DisjunctiveFormula>();
						for(Formula elem:set) copy.add((DisjunctiveFormula) elem.clone());
						this.addToAll(copy, sf);
						new_set.addAll(copy);
					}
					set=new_set;
				}
			}
			ConjunctiveFormula cf = new ConjunctiveFormula();
			for(Formula f: set){
				cf.add(f);
			}
			return cf;
		}
	}
	
	private void addToAll(Set<DisjunctiveFormula> set, Formula disjunct){
		for(DisjunctiveFormula  df: set)
			df.add(disjunct);
	}
	
	public boolean hasOnlyLiterals(){
		for(Formula sub: this.subFormulas)
			if(!sub.isLiteral())
				return false;
		return true;
	}

}
