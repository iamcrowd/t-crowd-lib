package it.gilia.client;


import com.github.rvesse.airline.annotations.Cli;


@Cli(name = "basic", 
description = "Provides a basic example CLI",
defaultCommand = GettingStarted.class, 
commands = { GettingStarted.class })
public class TCrowdClient {

	public static void main(String[] args) {
		 com.github.rvesse.airline.Cli<Runnable> cli = new com.github.rvesse.airline.Cli<>(TCrowdClient.class);
		 Runnable cmd = cli.parse(args);
		 cmd.run();
	}
}

