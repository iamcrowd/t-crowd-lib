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
	 * 
	 * @apiNote {"entities":[
        	{"name":"Entity1","timestamp":"snapshot","position":{"x":625,"y":183}},
        	{"name":"Entity2","timestamp":"temporal","position":{"x":328,"y":411}},
        	{"name":"Entity3","timestamp":"","position":{"x":809,"y":432}},
        	{"name":"Entity4","timestamp":"","position":{"x":259,"y":187}},
        	{"name":"Entity5","timestamp":"","position":{"x":151,"y":412}}]}
     *   	
     * Entity1 -> \box_f \box_p Entity1 
     * \neg \bot -> \Diamond_f \Diamond_p \neg Entity2 
	 */
	public TBox to_dllitefpx(JSONObject ervt_json) {
		//https://www.mkyong.com/java/json-simple-example-read-and-write-json/
		System.out.println("Starting JSON: "+ervt_json);
		
		ervt_json.keys().forEachRemaining(key -> {
			
			if (key.equals("entities")) {
				Object value = ervt_json.get(key);
				JSONArray arr = ervt_json.getJSONArray(key);
	        
				arr.iterator().forEachRemaining(element -> {
					JSONTokener t = new JSONTokener(element.toString());
					JSONObject jo = new JSONObject(t);
	        	
					if (jo.get("timestamp").toString().equals("")){
						Concept acpt = new AtomicConcept(jo.get("name").toString());
						this.myTBox.add(new ConceptInclusionAssertion(acpt, new AtomicConcept("Top")));
		        	
					}else if (jo.get("timestamp").toString().equals("snapshot")) {
						Concept acpt = new AtomicConcept(jo.get("name").toString());
						this.myTBox.add(new ConceptInclusionAssertion(
								acpt,
								new AlwaysFuture(new AlwaysPast(acpt))));
		        	
					}else if (jo.get("timestamp").toString().equals("temporal")) {
						Concept acpt = new AtomicConcept(jo.get("name").toString());
						this.myTBox.add(new ConceptInclusionAssertion(
								new NegatedConcept(new BottomConcept()),
								new SometimeFuture(new SometimePast(new NegatedConcept(acpt)))));
					}
				});
				
			}else if (key.equals("attributes")) {
				Object value = ervt_json.get(key);
				JSONArray arr = ervt_json.getJSONArray(key);
	        
				arr.iterator().forEachRemaining(element -> {
					JSONTokener t = new JSONTokener(element.toString());
					JSONObject jo = new JSONObject(t);
					
					Concept acdt = new AtomicConcept(jo.get("datatype").toString());
					Role role_a = new PositiveRole(new AtomicLocalRole(jo.get("name").toString()));

					System.out.println("datatype: "+acdt);
					System.out.println("role: "+role_a);
				});
				
			}else if (key.equals("links")) {
				Object value = ervt_json.get(key);
				JSONArray arr = ervt_json.getJSONArray(key);
	        
				arr.iterator().forEachRemaining(element -> {
					JSONTokener t = new JSONTokener(element.toString());
					JSONObject jo = new JSONObject(t);
	        	
					switch (jo.get("type").toString()) {
					case "isa":
						this.to_dllitefpx_isa(jo);
						break;
					case "attribute":

					break;
					case "attribute_rel":

					break;
					case "relationship":

					break;
					case "tex":

					break;
					case "dev":

					break;
					case "dexminus":

					break;
					case "pex":

					break;
					default:

						break;
					}
				});
			}
	    });
		
		System.out.println("TBox stats..."+this.getTBox().getStats());
		return this.getTBox();
	}

	/**
	 * 
	 * @param ervt_isa
	 * 
	 * @apiNote {"name":"s2","parent":"Entity1","entities":["Entity3"],"type":"isa","constraint":[],"position":{"x":793,"y":333}}
	 */
	public void to_dllitefpx_isa(JSONObject ervt_isa) {
		//https://www.mkyong.com/java/json-simple-example-read-and-write-json/
		System.out.println("Starting JSON: "+ervt_isa);
		
		Concept parent = new AtomicConcept(ervt_isa.get("parent").toString());
		
		System.out.println(ervt_isa.get("entities"));
		
		ervt_isa.getJSONArray("entities").iterator().forEachRemaining(element -> {
			Concept child = new AtomicConcept(element.toString());
			this.myTBox.add(new ConceptInclusionAssertion(child, parent));
		});
		
	}
	
	
	
}
