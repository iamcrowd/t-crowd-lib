package it.unibz.inf.tdllitefpx.concepts;

import it.unibz.inf.tdllitefpx.roles.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gario.code.output.OutputFormat;
import org.gario.code.output.OutputSymbolType;
import org.gario.code.output.SymbolUndefinedException;

public class ConjunctiveConcept extends Concept {
	List<Concept> conjuncts = new ArrayList<Concept>();
	
	public ConjunctiveConcept(Concept c1, Concept c2){
		conjuncts.add(c1);
		conjuncts.add(c2);
	}
	
	public ConjunctiveConcept(){};
	
	public void add(Concept c){
		conjuncts.add(c);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Iterator<Concept> it = conjuncts.iterator();
		if(it.hasNext()){
			sb.append(" ( ");
			Concept c = it.next();
			sb.append(c.toString());
			while(it.hasNext()){
				sb.append(" /\\ ");
				sb.append(it.next().toString());
			}
			sb.append(" ) ");
		}
		return sb.toString();
			
	}

	public List<Concept> getConjuncts() {
		return conjuncts;
	}
	
	@Override
	public Set<Role> getRoles() {
		HashSet<Role> s = new HashSet<Role>();
		for(Concept c: conjuncts)
			s.addAll(c.getRoles());
		return s;
	}

	@Override
	public String toString(OutputFormat fmt) throws SymbolUndefinedException{
		StringBuilder sb = new StringBuilder();
		
		if(fmt.getSymbolPosition(this)==OutputSymbolType.PREFIX){
			if(fmt.hasParenthesis(this)){
				sb.append("(");
			}
			sb.append(fmt.getSymbol(this));
			for(Concept c: conjuncts) {
				sb.append(c.toString(fmt)+" ");
			}
			
			if(fmt.hasParenthesis(this)){
				sb.append(")");
			}
			
		}else if(fmt.getSymbolPosition(this)==OutputSymbolType.INFIX){

			if(fmt.hasParenthesis(this)){
				sb.append("(");
			}

			Iterator<Concept> it = conjuncts.iterator();
			if(it.hasNext()){
				sb.append(" ( ");
				Concept c = it.next();
				sb.append(c.toString(fmt));
				while(it.hasNext()){
					sb.append("  ");
					sb.append(fmt.getSymbol(this));
					sb.append("  ");
					sb.append(it.next().toString(fmt));
				}
				sb.append(" ) ");
			}
			
			if(fmt.hasParenthesis(this)){
				sb.append(")");
			}
		}
		return sb.toString();
	}
	
	@Override
	public Set<Concept> getBasicConcepts() {
		Set<Concept> res = null;
		for(Concept c: conjuncts){
			if(res==null)
				res=c.getBasicConcepts();
			else
				res.addAll(c.getBasicConcepts());
		}
		return res;
	}

	@Override
	public int hashCode() {
		return conjuncts.hashCode();
	}
}
