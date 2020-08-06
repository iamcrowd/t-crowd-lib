package it.gilia.tcrowd.cli;

import com.github.rvesse.airline.annotations.Command;
//import com.github.rvesse.airline.annotations.Option;
//import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.ExampleTDL;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.TDLLiteFPXReasoner;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;

import it.unibz.inf.tdllitefpx.tbox.TD_LITE;

import it.gilia.tcrowd.encoding.DefaultStrategy;
import it.gilia.tcrowd.utils.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


@Command(name = "RandomTBoxABoxSatPLTL",
description = " TBox|ABox -> QTL1 -> PLTL (only NuSMV)"
			  + "\n"
			  + "\t \t \t \t TBox is randomly generated given the required parameters. Output LTL includes Past Operators"
			  + "\n"
			  + "\t \t \t \t \t * If ABox is empty, only TBox is checked for SAT")

public class TCrowdRandomTBoxABoxPLTL extends TCrowdRandomTDLRelatedCommand {
	
	@Option(type = OptionType.COMMAND, name = {"-a", "--tdata"}, title = "Temporal Data",
			description = "JSON file input containing temporal data")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
	String tData;
	

    @Override
    public void run() {

        try {
            Objects.requireNonNull(ltbox, "Number of Concept Inclusions must not be null");
            Objects.requireNonNull(n, "Number of Concepts must not be null");
            Objects.requireNonNull(lc, "Length of each Concept must not be null");
            Objects.requireNonNull(qm, "Maximum Cardinality of Qualified Roles must not be null");
            Objects.requireNonNull(pt, "Probability of generating Temporal Concepts must not be null");
            Objects.requireNonNull(pr, "Probability of generating Rigid Roles must not be null");
            
            Objects.requireNonNull(tData, "JSON temporal data file must not be null");
            
    		TD_LITE exTDLITE = new TD_LITE();
    		TBox tbox = new TBox();
    		tbox = exTDLITE.getTbox(ltbox, n, lc, qm, pt, pr);
    		
            InputStream td = new FileInputStream(tData);
                    
            if (td == null) {
            	throw new NullPointerException("Cannot find resource file " + tData);
            } else {
            	BufferedReader reader = new BufferedReader(new FileReader(tData));
                String line = reader.readLine();
                
                PathsManager pathMan = new PathsManager();
                String pathToTemp = pathMan.getPathToTmp(tData);
        		String fileNameOut = pathToTemp+"random";
                   	    
                if (line == null) { /*Check only for TBox satisfiability if ABox is empty*/
                	    TDLLiteFPXReasoner.buildCheckSatisfiability(
                   	    		tbox,
                   	    		true, 
                   	    		fileNameOut,
                   	    		false,
                   	    		"NuSMV",
                   	    		true);
                    	    
                } else { /*Check for TBox and ABox satisfiability */
                   	String jsonTxtData = IOUtils.toString(td, "UTF-8");
                    System.out.println(jsonTxtData);
                    JSONObject objectData = new JSONObject(jsonTxtData);

                    DefaultStrategy strategy = new DefaultStrategy();
                    ABox abox = strategy.to_dllitefpxABox(objectData);
                        	    
                    TDLLiteFPXReasoner.buildCheckABoxtSatisfiability(
                    		tbox,
                    		true, 
                    		fileNameOut,
                    		abox,
                    		true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
