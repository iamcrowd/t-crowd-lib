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

import it.gilia.tcrowd.encoding.DefaultStrategy;
import it.gilia.tcrowd.utils.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONException;

import java.util.Objects;

import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;


@Command(name = "tdllitefpx",
        description = "Encode ERvt model as a KB in TDL DL-Litefpx")
public class TCrowdTDLLiteFPX extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON temporal model file must not be null");
            
            InputStream is = new FileInputStream(tModel);
                       
            if (is == null) {
                throw new NullPointerException("Cannot find resource file " + tModel);
            }
            
            PathsManager pathMan = new PathsManager();
            String pathToTemp = pathMan.getPathToTmp(tModel);
    		String fileNameOut = pathToTemp+"tcrowdOut";
            
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            System.out.println(jsonTxt);

            JSONObject object = new JSONObject(jsonTxt);
    		
    		DefaultStrategy strategy = new DefaultStrategy();
            TBox tbox = strategy.to_dllitefpx(object);
            
            tbox.addExtensionConstraints();
    		
    		System.out.println("Saving in "+fileNameOut);
    		System.out.println("Original TBox...");
    		
    		(new LatexOutputDocument(tbox)).toFile(fileNameOut+"_tbox.tex");

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
