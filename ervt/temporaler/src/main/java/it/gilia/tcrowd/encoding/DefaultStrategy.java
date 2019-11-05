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


public class DefaultStrategy{
	
	TBox myTBox = new TBox();
	List<Concept> list_ac = new ArrayList<Concept>();
	List<Role> list_role = new ArrayList<Role>();
	
	public DefaultStrategy() {
		
	}
	
	public TBox getTBox() {
		return myTBox;
	}
	
	/**
	 * 
	 * @param ervt_json A JSON object representing an ERvt temporal model 
	 * containing entities (regular, snapshot and temporal), attributes (regular, snapshot, temporal) and
	 * links meaning for visual lines between entities, entities and attributes and relationships with roles.
	 * 
	 * @code{json} {"entities":[
        	{"name":"Entity1","timestamp":"snapshot","position":{"x":625,"y":183}},
        	{"name":"Entity2","timestamp":"temporal","position":{"x":328,"y":411}},
        	{"name":"Entity3","timestamp":"","position":{"x":809,"y":432}},
        	{"name":"Entity4","timestamp":"","position":{"x":259,"y":187}},
        	{"name":"Entity5","timestamp":"","position":{"x":151,"y":412}}]
		"attributes":[
        	{"name":"A","type":"key","datatype":"integer","timestamp":"snapshot","position":{"x":625,"y":183}},
        	{"name":"B","type":"normal","datatype":"string","timestamp":"temporal","position":{"x":809,"y":432}},
        	{"name":"C","type":"normal","datatype":"string","timestamp":"","position":{"x":809,"y":432}}],
		"links":[
        	{"name":"AttrA","entity":"Entity1","attribute":"A","type":"attribute"},
        	{"name":"AttrB","entity":"Entity2","attribute":"B","type":"attribute"},
        	{"name":"AttrC","relationship":"R1","attribute":"C","type":"attribute_rel"},
        	{"name":"R","entities":["Entity4","Entity1"],"cardinality":["1..4","3..5"],"roles":["entity4","entity1"],"timestamp":"temporal","type":"relationship"},
        	{"name":"R1","entities":["Entity2","Entity3"],"cardinality":["0..*","0..*"],"roles":["entity2","entity3"],"timestamp":"snapshot","type":"relationship"},
        	{"name":"s1","parent":"Entity4","entities":["Entity2","Entity5"],"type":"isa","constraint":["disjoint","covering"]},
        	{"name":"s2","parent":"Entity1","entities":["Entity3"],"type":"isa","constraint":[]},
        	{"name":"tex1","entities":["Entity2","Entity3"],"type":"tex"},
        	{"name":"dev1","entities":["Entity4","Entity5"],"type":"dev"},
        	{"name":"dexminus1","entities":["Entity1","Entity2"],"type":"dexminus"},
        	{"name":"pex1","entity":"Entity1","type":"pex"}]}
        @endcode
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
						this.list_ac.add(acpt);
						this.myTBox.add(new ConceptInclusionAssertion(acpt, new AtomicConcept("Top")));
		        	
					}else if (jo.get("timestamp").toString().equals("snapshot")) {
						Concept acpt = new AtomicConcept(jo.get("name").toString());
						this.list_ac.add(acpt);
						this.myTBox.add(new ConceptInclusionAssertion(
								acpt,
								new AlwaysFuture(new AlwaysPast(acpt))));
		        	
					}else if (jo.get("timestamp").toString().equals("temporal")) {
						Concept acpt = new AtomicConcept(jo.get("name").toString());
						this.list_ac.add(acpt);
						this.myTBox.add(new ConceptInclusionAssertion(
								new NegatedConcept(new BottomConcept()),
								new SometimeFuture(new SometimePast(new NegatedConcept(acpt)))));
					}
					
				});
				
				// Integer "magic" concept must be disjoint with each atomic concept
				
				this.list_ac.forEach(atomic -> {
					this.myTBox.add(new ConceptInclusionAssertion(
							new AtomicConcept("Integer"), new NegatedConcept(atomic)));
				});
				
			}else if (key.equals("attributes")) {
				Object value = ervt_json.get(key);
				JSONArray arr = ervt_json.getJSONArray(key);
				
				Concept integer_c = new AtomicConcept("Integer");
	        
				arr.iterator().forEachRemaining(element -> {
					JSONTokener t = new JSONTokener(element.toString());
					JSONObject jo = new JSONObject(t);
					
					if (!jo.get("datatype").toString().equals("Integer")) {
						Concept acdt = new AtomicConcept(jo.get("datatype").toString());
					}
					
					Role role_a = new PositiveRole(new AtomicLocalRole(jo.get("name").toString()));
					this.list_role.add(role_a);
					
					this.myTBox.add(new ConceptInclusionAssertion(
									new QuantifiedRole(role_a.getInverse(), 1),
									integer_c));
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
						this.to_dllitefpx_attr(jo);
					break;
					case "attribute_rel":

					break;
					case "relationship":
						this.to_dllitefpx_rel(jo);
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
	 * ISA links
	 * 
	 * @param ervt_isa
	 * 
	 * @apiNote {"name":"s2","parent":"Entity1","entities":["Entity3"],"type":"isa","constraint":[]}
	 * @see {"name":"s1","parent":"Entity4","entities":["Entity2","Entity5"],"type":"isa","constraint":["disjoint","covering"]}
	 */
	public void to_dllitefpx_isa(JSONObject ervt_isa) {
		Concept parent = new AtomicConcept(ervt_isa.get("parent").toString());
		
		ervt_isa.getJSONArray("entities").iterator().forEachRemaining(element -> {
			Concept child = new AtomicConcept(element.toString());
			this.myTBox.add(new ConceptInclusionAssertion(child, parent));
		});
	}

	/**
	 * Attribute links
	 * 
	 * @param ervt_attr
	 * 
	 * @apiNote {"name":"AttrA","entity":"Entity1","attribute":"A","type":"attribute"}
	 */
	public void to_dllitefpx_attr(JSONObject ervt_attr) {
		this.myTBox.add(new ConceptInclusionAssertion(
				new AtomicConcept(ervt_attr.get("entity").toString()),
				new QuantifiedRole(
						new PositiveRole(
								new AtomicLocalRole(ervt_attr.get("attribute").toString())), 1)));
		this.myTBox.add(new ConceptInclusionAssertion(
				new AtomicConcept(ervt_attr.get("entity").toString()),
				new NegatedConcept(new QuantifiedRole(
						new PositiveRole(
								new AtomicLocalRole(ervt_attr.get("attribute").toString())), 2))));
		
	}

	/**
	 * Binary Relationship links
	 * 
	 * @param ervt_rel
	 * 
	 * @apiNote {"name":"R","entities":["Entity4","Entity1"],"cardinality":["1..4","3..5"],"roles":["entity4","entity1"],"timestamp":"","type":"relationship"},
	 * @apiNote {"name":"R","entities":["Entity4","Entity1"],"cardinality":["1..4","3..5"],"roles":["entity4","entity1"],"timestamp":"temporal","type":"relationship"},
     * @apiNote {"name":"R1","entities":["Entity2","Entity3"],"cardinality":["0..*","0..*"],"roles":["entity2","entity3"],"timestamp":"snapshot","type":"relationship"},
	 * @apiNote rel origin = entities[0]. rel target = entities[1]
	 */
	public void to_dllitefpx_rel(JSONObject ervt_rel) {
		
		Concept reification = new AtomicConcept(ervt_rel.get("name").toString());
		this.myTBox.add(new ConceptInclusionAssertion(
				new AtomicConcept("Integer"), reification));
		
		Concept origin = new AtomicConcept(
				ervt_rel.getJSONArray("entities").get(0).toString());
		Role role_origin = new PositiveRole(
				new AtomicLocalRole(ervt_rel.getJSONArray("roles").get(0).toString()));
		
		int card_min_role_o = Character.getNumericValue(ervt_rel.getJSONArray("cardinality")
				.get(0)
				.toString()
				.charAt(0));
		int card_max_role_o = Character.getNumericValue(ervt_rel.getJSONArray("cardinality")
				.get(0)
				.toString()
				.charAt(3));
		
		this.myTBox.add(new ConceptInclusionAssertion(
				reification, new QuantifiedRole(role_origin, card_min_role_o)));
		this.myTBox.add(new ConceptInclusionAssertion(
				new QuantifiedRole(role_origin, card_min_role_o),
				reification));
		this.myTBox.add(new ConceptInclusionAssertion(
				new QuantifiedRole(role_origin, card_max_role_o + 1),
				new BottomConcept()));
		this.myTBox.add(new ConceptInclusionAssertion(
				new QuantifiedRole(role_origin.getInverse(), card_min_role_o),
				origin));
		this.myTBox.add(new ConceptInclusionAssertion(
				origin,
				new QuantifiedRole(role_origin.getInverse(), card_min_role_o)));
		this.myTBox.add(new ConceptInclusionAssertion(
				origin,
				new NegatedConcept(new QuantifiedRole(role_origin.getInverse(), card_max_role_o + 1))));

		
		Concept target = new AtomicConcept(
				ervt_rel.getJSONArray("entities").get(1).toString());
		Role role_target = new PositiveRole(
				new AtomicLocalRole(ervt_rel.getJSONArray("roles").get(1).toString()));
		
		int card_min_role_t = Character.getNumericValue(ervt_rel.getJSONArray("cardinality")
				.get(1)
				.toString()
				.charAt(0));
		int card_max_role_t = Character.getNumericValue(ervt_rel.getJSONArray("cardinality")
				.get(1)
				.toString()
				.charAt(3));
		
		this.myTBox.add(new ConceptInclusionAssertion(
				reification, new QuantifiedRole(role_target, card_min_role_t)));
		this.myTBox.add(new ConceptInclusionAssertion(
				new QuantifiedRole(role_target, card_min_role_t),
				reification));
		this.myTBox.add(new ConceptInclusionAssertion(
				new QuantifiedRole(role_target, card_max_role_t + 1),
				new BottomConcept()));
		this.myTBox.add(new ConceptInclusionAssertion(
				new QuantifiedRole(role_target.getInverse(), card_min_role_t),
				target));
		this.myTBox.add(new ConceptInclusionAssertion(
				target,
				new QuantifiedRole(role_target.getInverse(), card_min_role_t)));
		this.myTBox.add(new ConceptInclusionAssertion(
				target,
				new NegatedConcept(new QuantifiedRole(role_target.getInverse(), card_max_role_t + 1))));
		
	}
	
}
