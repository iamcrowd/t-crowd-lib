package it.gilia.tcrowd.cli.dllite;

import it.gilia.tcrowd.cli.*;

import javax.annotation.Nullable;

import com.github.rvesse.airline.annotations.DefaultOption;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.MutuallyExclusiveWith;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

abstract class TCrowdRandomRelatedCommand implements TCrowdCommand {
   
	@Option(type = OptionType.COMMAND, name = {"-IRI", "--ontologyIRI"}, title = "Ontology IRI",
			description = "International Resource Identifier (IRI) of the input ontology")
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	@MutuallyExclusiveWith(tag = "input")
	@Nullable // optional
	String iri;

	@Option(type = OptionType.COMMAND, name = {"-f", "--ontology"}, title = "ontology.owl",
			description = "OWL ontology file")
	@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
	@MutuallyExclusiveWith(tag = "input")
	@Nullable // optional
	protected String owlFile;
	
	@Option(type = OptionType.COMMAND, name = {"-s", "--solver"}, title = "SAT Solver",
			description = "Name of the SAT solver in the backend of the DL Reasoner (BLACK|nuXmv)")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	String solver;
	
	@Option(type = OptionType.COMMAND, name = {"-abs", "--abstraction"}, title = "ABox Abstraction",
			description = "Enable/Disable the ABox abstraction step. Default: true")
	public boolean abs;
	
	@Option(type = OptionType.COMMAND, name = {"-p", "--pref"}, title = "Prefix of the output files",
			description = "A String to identify output files")
	@Required
	@BashCompletion(behaviour = CompletionBehaviour.NONE)
	String pr;

}
