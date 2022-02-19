package it.gilia.tcrowd.cli.random;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

import it.unibz.inf.tdllitefpx.TDLLiteNABSFPXReasoner;
import it.unibz.inf.tdllitefpx.abox.ABox;

import it.unibz.inf.tdllitefpx.tbox.TD_LITE_N_AbsABox;

import java.util.Map;
import java.util.Objects;


@Command(name = "RandomAbsABox",
description = "Random ABox -> Abstracted ABox "
				+ "\n"
				+ "\t \t \t \t \t ABox is randomly generated with future operators and the required parameters.")

public class TCrowdRandomAbsABox extends TCrowdRandomAbstractionCommand {
	
	@Option(type = OptionType.COMMAND, name = {"-a", "--assertions"}, title = "Assertions",
			description = "Number of ABox Assertions")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer assertions;
	
	@Option(type = OptionType.COMMAND, name = {"-i", "--indiv"}, title = "Individual Names",
			description = "Number of ABox Individuals")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer indiv;

	@Option(type = OptionType.COMMAND, name = {"-s", "--sig"}, title = "Concepts and Roles",
		description = "Number of Concepts, Global Roles and Local Roles")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer S;

	@Option(type = OptionType.COMMAND, name = {"-t", "--tmax"}, title = "Time",
		description = "Maximum Time in interval [0, T-1]")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer T;

    @Override
    public void run() {

        try {
            Objects.requireNonNull(assertions, "Number of Assertions must not be null");
            Objects.requireNonNull(indiv, "Number of Individuals Names must not be null");
            Objects.requireNonNull(S, "Number of Concepts and Roles must not be null");
            Objects.requireNonNull(T, "Maximum Time must not be null");
            		
    		TD_LITE_N_AbsABox absABox = new TD_LITE_N_AbsABox();

			ABox a = new ABox();
			
			a = absABox.getABox(assertions, indiv, S, T);
			
			Map<String, Integer> statsABox = a.getStatsABox();
			System.out.println("");
			System.out.println("------TDLITE ABOX");
			
			String keyA;
			keyA="Concept_Assertion:";
			System.out.println(keyA+ statsABox.get(keyA));
			keyA="Role_Assertion:";
			System.out.println(keyA+ statsABox.get(keyA));
                    		
            TDLLiteNABSFPXReasoner.buildAbstract(a, 3);

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
