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
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;

/**
 * 
 * @author gab
 *
 */
public class Strategy{
	
	TBox myTBox = new TBox();
	List<Concept> list_ac = new ArrayList<Concept>();
	List<Role> list_role = new ArrayList<Role>();
	
	public Strategy() {
		
	}
	
	public TBox getTBox() {
		return myTBox;
	}
	
	public List<Concept> getConceptList(){
		return this.list_ac;
	}
	
	public List<Role> getRoleList(){
		return this.list_role;
	}
	
	public boolean existsConcept(Concept ac) {
		return this.getConceptList().contains(ac);
	}
	
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
	
	public boolean existsRole(Role ro) {
		return this.getRoleList().contains(ro);
	}
	
/*	public Role getRoleByName(String nameRo) {
		this.getRoleList().iterator().forEachRemaining(element -> {
			if (nameRo.equals(element.toString())) {
				return element;
			}
		});		
	}*/
	

}
