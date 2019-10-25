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



import java.util.Objects;

@Command(name = "qtlz",
        description = "Encode ERvt model into QTL formulae over integers")
public class TCrowdQTLZ extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON temporal model file must not be null");

    		String fileName="tdl2qtlz";
    		
    		TBox tBox = new ExampleTDL().getTBox();
    		
    		TDLLiteFPXConverter converter = new TDLLiteFPXConverter(tBox);
    		Formula qtl = converter.getFormula(true);
    		
    		System.out.println("QTL translation...");
    		(new LatexDocumentCNF(qtl)).toFile(fileName+"_qtl.tex");
    		

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
