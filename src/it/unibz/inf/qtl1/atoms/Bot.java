package it.unibz.inf.qtl1.atoms;

public class Bot extends Proposition {

	static Bot bot = new Bot();
	
	public static Bot getStatic(){return bot;}
	
	public Bot(String name) {
		super(name);
	}

	public Bot() {
		super("bot");
	}


}
