package it.gilia.tcrowd.cli.dllite;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.help.Copyright;

import it.unibz.inf.dllite.DLLiteReasoner;
import it.unibz.inf.tdllitefpx.output.LatexOutputDocument;

import it.gilia.tcrowd.importer.OWLImport;

import org.semanticweb.owlapi.model.IRI;

import java.util.Map;
import java.util.Objects;


@Command(name = "SatisfiabilityChecking",
		 description = " Main mode of the tool ")
@Copyright(startYear = 2019,
		 holder = "Free University of Bozen-Bolzano and Universidad Nacional del Comahue")
public class TCrowdSatisfiabilityChecking extends TCrowdRandomRelatedCommand {

    @Override
    public void run() {

        try {
            Objects.requireNonNull(iri, "Ontology IRI must not be null");
            Objects.requireNonNull(solver, "Name of the backend SAT solver must not be null (BLACK or nuXmv)");
            //Objects.(abs, "Abstraction step must be specify for true or false");
            Objects.requireNonNull(pr, "A string for output files must be given");

			System.out.println("abs: " + abs);
            		
			IRI ontoiri = IRI.create(iri);
			OWLImport importer = new OWLImport();
			importer.load(ontoiri);
			importer.dlliteCI();
			importer.dlliteAbox();
	
			try{
				(new LatexOutputDocument(importer.getTBox())).toFile(pr + "TBox.tex");
			} catch (Exception e) {}


			DLLiteReasoner.checkKB(importer.getTBox(),
			                	   importer.getABox(), 
								   abs, 
								   pr,
								   solver);
	
			Map<String, String> stats = importer.getTBox().getStats();
			System.out.println("");
			System.out.println("------TBOX------");
			String key;
			key = "Basic Concepts:";
			System.out.println(key + stats.get(key));
			key = "Roles:";
			System.out.println(key + stats.get(key));
			key = "CIs:";
			System.out.println(key + stats.get(key));
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Error occurred during encoding: "
			+ e.getMessage());
			System.err.println("Debugging information for developers: ");
			e.printStackTrace();
		}

    }
}