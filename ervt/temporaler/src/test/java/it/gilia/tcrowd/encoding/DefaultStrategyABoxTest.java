package it.gilia.tcrowd.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.apache.commons.io.IOUtils;
import org.json.*;

import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;

import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

@DisplayName("Test Suite for a default strategy encoding Temporal Data into DL-Lite_fpx")
public class DefaultStrategyABoxTest {
	
	private JSONObject getJSONfromFile(String fileName){
		InputStream data = DefaultStrategyABoxTest.class.
				getClassLoader().
				getResourceAsStream(fileName + ".json"); 
		
	    if (data == null) {
            throw new NullPointerException("Cannot find resource file " + fileName);
        }
	    String jsonTxt = "";
	    try {
	    	jsonTxt = IOUtils.toString(data, "UTF-8");
	    }
	    catch(IOException e) {
	    	e.printStackTrace();
	    }
	    JSONObject object = new JSONObject(jsonTxt);
	    return object;
	}
	
	private String checkExpectedResult(String fileName) {
		InputStream data = DefaultStrategyABoxTest.class.
				getClassLoader().
				getResourceAsStream(fileName + ".txt");
	    if (data == null) {
            throw new NullPointerException("Cannot find resource file " + fileName);
        }
	    
	    String val = "";
	    try {
	    	BufferedReader r = new BufferedReader(
	    			new InputStreamReader(data));
	        String l;
	        while((l = r.readLine()) != null) {
	           val = val + l + "\n";
	        } 
	        data.close();
	    }
	    catch(IOException e) {
	    	e.printStackTrace();
	    }
	    return val;
	}
	
	@Test
	@DisplayName("Concepts at time 0")
	public void testABoxConceptsAtTime0ToDL() {
		JSONObject obj = new JSONObject();
        JSONArray concepts = new JSONArray();
        JSONArray roles = new JSONArray();

        String jsonConcept = new JSONStringer()
                .object()
                .key("concept")
                .value("Person")
                .key("instance")
                .value("Maria")
                .key("timestamp")
                .value("0")
                .endObject()
                .toString();
        
        concepts.put(jsonConcept);

        obj.put("concepts", concepts);
        obj.put("roles", roles);

        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpxABox(obj);

	    System.out.println("---------------------------------ABox Test 1 only one concept at time 0");
	}
	
	@Test
	@DisplayName("Concepts at time 1")
	public void testABoxConceptsAtTime1ToDL() {
		JSONObject obj = new JSONObject();
        JSONArray concepts = new JSONArray();
        JSONArray roles = new JSONArray();

        String jsonConcept = new JSONStringer()
                .object()
                .key("concept")
                .value("Person")
                .key("instance")
                .value("Maria")
                .key("timestamp")
                .value("1")
                .endObject()
                .toString();
        
        concepts.put(jsonConcept);

        obj.put("concepts", concepts);
        obj.put("roles", roles);

        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpxABox(obj);

	    System.out.println("---------------------------------ABox Test 2 only one concept at time 1");
	}
	
	@Test
	@DisplayName("Concepts at time > 1")
	public void testABoxConceptsAtTimeNToDL() {
		JSONObject obj = new JSONObject();
        JSONArray concepts = new JSONArray();
        JSONArray roles = new JSONArray();

        String jsonConcept = new JSONStringer()
                .object()
                .key("concept")
                .value("Person")
                .key("instance")
                .value("Maria")
                .key("timestamp")
                .value("1")
                .endObject()
                .toString();
        
        String jsonConcept2 = new JSONStringer()
                .object()
                .key("concept")
                .value("Person")
                .key("instance")
                .value("Maria")
                .key("timestamp")
                .value("2")
                .endObject()
                .toString();
        
        concepts.put(jsonConcept);
        concepts.put(jsonConcept2);

        obj.put("concepts", concepts);
        obj.put("roles", roles);

        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpxABox(obj);

	    System.out.println("---------------------------------ABox Test 3 only one concept at time > 1");
	}
	
	@Test
	@DisplayName("Roles at time 0")
	public void testABoxRolesAtTime0ToDL() {
		JSONObject obj = new JSONObject();
        JSONArray concepts = new JSONArray();
        JSONArray roles = new JSONArray();

        String jsonRole = new JSONStringer()
                .object()
                .key("role")
                .value("Surname")
                .key("from")
                .value("Maria")
                .key("to")
                .value("Clinton")
                .key("timestamp")
                .value("0")
                .endObject()
                .toString();
        
        roles.put(jsonRole);

        obj.put("concepts", concepts);
        obj.put("roles", roles);
        
        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpxABox(obj);

	    System.out.println("---------------------------------ABox Test 2 only one role at time 0");
	}
	
	@Test
	@DisplayName("Roles at time 1")
	public void testABoxRolesAtTime1ToDL() {
		JSONObject obj = new JSONObject();
        JSONArray concepts = new JSONArray();
        JSONArray roles = new JSONArray();

        String jsonRole = new JSONStringer()
                .object()
                .key("role")
                .value("Surname")
                .key("from")
                .value("Maria")
                .key("to")
                .value("Clinton")
                .key("timestamp")
                .value("1")
                .endObject()
                .toString();
        
        roles.put(jsonRole);

        obj.put("concepts", concepts);
        obj.put("roles", roles);
        
        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpxABox(obj);

	    System.out.println("---------------------------------ABox Test only one role at time 1");
	}
	
	@Test
	@DisplayName("Roles at time N")
	public void testABoxRolesAtTimeNToDL() {
		JSONObject obj = new JSONObject();
        JSONArray concepts = new JSONArray();
        JSONArray roles = new JSONArray();

        String jsonRole = new JSONStringer()
                .object()
                .key("role")
                .value("Surname")
                .key("from")
                .value("Maria")
                .key("to")
                .value("Clinton")
                .key("timestamp")
                .value("1")
                .endObject()
                .toString();
        
        String jsonRole2 = new JSONStringer()
                .object()
                .key("role")
                .value("Surname")
                .key("from")
                .value("Maria")
                .key("to")
                .value("Clinton")
                .key("timestamp")
                .value("2")
                .endObject()
                .toString();
        
        roles.put(jsonRole);
        roles.put(jsonRole2);

        obj.put("concepts", concepts);
        obj.put("roles", roles);
        
        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpxABox(obj);

	    System.out.println("---------------------------------ABox Test only one role at time N");
	}
	
}

