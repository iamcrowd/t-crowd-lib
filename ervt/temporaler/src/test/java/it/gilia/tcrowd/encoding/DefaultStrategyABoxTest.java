package it.gilia.tcrowd.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.*;

import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.tbox.ConceptInclusionAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxRoleAssertion;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;

import it.unibz.inf.tdllitefpx.roles.temporal.NextFutureRole;
import it.unibz.inf.tdllitefpx.roles.temporal.TemporalRole;

import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	
	private List<String> checkExpectedResult(String fileName) {
		InputStream data = DefaultStrategyABoxTest.class.
				getClassLoader().
				getResourceAsStream(fileName + ".txt");
	    if (data == null) {
            throw new NullPointerException("Cannot find resource file " + fileName);
        }
	    
	    List<String> val = new ArrayList<String>();
	    try {
	    	BufferedReader r = new BufferedReader(
	    			new InputStreamReader(data));
	        String l;
	        while((l = r.readLine()) != null) {
	           val.add(l);
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
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testABoxConceptsAtTime0ToDLTBOX"));
        ABox abox = strategy.to_dllitefpxABox(this.getJSONfromFile("testABoxConceptsAtTime0ToDL"));
        
		Iterator<ABoxConceptAssertion> iterator = abox.getABoxConceptAssertions().iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	    	ABoxConceptAssertion ci = iterator.next();
	    	actual = actual.concat(ci.getConceptAssertion() + "(" + ci.getConstant() + ")" + "\n");   
	    }
	    
		assertEquals(this.checkExpectedResult("testABoxConceptsAtTime0ToDL"), actual);
	}
	
	@Test
	@DisplayName("Concepts at time 1")
	public void testABoxConceptsAtTime1ToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testABoxConceptsAtTime1ToDLTBOX"));
        ABox abox = strategy.to_dllitefpxABox(this.getJSONfromFile("testABoxConceptsAtTime1ToDL"));
        
		Iterator<ABoxConceptAssertion> iterator = abox.getABoxConceptAssertions().iterator();
		List<String> expected = this.checkExpectedResult("testABoxConceptsAtTime1ToDL");
		
	    while(iterator.hasNext()){
	    	ABoxConceptAssertion ci = iterator.next();
	    	String actual = new String(ci.getConceptAssertion() + "(" + ci.getConstant() + ")");
	    	assertTrue(expected.contains(actual),
	    			"'" + actual + "'" + " not expected");	
	    }
	}
	
	@Test
	@DisplayName("Concepts at time > 1")
	public void testABoxConceptsAtTimeNToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        TBox tbox = strategy.to_dllitefpx(this.getJSONfromFile("testABoxConceptsAtTimeNToDLTBOX"));
        ABox abox = strategy.to_dllitefpxABox(this.getJSONfromFile("testABoxConceptsAtTimeNToDL"));

		Iterator<ABoxConceptAssertion> iterator = abox.getABoxConceptAssertions().iterator();
		List<String> expected = this.checkExpectedResult("testABoxConceptsAtTimeNToDL");
		
	    while(iterator.hasNext()){
	    	ABoxConceptAssertion ci = iterator.next();
	    	String actual = new String(ci.getConceptAssertion() + "(" + ci.getConstant() + ")");
	    	assertTrue(expected.contains(actual),
	    			"'" + actual + "'" + " not expected");	
	    }   
	}
	
	@Test
	@DisplayName("Roles at time 0")
	public void testABoxRolesAtTime0ToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        ABox abox = strategy.to_dllitefpxABox(this.getJSONfromFile("testABoxRolesAtTime0ToDL"));
        
		Iterator<ABoxRoleAssertion> iterator = abox.getABoxRoleAssertions().iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	    	ABoxRoleAssertion ci = iterator.next();
	    	NextFutureRole nfr = new NextFutureRole(ci);
	    	actual = actual.concat(nfr.toString() + "\n");   
	    }
	    
		assertEquals(this.checkExpectedResult("testABoxRolesAtTime0ToDL"), actual);

	}
	
	@Test
	@DisplayName("Roles at time 1")
	public void testABoxRolesAtTime1ToDL() {        
        DefaultStrategy strategy = new DefaultStrategy();
        ABox abox = strategy.to_dllitefpxABox(this.getJSONfromFile("testABoxRolesAtTime1ToDL"));

		Iterator<ABoxRoleAssertion> iterator = abox.getABoxRoleAssertions().iterator();
	    String actual = new String();
	    while(iterator.hasNext()){
	    	ABoxRoleAssertion ci = iterator.next();
	    	NextFutureRole nfr = new NextFutureRole(ci);
	    	actual = actual.concat(nfr.toString() + "\n");    
	    }
	    
		assertEquals(this.checkExpectedResult("testABoxRolesAtTime1ToDL"), actual);
	}
	
	@Test
	@DisplayName("Roles at time N")
	public void testABoxRolesAtTimeNToDL() {
        DefaultStrategy strategy = new DefaultStrategy();
        strategy.to_dllitefpxABox(this.getJSONfromFile("testERvtSimpleTotalExclusiveISAtoDL"));

	}
	
}

