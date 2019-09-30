package it.unibz.inf.tdllitefpx.tbox;

import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRole;

import java.util.HashMap;
import java.util.Map;

public class Signature {
	Map<String, AtomicRole> atomicRoles = new HashMap<String, AtomicRole>();
	Map<String, AtomicConcept> atomicConcepts = new HashMap<String, AtomicConcept>();
	
	
	/**
	 * Returns the role if defined in the signature, null otherwise
	 * @param name
	 * @return
	 */
	public AtomicRole getRole(String name){
		return atomicRoles.get(name);
	}
	
	public AtomicLocalRole getLocalRole(String name){
		AtomicLocalRole r= (AtomicLocalRole) atomicRoles.get(name);
		if(r==null){
			r = new AtomicLocalRole(name);
			atomicRoles.put(name,r);
		}
		return r;
	}
	
	public AtomicRigidRole getRigidRole(String name){
		AtomicRigidRole r= (AtomicRigidRole) atomicRoles.get(name);
		if(r==null){
			r = new AtomicRigidRole(name);
			atomicRoles.put(name,r);
		}
		return r;
	}
	
}
