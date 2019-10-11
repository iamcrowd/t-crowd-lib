package it.gilia.cli;

import com.github.rvesse.airline.help.cli.bash.BashCompletionGenerator;
import com.github.rvesse.airline.model.GlobalMetadata;

import java.io.IOException;

public class TCrowdBashCompletionGenerator {


    public static void main(String[] args) throws IOException {
        GlobalMetadata<TCrowdCommand> metadata = TCrowd.getTCrowdCommandCLI().getMetadata();

        BashCompletionGenerator<TCrowdCommand> generator = new BashCompletionGenerator<>();

        generator.usage(metadata, System.out);
    }
}
