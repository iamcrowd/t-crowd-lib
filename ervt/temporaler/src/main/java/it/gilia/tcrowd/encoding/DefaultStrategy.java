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
import java.text.*;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.concepts.AtomicConcept;
import it.unibz.inf.tdllitefpx.concepts.ConjunctiveConcept;
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
import it.unibz.inf.tdllitefpx.roles.temporal.NextFutureRole;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;

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
	 * TDLLiteFPX TBox
	 * 
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
	 * @implNote attributes in relationships missing
	 * @implNote For attributes:
	 * (a) Every point with at least q'  R-successors has at least q R-successors, for each q < q'  .
	 * (b) If R is a rigid role, then every point with at least q R-successors at some moment has at least q R-successors at all moments of time.
	 * (c) If the domain of a role is not empty, then its range is not empty either.
	 * @see if attributes are not key so (c) is not needed?
	 * @implNote This impl manages a list of rigid roles and a list for remaining ones.
	 */
	public TBox to_dllitefpx(JSONObject ervt_json) {
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
							Role pRole = this.giveMeArigidRole(jo.get("name").toString());
							this.myTBox.add(new ConceptInclusionAssertion(
									new QuantifiedRole(pRole.getInverse(), 1),
									domain));
						
						}else {
							Role pRole = this.giveMeArole(jo.get("name").toString());
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
		//System.out.println("TBox stats..."+this.getTBox().getStats());
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
	 * @see {"name":"s1","parent":"Entity4","entities":["Entity2","Entity5"],"type":"isa","constraint":["exclusive","total"]}
	 *
	 * @implNote disjoint and covering constraints missing
	 */
	public void to_dllitefpx_isa(JSONObject ervt_isa) {
		List<Concept> list_childs = new ArrayList<Concept>();
		Concept parent = this.giveMeAconcept(ervt_isa.get("parent").toString());
		
		ervt_isa.getJSONArray("entities").iterator().forEachRemaining(element -> {
			Concept child = this.giveMeAconcept(element.toString());
			list_childs.add(child);
			this.myTBox.add(new ConceptInclusionAssertion(child, parent));
		});
		
		ervt_isa.getJSONArray("constraint").iterator().forEachRemaining(cons -> {
			
			if (cons.equals("exclusive")) {
				int i = 0;
				while (i < list_childs.size()) {
					int j = i + 1;

					while (j < list_childs.size()) {
						this.myTBox.add(new ConceptInclusionAssertion(
								list_childs.get(i), new NegatedConcept(list_childs.get(j))));
						j++;
					}
					i++;
				}
			}
			
			if (cons.equals("total")) {
				
				if (list_childs.size() == 1) {
					this.myTBox.add(new ConceptInclusionAssertion(
							parent, list_childs.get(0)));
					
				}else if (list_childs.size() > 1) {
					ConjunctiveConcept cc = new ConjunctiveConcept(
							new NegatedConcept(list_childs.get(0)),
							new NegatedConcept(list_childs.get(1)));
					
					if (list_childs.size() > 2) {
						int i = 2;
						while (i < list_childs.size()) {
							cc.add(new NegatedConcept(list_childs.get(i)));
							i++;
						}
					}
					
					this.myTBox.add(new ConceptInclusionAssertion(
							parent, new NegatedConcept(cc)));
				}
			}
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
		
		int ro = this.getRoleByNameIndex(ervt_attr.get("attribute").toString());
		
		if (ro != -1) {
			 Role prole = this.getRoleList().get(ro);
			 this.myTBox.add(new ConceptInclusionAssertion(
						this.giveMeAconcept(ervt_attr.get("entity").toString()),
											new QuantifiedRole(prole, 1)));
			 this.myTBox.add(new ConceptInclusionAssertion(
						this.giveMeAconcept(ervt_attr.get("entity").toString()),
											new NegatedConcept(new QuantifiedRole(prole, 2))));
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
		
		Role role_origin = this.giveMeArole(ervt_rel.getJSONArray("roles").get(0).toString());		
		
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
		
		Role role_target = this.giveMeArole(ervt_rel.getJSONArray("roles").get(1).toString());
		
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
	
	
	
	
	/**
	 * TDLLiteFPX ABox
	 * 
	 * @param ervtABox_json A JSON object representing temporal data 
	 * containing concept and role assertions.
	 * 
	 * @code{json} 	{"concepts":
	 * 					[{"concept":"Person", "instance":"Maria", "timestamp":"1"},
	 * 					 {"concept":"Person", "instance":"Maria", "timestamp":"2"}]
	 * 				 "roles":
	 * 					[{"role":"Surname", "from":"Maria", "to":"Clinton", "timestamp":"1"},
	 * 					 {"role":"Salary", "from":"Maria", "to":"1000", "timestamip":"2"}]
	 * 			 	}
       @endcode
     *
	 */
	public ABox to_dllitefpxABox(JSONObject ervtABox_json) {
		ervtABox_json.keys().forEachRemaining(key -> {
			
			if (key.equals("concepts")) {
				Object value = ervtABox_json.get(key);
				JSONArray arr = ervtABox_json.getJSONArray(key);
	        
				arr.iterator().forEachRemaining(element -> {
					JSONTokener t = new JSONTokener(element.toString());
					JSONObject jo = new JSONObject(t);
					try{
						this.getABoxConceptAssertion(jo);
					}catch(Exception e) {
						System.out.println(e.getMessage());
					}
				});
				
			}else if (key.equals("roles")) {
				Object value = ervtABox_json.get(key);
				JSONArray arr = ervtABox_json.getJSONArray(key);
				
				if (arr.length() > 0) {

					arr.iterator().forEachRemaining(element -> {
						JSONTokener t = new JSONTokener(element.toString());
						JSONObject jo = new JSONObject(t);
						try {
							this.getABoxRoleAssertion(jo);
						}catch(Exception e) {
							System.out.println(e.getMessage());
						}
						
					});
				}
			}
		});
		return this.getABox();
	}
	
	/**
	 * Given a JSONObject encoding a ABox concept with a timestamp, validate that such concept exists in
	 * the current TBox and assert the new ABox temporal concept
	 * 
	 * @param assertion a JSONObject ABox concept with a timestamp
	 * @throws Exception if the ABox concept does not exist in the current TBox
	 */
	public void getABoxConceptAssertion(JSONObject assertion) throws Exception {
		Concept temp_concept = new AtomicConcept(assertion.get("concept").toString());
		
		if (this.existsConcept(temp_concept)){
			Concept concept = this.giveMeAconcept(assertion.get("concept").toString());
			String instance = new String(assertion.get("instance").toString());
			int numberOfNext = Integer.parseInt(assertion.get("timestamp").toString());
		
			if (numberOfNext == 0) {
				ABoxConceptAssertion a1 = new ABoxConceptAssertion(concept, instance);
				this.myABox.addConceptsAssertion(a1);
			
			}else if (numberOfNext > 0) {
				int countNext = 1;

				while (countNext <= numberOfNext) {
					Concept tconcept = new NextFuture(concept);
					concept = tconcept;
					countNext++;
				}
				ABoxConceptAssertion a1 = new ABoxConceptAssertion(concept, instance);
				this.myABox.addConceptsAssertion(a1);
			}
		} else {
			throw new Exception("[MATCH EXCEPTION]: ABox and TBox do not match. Concept: " + assertion.get("concept").toString()); 
		}
	}
	
	/**
	 * Given a JSONObject encoding a ABox role with a timestamp, validate that such role exists in
	 * the current TBox and assert the new ABox temporal role
	 * 
	 * @param assertion a JSONObject ABox role with a timestamp
	 * @throws Exception if the ABox role does not exist in the current TBox
	 */
	public void getABoxRoleAssertion(JSONObject assertion) throws Exception {
		Role temp_local_role = new PositiveRole(new AtomicLocalRole(assertion.get("role").toString()));
		Role temp_rigid_role = new PositiveRole(new AtomicRigidRole(assertion.get("role").toString()));
		
		if (this.existsRole(temp_local_role) || this.existsRole(temp_rigid_role)){
			String from = new String(assertion.get("from").toString());
			String to = new String(assertion.get("to").toString()); 
			int numberOfNext = Integer.parseInt(assertion.get("timestamp").toString());
			
			if (this.existsRole(temp_local_role)) {
				Role localrole = this.giveMeArole(assertion.get("role").toString());
				ABoxRoleAssertion r1 = new ABoxRoleAssertion(localrole, from, to, numberOfNext);
				this.myABox.addABoxRoleAssertion(r1);
				
			} else if (this.existsRole(temp_rigid_role)) {
				Role rigidrole = this.giveMeArigidRole(assertion.get("role").toString());
				ABoxRoleAssertion r1 = new ABoxRoleAssertion(rigidrole, from, to, numberOfNext);
				this.myABox.addABoxRoleAssertion(r1);
			}
			
		} else {
			throw new Exception("[MATCH EXCEPTION]: ABox and TBox do not match. Role: " + assertion.get("role").toString()); 
		}
	}

}