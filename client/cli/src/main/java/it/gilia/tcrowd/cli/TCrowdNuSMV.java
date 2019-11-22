package it.gilia.tcrowd.cli;

import com.github.rvesse.airline.annotations.Command;
//import com.github.rvesse.airline.annotations.Option;
//import com.github.rvesse.airline.annotations.OptionType;

import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.tdllitefpx.ExampleTDL;
import it.unibz.inf.tdllitefpx.TDLLiteFPXConverter;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;

import it.gilia.tcrowd.encoding.DefaultStrategy;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONException;

import java.util.Objects;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;


@Command(name = "NuSMV",
        description = "Encode ERvt model into LTL formulae and return a LTL file together with a NuSMV file")
public class TCrowdNuSMV extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON temporal model file must not be null");

    		String fileNameOut="tcrowdOut";
    		
            InputStream is = new FileInputStream(tModel);
            
            if (is == null) {
                throw new NullPointerException("Cannot find resource file " + tModel);
            }
            
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            System.out.println(jsonTxt);

            JSONObject object = new JSONObject(jsonTxt);
    		
    		DefaultStrategy strategy = new DefaultStrategy();
            TBox tbox = strategy.to_dllitefpx(object);
    		
    		TDLLiteFPXConverter converter = new TDLLiteFPXConverter(tbox);
    		Formula qtl = converter.getFormula(true);
    		
    		System.out.println("QTL translation...");
    		(new LatexDocumentCNF(qtl)).toFile(fileNameOut+"_qtl.tex");
    		
    		Formula qtlN =  qtl.makeTemporalStrict();
    		qtlN =  (new NaturalTranslator(qtl)).getTranslation();
    		
    		System.out.println("QTL over N translation...");
    		(new LatexDocumentCNF(qtlN)).toFile(fileNameOut+"_qtlN.tex");

    		Formula ltl = qtlN.makePropositional();
    		
    		System.out.println("LTL over N translation...");
    		(new LatexDocumentCNF(ltl)).toFile(fileNameOut+"_ltl.tex");
    		
    		System.out.println("LTL Formula:");
    		System.out.println("Num of Propositions: "+ltl.getPropositions().size());
    		System.out.println("Num of Constants: "+ltl.getConstants().size());
    		
    		System.out.println("NuSMV file...");
    		(new NuSMVOutput(ltl)).toFile(fileNameOut+".smv");
    		

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
