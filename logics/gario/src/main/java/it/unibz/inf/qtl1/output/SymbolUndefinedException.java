package it.unibz.inf.qtl1.output;

public class SymbolUndefinedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SymbolUndefinedException(String symbolName) {
		super("Symbol for "+ symbolName+ " undefined.");
	}
}
