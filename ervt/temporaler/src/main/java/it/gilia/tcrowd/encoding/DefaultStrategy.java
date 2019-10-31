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
import it.unibz.inf.tdllitefpx.concepts.*;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;

public class DefaultStrategy{
	
	TBox myTBox;
	
	public DefaultStrategy() {
		TBox myTBox = new TBox();
	}
	
	public TBox getTBox() {
		return myTBox;
	}
	
	/**
	 * 
	 * @param ervt_json A JSON object representing an ERvt temporal model only 
	 * containing entities.
	 */
	public void to_dllitefpx_entities(JSONObject ervt_json) {
		//https://www.mkyong.com/java/json-simple-example-read-and-write-json/		
		ervt_json.keys().forEachRemaining(key -> {
	        Object value = ervt_json.get(key);
	        JSONArray arr = ervt_json.getJSONArray(key);
	        System.out.println("Key: {0}..."+key);
	        arr.iterator().forEachRemaining(element -> {
	        	System.out.println("Element: {0}..."+element);
	        	Concept cpt = new AtomicConcept(element.toString());
	        	this.myTBox.add(cpt);
	        });
	    });
		System.out.println("TBox..."+this.getTBox());
		
	}
	
}
