package it.gilia.cli;


import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

abstract class TCrowdEncodingERvtRelatedCommand implements TCrowdCommand {

    @Option(type = OptionType.COMMAND, name = {"-t", "--tmodel"}, title = "temporal model",
            description = "JSON file input containing a temporal model")
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    String tModel;
/**
    @Option(type = OptionType.COMMAND, name = {"-m", "--mapping"}, title = "mapping file",
            description = "Mapping file in R2RML (.ttl) or in Ontop native format (.obda)")
    @Required
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    String mappingFile;

    @Option(type = OptionType.COMMAND, name = {"-p", "--properties"}, title = "properties file",
            description = "Properties file")
    @Required
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    String propertiesFile;

    protected boolean isR2rmlFile(String mappingFile) {
        return !mappingFile.endsWith(".obda");
    }
**/
}
