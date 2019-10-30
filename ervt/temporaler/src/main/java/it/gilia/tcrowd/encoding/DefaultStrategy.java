package it.gilia.tcrowd.encoding;

/**
 * Define a strategy for encoding ERvt models into Temporal DL.
 * 
 * @see Read paper "A Cookbook for Temporal Conceptual Data Modelling" (Artale et.al) for more details about formalisation. 
 * 
 * 
 */


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DefaultStrategy{
	
	
	public DefaultStrategy() {
		
	}
	
	/**
	 * 
	 * @param ervt_json A JSON object representing an ERvt temporal model.
	 */
	public void to_dllitefpx(String ervt_json) {
		//https://www.mkyong.com/java/json-simple-example-read-and-write-json/
		//JSONParser parser = new JSONParser();
		//JSONObject jsonObject = (JSONObject)parser.parse(ervt_json);
      //  System.out.println("Parsing JSON..."+jsonObject);
        System.out.println("Parsing JSON..."+ervt_json);

  /*      String entities = (String) jsonObject.get("entities");
        System.out.println(entities);

        String attributes = (String) jsonObject.get("attributes");
        System.out.println(attributes);

        String links = (String) jsonObject.get("links");
        System.out.println(links); */

        // loop array
        // JSONArray msg = (JSONArray) jsonObject.get("messages");
        // Iterator<String> iterator = msg.iterator();
        // while (iterator.hasNext()) {
           // System.out.println(iterator.next());
        //}
		
	}
	
}
