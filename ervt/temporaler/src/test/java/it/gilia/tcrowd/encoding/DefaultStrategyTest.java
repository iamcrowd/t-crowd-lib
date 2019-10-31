package it.gilia.tcrowd.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.json.*;

public class DefaultStrategyTest{
	
	@Test
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
        strategy.to_dllitefpx(obj.toString());

	}
	
	@Test
	public void testERvtEntitiesToDL() {
		JSONObject obj = new JSONObject();
        JSONArray entities = new JSONArray();
        entities.put("entitiy 1");
        entities.put("entitiy 2");
        entities.put("entitiy 3");

        obj.put("entities", entities);
        
        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpx_entities(obj);

	}
	
}

