package it.gilia.tcrowd.cli;


import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

abstract class TCrowdRandomTDLRelatedCommand implements TCrowdCommand {
   
	@Option(type = OptionType.COMMAND, name = {"-ltbox", "--lengthTBox"}, title = "Concept Inclusion",
			description = "Number of Concept Inclusions (CIs) - length of TBox")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	int ltbox;
	
	@Option(type = OptionType.COMMAND, name = {"-n", "--concepts"}, title = "Concepts",
			description = "Number of Concepts")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	int n;
	
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
}
