package it.gilia.tcrowd.encoding;

/**
 * Define a strategy for encoding ERvt models into Temporal DL.
 * 
 * @see Read paper "A Cookbook for Temporal Conceptual Data Modelling" (Artale et.al) for more details about formalisation. 
 * 
 */

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

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


public class DefaultStrategy{
	
	TBox myTBox = new TBox();
	
	public DefaultStrategy() {
		
	}
	
	public TBox getTBox() {
		return myTBox;
	}
	
	/**
	 * 
	 * @param ervt_json A JSON object representing an ERvt temporal model only 
	 * containing entities.
	 */
	public TBox to_dllitefpx_entities(JSONObject ervt_json) {
		//https://www.mkyong.com/java/json-simple-example-read-and-write-json/		
		ervt_json.keys().forEachRemaining(key -> {
	        Object value = ervt_json.get(key);
	        JSONArray arr = ervt_json.getJSONArray(key);
	        System.out.println("Key: {0}..."+key);
	        arr.iterator().forEachRemaining(element -> {
	        	System.out.println("Element: {0}..."+element);
	        	Concept acpt = new AtomicConcept(element.toString());
	        	this.myTBox.add(new ConceptInclusionAssertion(acpt, new AtomicConcept("Top")));
	        });
	    });
		System.out.println("TBox stats..."+this.getTBox().getStats());
		return this.getTBox();
	}
	
}
