package it.gilia.tcrowd.cli.random;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

import it.unibz.inf.tdllitefpx.TDLLiteNABSFPXReasoner;
import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.tbox.TDLLiteN_TBox_AbsABox;

import java.util.Map;
import java.util.Objects;


@Command(name = "GuessTBoxABox",
description = "Random TBox, ABox -> Abstracted KB for Experiments"
				+ "\n"
				+ "\t \t \t \t \t TBox and ABox are randomly generated with future operators and the required parameters.")

public class GuessTBoxABox extends TCrowdRandomAbstractionCommand {

	// TBox
	@Option(type = OptionType.COMMAND, name = {"-ltbox", "--lengthTBox"}, title = "Concept Inclusion",
			description = "Number of Concept Inclusions (CIs) - length of TBox")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	int ltbox;
	
	@Option(type = OptionType.COMMAND, name = {"-lc", "--lengthConcept"}, title = "Length of Concepts",
			description = "Length of each Concept")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	int lc;
	
	@Option(type = OptionType.COMMAND, name = {"-qm", "--qMax"}, title = "Cardinality of Roles",
			description = "Maximum Cardinality of Qualified Roles")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	int qm;
	
	@Option(type = OptionType.COMMAND, name = {"-pt", "--probTemp"}, title = "Temporal Concepts",
			description = "Probability of generating Temporal Concepts")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	int pt;
	
	@Option(type = OptionType.COMMAND, name = {"-pr", "--probRidig"}, title = "Rigid Roles",
			description = "Probability of generating Rigid Roles")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	int pr;

	// ABox
	@Option(type = OptionType.COMMAND, name = {"-a", "--assertions"}, title = "Assertions",
			description = "Number of ABox Assertions")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer assertions;
	
	@Option(type = OptionType.COMMAND, name = {"-i", "--individuals"}, title = "Individual Names",
			description = "Number of ABox Individuals")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer I;

	@Option(type = OptionType.COMMAND, name = {"-n", "--nc"}, title = "Concepts and Roles",
		description = "Number of Concepts, Global Roles and Local Roles")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer N;

	@Option(type = OptionType.COMMAND, name = {"-t", "--tmax"}, title = "Time",
		description = "Maximum Time in interval [0, T-1]")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer T;

    @Override
    public void run() {

        try {
			// TBox
			Objects.requireNonNull(ltbox, "Number of Concept Inclusions must not be null");
            Objects.requireNonNull(lc, "Length of each Concept must not be null");
            Objects.requireNonNull(qm, "Maximum Cardinality of Qualified Roles must not be null");
            Objects.requireNonNull(pt, "Probability of generating Temporal Concepts must not be null");
            Objects.requireNonNull(pr, "Probability of generating Rigid Roles must not be null");

			// ABox
            Objects.requireNonNull(assertions, "Number of Assertions must not be null");
            Objects.requireNonNull(I, "Number of Individuals Names must not be null");
            Objects.requireNonNull(N, "Number of Concepts and Roles must not be null");
            Objects.requireNonNull(T, "Maximum Time must not be null");


            		
    		TDLLiteN_TBox_AbsABox tboxAbsABox = new TDLLiteN_TBox_AbsABox();

			TBox t = new TBox();
			ABox a = new ABox();

				
			t = tboxAbsABox.getFTBox(ltbox, lc, N, qm, pr, pt);

			System.out.println("Start: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
			a = tboxAbsABox.getABox(assertions, N, I, T);
			System.out.println("Used: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
			
			Map<String, Integer> statsABox = a.getStatsABox();
			System.out.println("");
			System.out.println("------TDLITE ABOX");
			
			String keyA;
			keyA="Concept_Assertions:";
			System.out.println(keyA+ statsABox.get(keyA));
			keyA="Role_Assertions:";
			System.out.println(keyA+ statsABox.get(keyA));
                    		
            	//TDLLiteNABSFPXReasoner.buildCheckTBoxAbsABoxSAT(t, true, "test", a);

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
