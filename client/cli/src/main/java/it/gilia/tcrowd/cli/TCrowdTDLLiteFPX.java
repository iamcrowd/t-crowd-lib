package it.gilia.tcrowd.cli;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

import it.unibz.inf.tdllitefpx.ExampleTDL;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.abox.ABox;

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


@Command(name = "ERvtTBoxTDLLiteFPX",
        description = "Encode both ERvt model and Temporal data as a KB <TBox,ABox> in TDL DL-Litefpx.")
public class TCrowdTDLLiteFPX extends TCrowdEncodingERvtRelatedCommand {
	
    @Option(type = OptionType.COMMAND, name = {"-t", "--tmodel"}, title = "ERvt temporal model",
            description = "JSON file input containing an ERvt temporal model")
	@Required
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    String tModel;
	
	@Option(type = OptionType.COMMAND, name = {"-a", "--tdata"}, title = "Temporal Data",
			description = "JSON file input containing temporal data")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
	String tData;

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON ERvt temporal model file must not be null");
            Objects.requireNonNull(tData, "JSON Temporal data file must not be null");
            
            InputStream is = new FileInputStream(tModel);
                       
            if (is == null) {
                throw new NullPointerException("Cannot find resource file " + tModel);
            }
            
            InputStream data = new FileInputStream(tData);
            
            if (data == null) {
                throw new NullPointerException("Cannot find resource file " + tData);
            }
            
            PathsManager pathMan = new PathsManager();
            String pathToTemp = pathMan.getPathToTmp(tModel);
    		String fileNameOut = pathToTemp+"tcrowdOut";
            
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            System.out.println(jsonTxt);
            
            String jsonTxtData = IOUtils.toString(data, "UTF-8");
            System.out.println(jsonTxtData);

            JSONObject object = new JSONObject(jsonTxt);
            JSONObject objectData = new JSONObject(jsonTxtData);
    		
    		DefaultStrategy strategy = new DefaultStrategy();
            TBox tbox = strategy.to_dllitefpx(object);
            ABox abox = strategy.to_dllitefpxABox(objectData);
            
            //tbox.addExtensionConstraints();
		    abox.addExtensionConstraintsABox(tbox);
    		
    		System.out.println("Saving in "+fileNameOut);
    		System.out.println("Original TBox...");
       		System.out.println("Original ABox...");
    		
    		(new LatexOutputDocument(tbox)).toFile(fileNameOut+"_tbox.tex");
    		(new LatexOutputDocument(abox)).toFile(fileNameOut+"_abox.tex");

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
