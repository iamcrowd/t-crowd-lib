package org.gario.code.output;

public interface FormattableObj {
	public String toString(OutputFormat fmt) throws SymbolUndefinedException;
}
