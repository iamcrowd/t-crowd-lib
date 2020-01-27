package it.gilia.tcrowd.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.apache.commons.io.IOUtils;
import org.json.*;

import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;

import java.util.Objects;
import java.util.LinkedList;
import java.lang.*;
import java.io.*;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;

@DisplayName("Test Suite for a default strategy encoding ERvt diagrams into DL-Lite_fpx")
public class DefaultStrategyTest{
	
	private JSONObject getJSONfromFile(String fileName){
		InputStream data = DefaultStrategyTest.class.
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
		InputStream data = DefaultStrategyTest.class.
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
	@DisplayName("Only one Entity")
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

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       assertEquals("", ci.getLHS()+" -> "+ci.getRHS());
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

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       assertEquals("entity 2 -> G H entity 2", ci.getLHS()+" -> "+ci.getRHS());
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

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       assertEquals("!_|_ -> F O !entity 3", ci.getLHS()+" -> "+ci.getRHS());
	     }
	}
	
	@Test
	@DisplayName("Entities with a key attribute")
	public void testERvtEntitiesWithAttrToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtEntitiesWithAttrToDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtEntitiesWithAttrToDL"), actual);
	}
	
	@Test
	@DisplayName("Entities with a key attribute and a normal one without temporality timestamps.")
	public void testERvtEntitiesWithAttrKandNToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtEntitiesWithAttrKandNToDL"));
        
        tbox.addExtensionConstraints();

        Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtEntitiesWithAttrKandNToDL"), actual);
	}
	
	@Test
	@DisplayName("Entities with a snapshot key attribute. It returns an extended TBox")
	public void testERvtEntitiesWithAttrToDLExtended() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtEntitiesWithAttrToDLExtended"));
        
        tbox.addExtensionConstraints();
	    
		Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtEntitiesWithAttrToDLExtended"), actual);
	}
	
	@Test
	@DisplayName("Entities with a snapshot key string attribute. It returns an extended TBox")
	public void testERvtEntitiesWithStringAttrToDLExtended() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtEntitiesWithStringAttrToDLExtended"));
        
        tbox.addExtensionConstraints();
	    
		Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtEntitiesWithStringAttrToDLExtended"), actual);
	}
	
	@Test
	@DisplayName("Entities with a temporal key attribute. It returns an extended TBox")
	public void testERvtEntitiesWithTempAttrToDLExtended() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtEntitiesWithTempAttrToDLExtended"));
        
        tbox.addExtensionConstraints();
	    
		Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtEntitiesWithTempAttrToDLExtended"), actual);
	}
	
	@Test
	@DisplayName("Entity with a snapshot attribute")
	public void testERvtEntitiesWithSnapAttrToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtEntitiesWithSnapAttrToDL"));
        
        tbox.addExtensionConstraints();

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtEntitiesWithSnapAttrToDL"), actual);

	}

	@Test
	@DisplayName("Entity with a temporal attribute")
	public void testERvtEntitiesWithTemporalAttrToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtEntitiesWithTemporalAttrToDL"));

        tbox.addExtensionConstraints();
        
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtEntitiesWithTemporalAttrToDL"), actual);

	}
	
	@Test
	@DisplayName("ISA simple")
	public void testERvtSimpleISAtoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtSimpleISAtoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtSimpleISAtoDL"), actual);
	}
	
	@Test
	@DisplayName("Exclusive ISA")
	public void testERvtSimpleExclusiveISAtoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtSimpleExclusiveISAtoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtSimpleExclusiveISAtoDL"), actual);
	}
	
	@Test
	@DisplayName("Total ISA")
	public void testERvtSimpleTotalISAtoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtSimpleTotalISAtoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtSimpleTotalISAtoDL"), actual);
	}
	
	@Test
	@DisplayName("Total and Exclusive ISA")
	public void testERvtSimpleTotalExclusiveISAtoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtSimpleTotalExclusiveISAtoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtSimpleTotalExclusiveISAtoDL"), actual);
	}
	
	@Test
	@DisplayName("Binary Rel simple with cardinalities greater than 0")
	public void testERvtSimpleReltoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtSimpleReltoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtSimpleReltoDL"), actual);
	}
	
	@Test
	@DisplayName("Binary Rel simple with cardinalities greater than 0. Extended TBox returned")
	public void testERvtSimpleReltoDLExtendedTbox() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtSimpleReltoDLExtendedTbox"));
        tbox.addExtensionConstraints();
        
	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtSimpleReltoDLExtendedTbox"), actual);
	}

	@Test
	@DisplayName("Temporal Binary Rel")
	public void testERvtTemporalBinaryReltoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtTemporalBinaryReltoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtTemporalBinaryReltoDL"), actual);
	}

	@Test
	@DisplayName("TEX (Transition EXtension)")
	public void testERvtTEXtoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtTEXtoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtTEXtoDL"), actual);
	}
	
	@Test
	@DisplayName("DEV (Dynamic EVolution)")
	public void testERvtDEVtoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtDEVtoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtDEVtoDL"), actual);
	}	
	
	@Test
	@DisplayName("DEX^- (Dynamic EXtension)")
	public void testERvtDEXMinustoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtDEXMinustoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtDEXMinustoDL"), actual);
	}
	
	@Test
	@DisplayName("PEX (Persistent EXtension)")
	public void testERvtPEXtoDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testERvtPEXtoDL"));

	    Iterator<ConceptInclusionAssertion> iterator = tbox.iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	       ConceptInclusionAssertion ci = iterator.next();
	       actual = actual.concat(ci.getLHS()+" -> "+ci.getRHS() + "\n");   
	    }

		assertEquals(this.checkExpectedResult("testERvtPEXtoDL"), actual);
	}
	
}

