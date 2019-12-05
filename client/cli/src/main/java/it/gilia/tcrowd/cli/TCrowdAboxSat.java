package it.gilia.tcrowd.cli;

import com.github.rvesse.airline.annotations.Command;
//import com.github.rvesse.airline.annotations.Option;
//import com.github.rvesse.airline.annotations.OptionType;

//import it.unibz.inf.qtl1.NaturalTranslator;
//import it.unibz.inf.qtl1.formulae.Formula;
//import it.unibz.inf.qtl1.output.LatexDocumentCNF;
//import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.ExampleTDL;
//import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;

import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;

import it.unibz.inf.tdllitefpx.abox.Abox;
import it.unibz.inf.tdllitefpx.abox.AboxConceptAssertion;

import it.unibz.inf.tdllitefpx.TestAbox1;

import it.gilia.tcrowd.encoding.DefaultStrategy;
import it.gilia.tcrowd.utils.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONException;

import java.util.Map;
import java.util.Objects;

import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;


@Command(name = "abox",
        description = "Check for Abox Consistency - Sabiha's code")
public class TCrowdAboxSat extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            
    		TestAbox1 exTDL = new TestAbox1();
    		
    		/*	TDLLiteFPXReasoner.buildCheckSatisfiability(
    					exTDL.getTBox(),
    					true, 
    					"Status5");  */
    			exTDL.getABox();
    			TDLLiteFPXReasoner.buildCheckAboxtSatisfiability(
    					exTDL.getTBox(),
    					true, 
    					"Abox1",exTDL.getABox());
    			
    			Map<String, String> stats = exTDL.getTBox().getStats();
    			
    			String key;
    			key="Basic Concepts:";
    			System.out.println(key+ stats.get(key));
    			key="Roles:";
    			System.out.println(key+ stats.get(key));
    			key="CIs:";
    			System.out.println(key+ stats.get(key));
        		

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
