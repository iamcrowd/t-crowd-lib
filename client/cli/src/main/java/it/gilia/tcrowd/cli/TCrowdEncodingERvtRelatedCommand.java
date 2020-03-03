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
   
}
