package it.gilia.tcrowd.cli;


import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

abstract class TCrowdRandomTDLRelatedCommand implements TCrowdCommand {
   
	@Option(type = OptionType.COMMAND, name = {"-ltbox", "--lengthTBox"}, title = "Concept Inclusion",
			description = "Number of Concept Inclusions (CIs) - length of TBox")
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			int ltbox;
	@Option(type = OptionType.COMMAND, name = {"-n", "--concepts"}, title = "Concepts",
			description = "Number of Concepts")
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			int n;
	@Option(type = OptionType.COMMAND, name = {"-lc", "--lengthConcept"}, title = "Length of Concepts",
			description = "Length of each Concept")
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			int lc;
	@Option(type = OptionType.COMMAND, name = {"-qm", "--qMax"}, title = "Cardinality of Roles",
			description = "Maximum Cardinality of Qualified Roles")
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			int qm;
	@Option(type = OptionType.COMMAND, name = {"-pt", "--probTemp"}, title = "Temporal Concepts",
			description = "Probability of generating Temporal Concepts")
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			int pt;
	@Option(type = OptionType.COMMAND, name = {"-pr", "--probRidig"}, title = "Rigid Roles",
			description = "Probability of generating Rigid Roles")
			@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
			int pr;
}
