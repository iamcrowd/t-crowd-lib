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


@Command(name = "TBoxABoxSatLTL",
description = " TBox|ABox -> QTL1 -> QTLN -> LTL "
				+ "\n"
				+ "\t \t \t Encode both ERvt model and Temporal Data into a LTL Pure Future formulae and return a LTL file"
				+ "\n \t \t \t" + "to feed sat solvers"
				+ "\n"
				+ "\t \t  \t * If ABox is empty, only TBox is checked for SAT"
				+ "\n"
        		+ "\t \t \t * option -s requires entering a solver name (NuSMV|Aalta)")

public class TCrowdTBoxABoxSatLTL extends TCrowdEncodingERvtRelatedCommand {
	
	@Option(type = OptionType.COMMAND, name = {"-a", "--tdata"}, title = "Temporal Data",
			description = "JSON file input containing temporal data")
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			String tData;
	
	@Option(type = OptionType.COMMAND, name = {"-s", "--solver"}, title = "solver",
			description = "Solver (NuSMV|Aalta)")
			@Required
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			String solver;

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON ERvt temporal model file must not be null");
            Objects.requireNonNull(solver, "Solver (NuSMV|Aalta) must be specified");
            Objects.requireNonNull(tData, "JSON temporal data file must not be null");
    		
            InputStream is = new FileInputStream(tModel);
            
            if (is == null) {
                throw new NullPointerException("Cannot find resource file " + tModel);
            }else {
            	BufferedReader reader_t = new BufferedReader(new FileReader(tModel));
            	String line_t = reader_t.readLine();
            	
               	if (line_t == null) {
                    throw new NullPointerException("TBox is empty " + tModel);
               	}else {
                    PathsManager pathMan = new PathsManager();
                    String pathToTemp = pathMan.getPathToTmp(tModel);
            		String fileNameOut = pathToTemp+"tcrowdOut";
                    
                    String jsonTxt = IOUtils.toString(is, "UTF-8");
                    System.out.println(jsonTxt);

                    JSONObject objectModel = new JSONObject(jsonTxt);
            		
            		DefaultStrategy strategy = new DefaultStrategy();
                    TBox tbox = strategy.to_dllitefpx(objectModel);
                 
                    InputStream td = new FileInputStream(tData);
                    
                    if (td == null) {
                        throw new NullPointerException("Cannot find resource file " + tData);
                    }else {
                    	BufferedReader reader = new BufferedReader(new FileReader(tData));
                    	String line = reader.readLine();
                    	
                    	if (line == null) {
                    		/*Check for TBox satisfiability*/
                    		TDLLiteFPXReasoner.buildCheckSatisfiability(
                    				tbox,
                    				true, 
                    				fileNameOut,
                    				true,
                    				solver);
                    	    
                    	}else { /*Check for TBox and ABox satisfiability.*/
                    		String jsonTxtData = IOUtils.toString(td, "UTF-8");
                        	System.out.println(jsonTxtData);
                        	JSONObject objectData = new JSONObject(jsonTxtData);

                        	ABox abox = strategy.to_dllitefpxABox(objectData);
                    		
                    		TDLLiteFPXReasoner.buildCheckABoxLTLSatisfiability(
                    				tbox,
                    				true, 
                    				fileNameOut,
                    				abox,
                    				true,
                    				solver);
                    	}
                    }
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
