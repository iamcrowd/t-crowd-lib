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


@Command(name = "ERvtTBoxConceptSat",
description = "\t \t \t Encode ERvt model into LTL|PLTL formulae (ABox not considering here) and return"
				+ "\n "
				+ "\t \t \t a file to feed a sat solver"
				+ "\n"
        		+ "\t \t \t * the query is given as an input file. "
				+ "\n"
        		+ "\t \t \t * If query file is empty, KB is only checked for satisifiability. KB = <TBox,{}> " 
        		+ "\n"
        		+ "\t \t \t * Otherwise, query must be a concept to be checked."
        		+ "\n"
        		+ "\t \t \t * Flag pf is optional to reduce to pure future QTL1. "
        		+ "\n"
        		+ "\t \t \t * If flag is not specified, QTL1 could include some past operators"
        		+ "\n"
        		+ "\t \t \t * option -s requires entering a solver name (NuSMV|Aalta) "
        		+ "\n"
        		+ "\t \t \t * if pf is not specified, only NuSMV files will be generated")

public class TCrowdERvtTBoxConceptSat extends TCrowdEncodingERvtRelatedCommand {
	
    @Option(type = OptionType.COMMAND, name = {"-t", "--tmodel"}, title = "ERvt temporal model",
            description = "JSON file input containing an ERvt temporal model")
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    String tModel;
    
	@Option(type = OptionType.COMMAND, name = {"-pf", "--purefuture"}, title = "Pure Future Operators",
			description = "Flag to set reduction to QTL1 using only pure future operators")
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	boolean pf;
	
	@Option(type = OptionType.COMMAND, name = {"-q", "--query"}, title = "query file",
			description = "Plain Query file (.txt)")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
	String queryF;
	
	@Option(type = OptionType.COMMAND, name = {"-s", "--solver"}, title = "solver",
			description = "Solver (NuSMV|Aalta)")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	String solver;

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON ERvt temporal model file must not be null");
            Objects.requireNonNull(queryF, "Query file must not be null");
            Objects.requireNonNull(solver, "Solver (NuSMV|Aalta) must be specified");
    		
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
                    
                    InputStream query = new FileInputStream(queryF);
                    
                    if (query == null) {
                        throw new NullPointerException("Cannot find query file " + query);
                    }else{

                    	BufferedReader reader = new BufferedReader(new FileReader(queryF));
                    	String line = reader.readLine();
                   	    
                    	if (line == null) { /*Check for TBox satisfiability */
                    	    TDLLiteFPXReasoner.buildCheckSatisfiability(
                    	    		tbox,
                    	    		true, 
                    	    		fileNameOut,
                    	    		pf,
                    	    		solver,
                    	    		false);
                    	    
                    	}else { /*Check for Concept satisfiability.*/
                    		System.out.println(line);

                    		Concept acpt = strategy.giveMeAconcept(line); 
                    		
                    		TDLLiteFPXReasoner.buildCheckConceptSatisfiability(
                    				tbox,
                    				acpt,
                    				true, 
                    				fileNameOut,
                    				pf,
                    				solver,
                    				false);
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
