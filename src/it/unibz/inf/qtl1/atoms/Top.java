package it.unibz.inf.qtl1.atoms;

public class Top extends Proposition {

	static Top top = new Top();
	
	public static Top getStatic(){return top;}
	public Top(String name) {
		super(name);
	}

	public Top() {
		super("T");
	}


}
