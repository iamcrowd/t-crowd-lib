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

/**
 * 
 * @author gab
 *
 */
public class DefaultStrategy extends Strategy{
	
	public DefaultStrategy() {
		super();
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
        "relationships":[],
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
     *
	 * @implNote temporal and snapshot attributes missing
	 * @implNote attributes in relationships missing
	 * @implNote For attributes:
	 * (a) Every point with at least q'  R-successors has at least q R-successors, for each q < q'  .
	 * (b) If R is a rigid role, then every point with at least q R-successors at some moment has at least q R-successors at all moments of time.
	 * (c) If the domain of a role is not empty, then its range is not empty either.
	 * @see if attributes are not key so (c) is not needed?
	 * @implNote This impl manages a list of rigid roles and a list for remaining ones.
	 */
	public TBox to_dllitefpx(JSONObject ervt_json) {
		System.out.println("Starting JSON: "+ervt_json);
		
		ervt_json.keys().forEachRemaining(key -> {
			
			if (key.equals("entities")) {
				Object value = ervt_json.get(key);
				JSONArray arr = ervt_json.getJSONArray(key);
	        
				arr.iterator().forEachRemaining(element -> {
					JSONTokener t = new JSONTokener(element.toString());
					JSONObject jo = new JSONObject(t);
					
					Concept acpt = this.giveMeAconcept(jo.get("name").toString()); 
					
					if (jo.get("timestamp").toString().equals("snapshot")) {
						this.myTBox.add(new ConceptInclusionAssertion(
								acpt,
								new AlwaysFuture(new AlwaysPast(acpt))));
		        	
					}else if (jo.get("timestamp").toString().equals("temporal")) {
						this.myTBox.add(new ConceptInclusionAssertion(
								new NegatedConcept(new BottomConcept()),
								new SometimeFuture(new SometimePast(new NegatedConcept(acpt)))));
					}
					
				});
				
				
			}else if (key.equals("attributes")) {
				Object value = ervt_json.get(key);
				JSONArray arr = ervt_json.getJSONArray(key);
				
				if (arr.length() > 0) {
	        
					/* assert axioms >= 1 attInv \sqsubseteq domain */
					arr.iterator().forEachRemaining(element -> {
						JSONTokener t = new JSONTokener(element.toString());
						JSONObject jo = new JSONObject(t);

						Concept domain = this.giveMeAdomainConcept(jo.get("datatype").toString());
					
						if (jo.get("timestamp").toString().equals("snapshot")) {
							PositiveRole pRole = this.giveMeArigidRole(jo.get("name").toString());
							this.myTBox.add(new ConceptInclusionAssertion(
									new QuantifiedRole(pRole.getInverse(), 1),
									domain));
						
						}else {
							PositiveRole pRole = this.giveMeArole(jo.get("name").toString());
							this.myTBox.add(new ConceptInclusionAssertion(
									new QuantifiedRole(pRole.getInverse(), 1),
									domain));
						}
						
					});
				}
				
			}else if (key.equals("relationships")) {
				Object value = ervt_json.get(key);
				JSONArray arr = ervt_json.getJSONArray(key);
	        
				arr.iterator().forEachRemaining(element -> {
					JSONTokener t = new JSONTokener(element.toString());
					JSONObject jo = new JSONObject(t);
					
					Concept acpt = this.giveMeAconcept(jo.get("name").toString()); 
					
					if (jo.get("timestamp").toString().equals("snapshot")) {
						this.myTBox.add(new ConceptInclusionAssertion(
								acpt,
								new AlwaysFuture(new AlwaysPast(acpt))));
		        	
					}else if (jo.get("timestamp").toString().equals("temporal")) {
						this.myTBox.add(new ConceptInclusionAssertion(
								new NegatedConcept(new BottomConcept()),
								new SometimeFuture(new SometimePast(new NegatedConcept(acpt)))));
					}
					
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
						this.to_dllitefpx_tex(jo);
					break;
					case "dev":
						this.to_dllitefpx_dev(jo);
					break;
					case "dex-":
						this.to_dllitefpx_dexminus(jo);
					break;
					case "pex":
						this.to_dllitefpx_pex(jo);
					break;
					default:

						break;
					}
				});
			}
	    });
		
		this.tBoxClousure();
		System.out.println("TBox stats..."+this.getTBox().getStats());
		return this.getTBox();
	}
	
	/**
	 * tBoxClousure asserts background axioms in order to define the disjointness of the "Integer" concept with any other
	 * concept in the current KB. This is done if "Integer" previously asserted in the KB. 
	 * Otherwise, the clousure does not make sense.
	 */
	public void tBoxClousure() {
	// Integer "magic" concept must be disjoint with each atomic concept
		this.getConceptDomainList().forEach(domain -> {
			this.getConceptList().forEach(atomicC -> {
				if (!atomicC.toString().equals(domain.toString())) {
					this.myTBox.add(new ConceptInclusionAssertion(
							domain, new NegatedConcept(atomicC)));
				}
			});
		});
		
	}
	
	/**
	 * ISA links
	 * 
	 * @param ervt_isa
	 * 
	 * @apiNote {"name":"s2","parent":"Entity1","entities":["Entity3"],"type":"isa","constraint":[]}
	 * @see {"name":"s1","parent":"Entity4","entities":["Entity2","Entity5"],"type":"isa","constraint":["disjoint","covering"]}
	 *
	 * @implNote disjoint and covering constraints missing
	 */
	public void to_dllitefpx_isa(JSONObject ervt_isa) {
		Concept parent = this.giveMeAconcept(ervt_isa.get("parent").toString());
		
		ervt_isa.getJSONArray("entities").iterator().forEachRemaining(element -> {
			Concept child = this.giveMeAconcept(element.toString());
			this.myTBox.add(new ConceptInclusionAssertion(child, parent));
		});
	}

	/**
	 * Attribute links
	 * 
	 * @param ervt_attr
	 * 
	 * @apiNote {"name":"AttrA","entity":"Entity1","attribute":"A","type":"attribute"}
	 * @implNote
	 * 							this.myTBox.add(new ConceptInclusionAssertion(
									new QuantifiedRole(role_a, 2),
									new QuantifiedRole(role_a, 1)
									));
							this.myTBox.add(new ConceptInclusionAssertion(
								new QuantifiedRole(role_a, 1),
								new AlwaysPast(new AlwaysFuture(new QuantifiedRole(role_a, 1)))));
							this.myTBox.add(new ConceptInclusionAssertion(
								new QuantifiedRole(role_a, 2),
								new AlwaysPast(new AlwaysFuture(new QuantifiedRole(role_a, 2)))));
								
						    this.myTBox.add(new ConceptInclusionAssertion(
											new QuantifiedRole(role_a, 2),
											new QuantifiedRole(role_a, 1)
									));
	 */
	public void to_dllitefpx_attr(JSONObject ervt_attr) {
		List<PositiveRole> listRR = this.getRigidRoleList();
		List<PositiveRole> listR = this.getRoleList();
		int i = 0;
		int j = 0;

		while (i < listRR.size()) {
			this.myTBox.add(new ConceptInclusionAssertion(
					this.giveMeAconcept(ervt_attr.get("entity").toString()),
					new QuantifiedRole(listRR.get(i), 1)));
			this.myTBox.add(new ConceptInclusionAssertion(
					this.giveMeAconcept(ervt_attr.get("entity").toString()),
					new NegatedConcept(new QuantifiedRole(listRR.get(i), 2))));
			i++;
		}

		while (j < listR.size()) {
			this.myTBox.add(new ConceptInclusionAssertion(
					this.giveMeAconcept(ervt_attr.get("entity").toString()),
					new QuantifiedRole(listR.get(i), 1)));
			this.myTBox.add(new ConceptInclusionAssertion(
					this.giveMeAconcept(ervt_attr.get("entity").toString()),
					new NegatedConcept(new QuantifiedRole(listR.get(i), 2))));
			j++;
		}
	}

	/**
	 * Binary Relationship links
	 * 
	 * @param ervt_rel
	 * 
	 * @apiNote {"name":"R","entities":["Entity4","Entity1"],"cardinality":["1..4","3..5"],"roles":["entity4","entity1"],"type":"relationship"},
	 * @apiNote {"name":"R","entities":["Entity4","Entity1"],"cardinality":["1..4","3..5"],"roles":["entity4","entity1"],"type":"relationship"},
     * @apiNote {"name":"R1","entities":["Entity2","Entity3"],"cardinality":["0..*","0..*"],"roles":["entity2","entity3"],"type":"relationship"},
	 * @apiNote rel origin = entities[0]. rel target = entities[1]
	 * 
	 * @implNote 0..N cardinalities missing
	 */
	public void to_dllitefpx_rel(JSONObject ervt_rel) {
		
		Concept reification = this.giveMeAconcept(ervt_rel.get("name").toString());
		
		Concept origin = this.giveMeAconcept(
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

		
		Concept target = this.giveMeAconcept(
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
	
	/**
	 * 
	 * @param ervt_tex JSONObject containing a visual definition of TEX constraints
	 * 
	 * @apiNote TEX (Transition EXtension) is a quantitative evolution constraint, meaning how objects evolve 
	 * between two entities in exactly one time unit. Next Feature.
	 * @apiNote {"name":"tex1","entities":["Entity2","Entity3"],"type":"tex"}
	 */
	public void to_dllitefpx_tex(JSONObject ervt_tex) {
		Concept entity1 = this.giveMeAconcept(
				ervt_tex.getJSONArray("entities").get(0).toString());
		Concept entity2 = this.giveMeAconcept(
				ervt_tex.getJSONArray("entities").get(1).toString());
		this.myTBox.add(new ConceptInclusionAssertion(
				entity1,
				new NextFuture(entity2)));
	}
	
	/**
	 * 
	 * @param ervt_dev JSONObject containing a visual definition of DEV constraints
	 * 
	 * @apiNote DEV (Dynamic EVolution) is a qualitative evolution constraint meaning that every 
	 * object will eventually become an instance of another entity. Sometime in the future.
	 * @apiNote {"name":"dev1","entities":["Entity2","Entity3"],"type":"dev"}
	 */
	public void to_dllitefpx_dev(JSONObject ervt_dev) {
		Concept entity1 = this.giveMeAconcept(
				ervt_dev.getJSONArray("entities").get(0).toString());
		Concept entity2 = this.giveMeAconcept(
				ervt_dev.getJSONArray("entities").get(1).toString());
		this.myTBox.add(new ConceptInclusionAssertion(
				entity1,
				new SometimeFuture(entity2)));
		
	}
	
	/**
	 * 
	 * @param ervt_dexminus JSONObject containing a visual definition of DEV constraints
	 * 
	 * @apiNote DEX (Dynamic EXtension) is a qualitative evolution constraint meaning that every 
	 * object was once an instance of another entity. Sometime in the past.
	 * @apiNote {"name":"dev1","entities":["Entity2","Entity3"],"type":"dex-"}
	 */
	public void to_dllitefpx_dexminus(JSONObject ervt_dexminus) {
		Concept entity1 = this.giveMeAconcept(
				ervt_dexminus.getJSONArray("entities").get(0).toString());
		Concept entity2 = this.giveMeAconcept(
				ervt_dexminus.getJSONArray("entities").get(1).toString());
		this.myTBox.add(new ConceptInclusionAssertion(
				entity1,
				new SometimePast(entity2)));
		
	}
	
	/**
	 * 
	 * @param ervt_pex JSONObject containing a visual definition of PEX constraints
	 * 
	 * @apiNote PEX (Persistent EXtension) is a qualitative evolution constraint meaning that every 
	 * object will always be an instance of its entity. Always in the future.
	 * @apiNote {"name":"dev1","entities":"Entity2","type":"pex"}
	 */
	public void to_dllitefpx_pex(JSONObject ervt_pex) {
		Concept entity = this.giveMeAconcept(
				ervt_pex.getJSONArray("entities").get(0).toString());
		this.myTBox.add(new ConceptInclusionAssertion(
				entity,
				new AlwaysFuture(entity)));
		
	}
	
}
