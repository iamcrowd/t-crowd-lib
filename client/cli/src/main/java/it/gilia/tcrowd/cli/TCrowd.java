package it.gilia.tcrowd.cli;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.parser.errors.*;

public class TCrowd {

    public static void main(String... args) {

        Cli<TCrowdCommand> tcrowdCommandCLI = getTCrowdCommandCLI();

        TCrowdCommand command;

        try {
            command = tcrowdCommandCLI.parse(args);
            command.run();
        } catch (ParseCommandMissingException e) {
            main("help");
        } catch (ParseCommandUnrecognizedException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Run `tcrowd help` to see the help");
        } catch (ParseArgumentsUnexpectedException | ParseOptionMissingException e) {
            System.err.println("Error: " + e.getMessage());
            String commandName = args[0];
            System.err.format("Run `tcrowd help %s` to see the help for the command `%s`\n", commandName, commandName);
        } catch (ParseException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Run `tcrowd help` to see the help");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	static Cli<TCrowdCommand> getTCrowdCommandCLI() {
        //noinspection unchecked
        CliBuilder<TCrowdCommand> builder = Cli.<TCrowdCommand>builder("tcrowd")
                .withDescription("t-crowd api for Reasoning over Temporal Conceptual Models")
                .withCommands(
                        /**
                         * visible commands
                         */
                        TCrowdVersion.class,
                        TCrowdHelp.class,
                        TCrowdTDLLiteFPX.class,
                        /* TCrowdQTLZ.class,
                        TCrowdQTLN.class,
                        TCrowdLTL.class,
                        TCrowdAboxSat.class, */
                        TCrowdERvtTBoxABoxSatLTL.class,
                        TCrowdERvtTBoxABoxSatPLTL.class,
                        TCrowdERvtTBoxConceptSat.class,
                        TCrowdERvtTBoxABoxSatFO.class,
                        TCrowdRandomTBox.class,
                        //TCrowdRandomTBoxABoxFuture.class,
                        TCrowdRandomTBoxABoxPLTL.class,
                        TCrowdRandomTBoxABoxPastAndFuture.class
                );
        return builder.build();
    }

}
