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


@Command(name = "NuSMV",
        description = "Encode ERvt model and Temporal Data into LTL formulae and return a LTL file together with a NuSMV file including"
        		+ "the query given as an input file. If query file is empty, KB is to be checked for satisifiability."
        		+ "Otherwise, query must be a concept to be checked.")

public class TCrowdNuSMV extends TCrowdEncodingERvtRelatedCommand {
	
	@Option(type = OptionType.COMMAND, name = {"-q", "--query"}, title = "query file",
			description = "Plain Query file (.txt)")
			@Required
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			String queryF;

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON ERvt temporal model file must not be null");
            Objects.requireNonNull(tData, "JSON temporal data file must not be null");
            Objects.requireNonNull(queryF, "Query file must not be null");
    		
            InputStream is = new FileInputStream(tModel);
            
            if (is == null) {
                throw new NullPointerException("Cannot find resource file " + tModel);
            }
            
            InputStream td = new FileInputStream(tData);
            
            if (td == null) {
                throw new NullPointerException("Cannot find resource file " + tModel);
            }
            
            PathsManager pathMan = new PathsManager();
            String pathToTemp = pathMan.getPathToTmp(tModel);
    		String fileNameOut = pathToTemp+"tcrowdOut";
            
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            System.out.println(jsonTxt);
            
            String jsonTxtData = IOUtils.toString(td, "UTF-8");
            System.out.println(jsonTxtData);

            JSONObject objectModel = new JSONObject(jsonTxt);
            JSONObject objectData = new JSONObject(jsonTxtData);
    		
    		DefaultStrategy strategy = new DefaultStrategy();
            TBox tbox = strategy.to_dllitefpx(objectModel);
            ABox abox = strategy.to_dllitefpxABox(objectData);
            
            InputStream query = new FileInputStream(queryF);
            
            if (query == null) {
                throw new NullPointerException("Cannot find query file " + query);
            }else{

            	BufferedReader reader = new BufferedReader(new FileReader(queryF));
            	String line = reader.readLine();
           	    
            	if (line == null) { /*Check for TBox satisfiability */
            	    System.out.println("No errors, and file empty");
            	    
            	    TDLLiteFPXReasoner.buildCheckABoxtSatisfiability(
            	    		tbox,
            	    		true, 
            	    		fileNameOut,
            	    		abox);
            	    
            	}else { /*Check for Concept satisfiability */
            		System.out.println(line);

            		Concept acpt = strategy.giveMeAconcept(line); 
            		TDLLiteFPXReasoner.buildCheckConceptSatisfiability(
            				tbox,
            				acpt,
            				true, 
            				fileNameOut);
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
