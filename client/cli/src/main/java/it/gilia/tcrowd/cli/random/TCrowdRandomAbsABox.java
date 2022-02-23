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
description = "Random ABox -> Abstracted ABox for Experiments"
				+ "\n"
				+ "\t \t \t \t \t ABox is randomly generated with future operators and the required parameters.")

public class TCrowdRandomAbsABox extends TCrowdRandomAbstractionCommand {
	
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

	@Option(type = OptionType.COMMAND, name = {"-it", "--iteration"}, title = "Number of Iterations for Experiments",
	description = "Number of batches generated for s, t and i")
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	Integer B;

    @Override
    public void run() {

        try {
            Objects.requireNonNull(assertions, "Number of Assertions must not be null");
            Objects.requireNonNull(I, "Number of Individuals Names must not be null");
            Objects.requireNonNull(N, "Number of Concepts and Roles must not be null");
            Objects.requireNonNull(T, "Maximum Time must not be null");
			//Objects.requireNonNull(B, "Maximum Time must not be null");

			if (Objects.isNull(B)){
            		
    			TD_LITE_N_AbsABox absABox = new TD_LITE_N_AbsABox();

				ABox a = new ABox();
			
				a = absABox.getABox(assertions, I, N, T);
			
				Map<String, Integer> statsABox = a.getStatsABox();
				System.out.println("");
				System.out.println("------TDLITE ABOX");
			
				String keyA;
				keyA="Concept_Assertions:";
				System.out.println(keyA+ statsABox.get(keyA));
				keyA="Role_Assertions:";
				System.out.println(keyA+ statsABox.get(keyA));
                    		
            	TDLLiteNABSFPXReasoner.buildAbstract(a, 3);
			} else {
				for (int i = 0; i < B; i++) {
					TD_LITE_N_AbsABox absABox = new TD_LITE_N_AbsABox();

					ABox a = new ABox();
				
					a = absABox.getABox(assertions, I, N, T);
				
					Map<String, Integer> statsABox = a.getStatsABox();
					System.out.println("");
					System.out.println("------TDLITE ABOX");
				
					String keyA;
					keyA="Concept_Assertion:";
					System.out.println(keyA+ statsABox.get(keyA));
					keyA="Role_Assertion:";
					System.out.println(keyA+ statsABox.get(keyA));
								
					TDLLiteNABSFPXReasoner.buildAbstract(a, 3);
				}
			}

        } catch (Exception e) {
            System.err.println("Error occurred during encoding: "
                    + e.getMessage());
            System.err.println("Debugging information for developers: ");
            e.printStackTrace();
        }

    }
}
