package it.unibz.inf.ontop.cli;

import com.github.rvesse.airline.help.cli.bash.BashCompletionGenerator;
import com.github.rvesse.airline.model.GlobalMetadata;

import java.io.IOException;

public class OntopBashCompletionGenerator {


    public static void main(String[] args) throws IOException {
        GlobalMetadata<TCrowdCommand> metadata = Ontop.getOntopCommandCLI().getMetadata();

        BashCompletionGenerator<TCrowdCommand> generator = new BashCompletionGenerator<>();

        generator.usage(metadata, System.out);
    }
}
