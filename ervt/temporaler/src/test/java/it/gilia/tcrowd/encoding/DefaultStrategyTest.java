package it.gilia.tcrowd.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.json.*;

import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;

import java.util.LinkedList;
import java.util.Iterator;

@DisplayName("Test Suite for a default strategy encoding ERvt diagrams into DL-Lite_fpx")
public class DefaultStrategyTest{
	
	@Test
	@DisplayName("Entities")
	public void testERvtEntitiesToDL() {
		JSONObject obj = new JSONObject();
        JSONArray links = new JSONArray();
        JSONArray attributes = new JSONArray();
        JSONArray entities = new JSONArray();
        
        String jsonEntity = new JSONStringer()
                .object()
                .key("name")
                .value("entity 1")
                .key("timestamp")
                .value("")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
  
        entities.put(jsonEntity);

        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(obj);

	    System.out.println("TBox Test 1");
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	    //   assertEquals("entity 1 -> Top", ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }

	}
	
	@Test
	@DisplayName("Snapshot Entities")
	public void testERvtSnapEntitiesToDL() {
		JSONObject obj = new JSONObject();
        JSONArray links = new JSONArray();
        JSONArray attributes = new JSONArray();
        JSONArray entities = new JSONArray();
        
        String jsonEntity = new JSONStringer()
                .object()
                .key("name")
                .value("entity 2")
                .key("timestamp")
                .value("snapshot")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
  
        entities.put(jsonEntity);

        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(obj);

	    System.out.println("TBox Test 2");
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	  //     assertEquals("entity 2 -> G H entity 2", ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }

	}
	
	@Test
	@DisplayName("Temporal Entities")
	public void testERvtTempEntitiesToDL() {
		JSONObject obj = new JSONObject();
        JSONArray links = new JSONArray();
        JSONArray attributes = new JSONArray();
        JSONArray entities = new JSONArray();
        
        String jsonEntity = new JSONStringer()
                .object()
                .key("name")
                .value("entity 3")
                .key("timestamp")
                .value("temporal")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
  
        entities.put(jsonEntity);

        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(obj);

	    System.out.println("TBox Test 3");
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	 //      assertEquals("!_|_ -> F O !entity 3", ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }
	}
	
	@Test
	@DisplayName("Entities with a key attribute")
	public void testERvtEntitiesWithAttrToDL() {
		JSONObject obj = new JSONObject();
        JSONArray links = new JSONArray();
        JSONArray attributes = new JSONArray();
        JSONArray entities = new JSONArray();
        
        String jsonEntity = new JSONStringer()
                .object()
                .key("name")
                .value("entity 1")
                .key("timestamp")
                .value("")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
        
        String jsonAttribute = new JSONStringer()
                .object()
                .key("name")
                .value("attribute 1")
                .key("type")
                .value("key")
                .key("datatype")
                .value("Integer")
                .key("timestamp")
                .value("")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
  
        entities.put(jsonEntity);
        attributes.put(jsonAttribute);
        
        String jsonLinks = new JSONStringer()
                .object()
                .key("name")
                .value("attrA")
                .key("entity")
                .value("entity 1")
                .key("attribute")
                .value("attribute 1")
                .key("type")
                .value("attribute")
                .endObject()
                .toString();
  
        links.put(jsonLinks);

        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(obj);

	    System.out.println("TBox Test 4");
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	    //   assertEquals("entity 1 -> Top", ci.getLHS()+" -> "+ci.getRHS());

	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }

	}

	@Test
	@DisplayName("ISA simple")
	public void testERvtSimpleISAtoDL() {
		JSONObject obj = new JSONObject();
        JSONArray links = new JSONArray();
        JSONArray attributes = new JSONArray();
        JSONArray entities = new JSONArray();
        
        String jsonEntity = new JSONStringer()
                .object()
                .key("name")
                .value("entity 1")
                .key("timestamp")
                .value("")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
        
        String jsonEntity2 = new JSONStringer()
                .object()
                .key("name")
                .value("entity 2")
                .key("timestamp")
                .value("")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
  
        entities.put(jsonEntity);
        entities.put(jsonEntity2);
        
        String jsonLinks = new JSONStringer()
                .object()
                .key("name")
                .value("isa1")
                .key("parent")
                .value("entity 1")
                .key("entities")
                .value(new JSONArray()
                		.put("entity 2")
                		)
                .key("type")
                .value("isa")
                .key("constraint")
                .value(new JSONArray())
                .endObject()
                .toString();
  
        links.put(jsonLinks);

        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(obj);

	    System.out.println("TBox Test 5");
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       //assertEquals(actual_s, ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }
	}
	
	@Test
	@DisplayName("Binary Rel simple with cardinalities greater than 0")
	public void testERvtSimpleReltoDL() {
		JSONObject obj = new JSONObject();
        JSONArray links = new JSONArray();
        JSONArray attributes = new JSONArray();
        JSONArray entities = new JSONArray();
        
        String jsonEntity = new JSONStringer()
                .object()
                .key("name")
                .value("entity 1")
                .key("timestamp")
                .value("")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
        
        String jsonEntity2 = new JSONStringer()
                .object()
                .key("name")
                .value("entity 2")
                .key("timestamp")
                .value("")
                .key("position")
                .object()
                .key("x")
                .value("600")
                .key("y")
                .value("800")
                .endObject()
                .endObject()
                .toString();
  
        entities.put(jsonEntity);
        entities.put(jsonEntity2);
        
        String jsonLinks = new JSONStringer()
                .object()
                .key("name")
                .value("rel1")
                .key("entities")
                .value(new JSONArray()
                		.put("entity 1")
                		.put("entity 2")
                		)
                .key("cardinality")
                .value(new JSONArray()
                		.put("1..4")
                		.put("3..5")
                		)
                .key("roles")
                .value(new JSONArray()
                		.put("e1")
                		.put("e2")
                		)
                .key("timestamp")
                .value("")
                .key("type")
                .value("relationship")
                .endObject()
                .toString();
  
        links.put(jsonLinks);

        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(obj);

	    System.out.println("TBox Test 6");
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       //assertEquals(actual_s, ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }
	}
	
}

