package it.gilia.tcrowd.cli;


import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

abstract class TCrowdEncodingERvtRelatedCommand implements TCrowdCommand {

    @Option(type = OptionType.COMMAND, name = {"-t", "--tmodel"}, title = "ERvt temporal model",
            description = "JSON file input containing an ERvt temporal model")
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    String tModel;
/**
    @Option(type = OptionType.COMMAND, name = {"-q", "--query"}, title = "query file",
            description = "Plain Query file (.txt)")
    @Required
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    String queryF;

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
