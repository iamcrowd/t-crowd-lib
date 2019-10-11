package it.gilia.cli;

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

import java.util.Objects;

@Command(name = "tdllitefpx",
        description = "Encode ERvt model as a KB in TDL DL-Litefpx")
public class TCrowdTDLLiteFPX extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON temporal model file must not be null");

    		String fileName="tdl2TDLLiteFPX";
    		
    		TBox tBox = new ExampleTDL().getTBox();
    		
    		System.out.println("Saving in "+fileName);
    		
    		System.out.println("Original TBox...");
    		(new LatexOutputDocument(tBox)).toFile(fileName+"_tbox.tex");

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
