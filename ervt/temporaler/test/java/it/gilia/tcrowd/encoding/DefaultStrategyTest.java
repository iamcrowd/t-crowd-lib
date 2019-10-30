package it.gilia.tcrowd.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.json.simple.*;

public class DefaultStrategyTest{
	
	@Test
	public void testBasicJSONReader() {
		JSONObject obj = new JSONObject();
        JSONArray entities = new JSONArray();
        entities.add("entitiy 1");
        entities.add("entitiy 2");
        entities.add("entitiy 3");
 
        JSONArray attributes = new JSONArray();
        attributes.add("attribute 1");
        attributes.add("attribute 2");
        attributes.add("attribute 3");

        JSONArray links = new JSONArray();
        links.add("link 1");
        links.add("link 2");
        links.add("link 3");


        obj.put("entities", entities);
        obj.put("attributes", attributes);
        obj.put("links", links);
        
        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpx(obj);

	}
	
}

