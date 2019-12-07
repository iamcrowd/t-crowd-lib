package it.gilia.tcrowd.encoding;

/**
 * Define a strategy for encoding ERvt models into Temporal DL.
 * 
 * @see Read paper "A Cookbook for Temporal Conceptual Data Modelling" (Artale et.al) for more details about formalisation. 
 * 
 */

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONException;

import java.util.*;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.BottomConcept;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.concepts.NegatedConcept;
import it.unibz.inf.tdllitefpx.concepts.QuantifiedRole;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.AlwaysPast;
import it.unibz.inf.tdllitefpx.concepts.temporal.NextFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimeFuture;
import it.unibz.inf.tdllitefpx.concepts.temporal.SometimePast;
import it.unibz.inf.tdllitefpx.roles.AtomicLocalRole;
import it.unibz.inf.tdllitefpx.roles.AtomicRigidRole;
import it.unibz.inf.tdllitefpx.roles.PositiveRole;
import it.unibz.inf.tdllitefpx.roles.Role;
import it.unibz.inf.tdllitefpx.roles.AtomicRole;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;

/**
 * 
 * @author gab
 *
 */
public class Strategy{
	
	TBox myTBox = new TBox();
	ABox myABox = new ABox();
	List<Concept> list_ac = new ArrayList<Concept>();
	List<Concept> list_domains = new ArrayList<Concept>();
	List<Role> list_role = new ArrayList<Role>();
	
	public Strategy() {
		
	}
	
	/**
	 * 
	 * @return a TBox object
	 */
	public TBox getTBox() {
		return myTBox;
	}
	
	/**
	 * 
	 * @return a ABox object
	 */
	public ABox getABox() {
		return myABox;
	}
	
	/**
	 * 
	 * @return a list of atomic concepts
	 */
	public List<Concept> getConceptList(){
		return this.list_ac;
	}
	
	/**
	 * 
	 * @return a list of atomic domain concepts (Integer, String, Boolean, Date)
	 */
	public List<Concept> getConceptDomainList(){
		return this.list_domains;
	}
	
	/**
	 * 
	 * @return a list of atomic local roles
	 */
	public List<Role> getRoleList(){
		return this.list_role;
	}
	
	/**
	 * 
	 * @param ac an object representing an atomic concept
	 * @return true if the atomic concept given as parameter has been already asserted in the current KB
	 */
	public boolean existsConcept(Concept ac) {
		return this.getConceptList().contains(ac);
	}
	
	/**
	 * 
	 * @param nameAc an atomic concept name
	 * @return the concept list index if nameAc is the name of an existing atomic concept. Otherwise, it returns -1.
	 */
	public int getConceptByNameIndex(String nameAc) {
		List<Concept> list = this.getConceptList();
		int i = 0;
		int index = -1;
		while (i < list.size()) {
			if (nameAc.equals(list.get(i).toString())) {
				index = i;
			}
			i++;
		}
		return index;
	}
	
	/**
	 * 
	 * @param name a String atomic concept name
	 * @return a new object representing an atomic concept with name.
	 */
	public Concept giveMeAconcept(String name) {
		int exists = this.getConceptByNameIndex(name);
		if (exists != -1) {
			return this.getConceptList().get(exists);
		}
		else {
			Concept acpt = new AtomicConcept(name);
			this.list_ac.add(acpt);
			return acpt;
		}
	}
	
	/**
	 * 
	 * @param ac an object representing an atomic domain concept
	 * @return true if the atomic domain concept given as parameter has been already asserted in the current KB
	 */
	public boolean existsConceptDomain(Concept ac) {
		return this.getConceptDomainList().contains(ac);
	}
	
	/**
	 * 
	 * @param nameAc an atomic domain concept name
	 * @return the domain concept list index if nameAc is the name of an existing concept. Otherwise, it returns -1.
	 */
	public int getConceptDomainByNameIndex(String nameAc) {
		List<Concept> list = this.getConceptDomainList();
		int i = 0;
		int index = -1;
		while (i < list.size()) {
			if (nameAc.equals(list.get(i).toString())) {
				index = i;
			}
			i++;
		}
		return index;
	}
	
	/**
	 * 
	 * @param name a String atomic domain concept name
	 * @return a new object representing an atomic domain concept with name.
	 */
	public Concept giveMeAdomainConcept(String name) {
		int exists = this.getConceptDomainByNameIndex(name);
		if (exists != -1) {
			return this.getConceptDomainList().get(exists);
		}
		else {
			Concept acpt = new AtomicConcept(name);
			this.list_domains.add(acpt);
			return acpt;
		}
	}
	
	/**
	 * 
	 * @param ro an object representing an atomic local role
	 * @return true if the atomic local role given as parameter has been already asserted in the current KB
	 */
	public boolean existsRole(Role ro) {
		return this.getRoleList().contains(ro);
	}
	
	/**
	 * 
	 * @param nameRo an atomic local role name
	 * @return the local role list index if nameRo is the name of an existing role. Otherwise, it returns -1.
	 */
	public int getRoleByNameIndex(String nameRo) {
		List<Role> list = this.getRoleList();
		int i = 0;
		int index = -1;
		while (i < list.size()) {
			if (nameRo.equals(list.get(i).toString())) {
				index = i;
			}
			i++;
		}
		return index;
	}
	
	/**
	 * 
	 * @param name a String atomic local role name
	 * @return a new object representing an atomic local role with name.
	 */
	public Role giveMeArole(String name) {
		int exists = this.getRoleByNameIndex(name);
		if (exists != -1) {
			return this.list_role.get(exists);
		}
		else {
			Role ro = new PositiveRole(new AtomicLocalRole(name));
			this.list_role.add(ro);
			return ro;
		}
	}
	
	/**
	 * 
	 * @param name a String atomic rigid role name
	 * @return a new object representing an atomic rigid role with name.
	 */
	public Role giveMeArigidRole(String name) {
		int exists = this.getRoleByNameIndex(name);
		if (exists != -1) {
			return this.list_role.get(exists);
		}
		else {
			Role ro = new PositiveRole(new AtomicRigidRole(name));
			this.list_role.add(ro);
			return ro;
		}
	}
	
}
