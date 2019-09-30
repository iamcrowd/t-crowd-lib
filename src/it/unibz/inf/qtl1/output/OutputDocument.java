package it.unibz.inf.qtl1.output;


import java.io.BufferedWriter;
import java.io.FileWriter;

import it.unibz.inf.qtl1.formulae.Formula;

/***
 * Defines a template for outputting a formula.
 * Derived classes like {@link LatexDocumentCNF} an {@link NuSMVOutput} show an
 * example of usage.
 * @author Marco Gario
 *
 */
public abstract class OutputDocument {
	protected Formula f;
	protected FormulaOutputFormat fmt;
	
	public OutputDocument(Formula f, FormulaOutputFormat fmt){
		this.f=f;
		this.fmt=fmt;
	}
	
	public abstract String getOpening();
	public abstract String getEnding();
	
	public String getOutput(){
		StringBuilder out=new StringBuilder();
		
		out.append(getOpening());
		try{
			out.append(f.getFormattedFormula(fmt));
		}catch (Exception ex){
			ex.printStackTrace();
			out.append(ex.getMessage());
		}
		out.append(getEnding());
		
		return out.toString();
	}
	
	public void toFile(String fileName) throws Exception{
	    // Create file 
	    
		FileWriter fstream = new FileWriter(fileName);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write(this.getOutput());

	    //Close the output stream
	    out.close();
		    
	}
	
	public String toString(){ return this.getOutput(); }
}
