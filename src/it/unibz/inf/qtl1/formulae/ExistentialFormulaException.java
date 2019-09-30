package it.unibz.inf.qtl1.formulae;

public class ExistentialFormulaException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExistentialFormulaException(){
		super("The given formula contains Existential quantifiers.");
	}

}
