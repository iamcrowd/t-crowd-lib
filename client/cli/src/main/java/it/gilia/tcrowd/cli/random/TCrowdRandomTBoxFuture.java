package it.gilia.tcrowd.cli.random;

import com.github.rvesse.airline.annotations.Command;
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
import it.unibz.inf.tdllitefpx.TDLLiteNFPXConverter;
import it.unibz.inf.tdllitefpx.TDLLiteNFPXReasoner;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;

import it.unibz.inf.tdllitefpx.tbox.TD_LITE;
import it.unibz.inf.tdllitefpx.tbox.TD_LITE_N;

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


@Command(name = "RandomTBoxFuture",
description = " TBox on N -> QTL1 -> LTL. No past to future translation is required"
			  + "\n"
			  + "\t \t \t \t TBox with only future operators is randomly generated given the required parameters."
			  + "\n")

public class TCrowdRandomTBoxFuture extends TCrowdRandomRelatedCommand {
	
    @Override
    public void run() {

        try {
            Objects.requireNonNull(ltbox, "Number of Concept Inclusions must not be null");
            Objects.requireNonNull(n, "Number of Concepts must not be null");
            Objects.requireNonNull(lc, "Length of each Concept must not be null");
            Objects.requireNonNull(qm, "Maximum Cardinality of Qualified Roles must not be null");
            Objects.requireNonNull(pt, "Probability of generating Temporal Concepts must not be null");
            Objects.requireNonNull(pr, "Probability of generating Rigid Roles must not be null");
            
            
    		TD_LITE_N exTDLITE_N = new TD_LITE_N();
    		TBox tbox = new TBox();
    		tbox = exTDLITE_N.getTbox(ltbox, lc, n, qm, pt, pr);
                   	         	    
            TDLLiteNFPXReasoner.buildFOCheckTBoxSatisfiabilityOnlyFuture(
                    		tbox,
                    		true, 
                    		"random",
                    		true);
            
        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
    
}
