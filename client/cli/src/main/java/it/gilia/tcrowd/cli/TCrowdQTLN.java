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

@Command(name = "qtln",
        description = "Encode ERvt model into QTL formulae over natural numbers")
public class TCrowdQTLN extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON temporal model file must not be null");

    		String fileName="tdl2qtln";
    		
    		TBox tBox = new ExampleTDL().getTBox();
    		
    		TDLLiteFPXConverter converter = new TDLLiteFPXConverter(tBox);
    		Formula qtl = converter.getFormula(true);
    		
    		Formula qtlN =  qtl.makeTemporalStrict();
    		qtlN =  (new NaturalTranslator(qtl)).getTranslation();

    		System.out.println("QTL over N translation...");
    		(new LatexDocumentCNF(qtlN)).toFile(fileName+"_qtlN.tex");
    		

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
