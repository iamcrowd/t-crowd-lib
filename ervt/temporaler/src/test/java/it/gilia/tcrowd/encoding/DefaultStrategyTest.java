package it.gilia.tcrowd.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.json.*;

import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;

import java.util.LinkedList;
import java.util.Iterator;

public class DefaultStrategyTest{
	
/*	@Test
	public void testBasicJSONReader() {
		JSONObject obj = new JSONObject();
        JSONArray entities = new JSONArray();
        entities.put("entitiy 1");
        entities.put("entitiy 2");
        entities.put("entitiy 3");
 
        JSONArray attributes = new JSONArray();
        attributes.put("attribute 1");
        attributes.put("attribute 2");
        attributes.put("attribute 3");

        JSONArray links = new JSONArray();
        links.put("link 1");
        links.put("link 2");
        links.put("link 3");


        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        strategy.inputJSON(obj);

	} */
	
	@Test
	@DisplayName("Entities")
	public void testERvtEntitiesToDL() {
		JSONObject obj = new JSONObject();
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
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx_entities(obj);
	    // print the list
	    System.out.println("LinkedList:" + tbox);

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       assertEquals("entity 1 -> Top", ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }

	}
	
	@Test
	@DisplayName("Snapshot Entities")
	public void testERvtSnapEntitiesToDL() {
		JSONObject obj = new JSONObject();
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
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx_entities(obj);
        
	    System.out.println("LinkedList:" + tbox);

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       assertEquals("entity 2 -> G H entity 2", ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }

	}
	
	@Test
	@DisplayName("Temporal Entities")
	public void testERvtTempEntitiesToDL() {
		JSONObject obj = new JSONObject();
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
        
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx_entities(obj);
        
	    System.out.println("LinkedList:" + tbox);

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	     while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       assertEquals("!_|_ -> F O !entity 3", ci.getLHS()+" -> "+ci.getRHS());
	       System.out.println(ci.getLHS()+" -> "+ci.getRHS());
	     }

	}
	
}

