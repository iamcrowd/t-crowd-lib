package it.unibz.inf.qtl1.formulae;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import it.unibz.inf.qtl1.atoms.*;

/**
 * An alphabet is a collection of Atoms in use. Even though comparison of atoms is
 * performed by name, when dealing with big formulas, it is important to allocate
 * only once each propositional variable. The method get offers a ready to use wrapper
 * that returns an atom independently of whether it was already defined or not.
 * To increase flexibility is also possible to add atoms to the collection through
 * the add method.  
 * 
 * @author Marco Gario
 *
 */
public class Alphabet extends TreeMap<String,Atom> implements Iterable<Atom>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Alphabet(){
		super();
	}

	/**
	 * Adds the atom p to the collection
	 * @param p
	 */
	public void add(Atom p){
		this.put(p.toString(),p);
	}
	
	public Iterator<Atom> iterator(){
		return this.values().iterator();
	}
	
	public boolean containsAll(Collection<Atom> c){
		return this.values().containsAll(c);
	}

	public boolean contains(Atom p) {
		return this.containsKey(p.toString());
	}
	/**
	 * Returns the atom with matching name and arity if it exists 
	 * or a brand new atom if it didn't exist. Note that the brand new
	 * atom will have the anonymous variable _ as arguments.
	 * Returns null only if an atom with the same name and different
	 * arity is found.
	 *  
	 * @param name
	 * @param arity
	 * @return
	 */
	public Atom get(String name,int arity){
		Atom a = this.get(name);
		
		if(a!=null){
			if(a.getArity()==arity){
				return a;
			}else{
				System.err.println("An atom with different arity already exists!");
				return null;
			}
		}else{
			if(arity==0)
				a= new Proposition(name);
			else
				a = new Atom(name,arity);
			
			this.add(a);
			return a;
		}
	}

	public void addAll(Collection<Atom> c) {
		for(Atom p:c){
			this.add(p);
		}
		
	}
}
