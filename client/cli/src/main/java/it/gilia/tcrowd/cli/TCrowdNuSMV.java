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

@Command(name = "NuSMV",
        description = "Encode ERvt model into LTL formulae and return a LTL file together with a NuSMV file")
public class TCrowdNuSMV extends TCrowdEncodingERvtRelatedCommand {

    @Override
    public void run() {

        try {
            Objects.requireNonNull(tModel, "JSON temporal model file must not be null");

    		String fileName="tdl2NuSMV";
    		
    		TBox tBox = new ExampleTDL().getTBox();
    		
    		TDLLiteFPXConverter converter = new TDLLiteFPXConverter(tBox);
    		Formula qtl = converter.getFormula(true);
    		
    		Formula qtlN =  qtl.makeTemporalStrict();
    		qtlN =  (new NaturalTranslator(qtl)).getTranslation();

    		Formula ltl = qtlN.makePropositional();

    		System.out.println("LTL over N translation...");
    		(new LatexDocumentCNF(ltl)).toFile(fileName+"_ltl.tex");
    		
    		System.out.println("LTL Formula:");
    		System.out.println("Num of Propositions: "+ltl.getPropositions().size());
    		System.out.println("Num of Constants: "+ltl.getConstants().size());
    		
    		System.out.println("NuSMV file...");
    		(new NuSMVOutput(ltl)).toFile(fileName+".smv");
    		

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
