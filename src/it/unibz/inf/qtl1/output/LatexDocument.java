package it.unibz.inf.qtl1.output;

import it.unibz.inf.qtl1.formulae.Formula;

/***
 * Defines an {@link OutputDocument} of type Latex, with macros to interpret the symbols defined in 
 * {@link LatexFormat}
 * 
 * @author Marco Gario
 *
 */
public class LatexDocument extends OutputDocument{
	String opening;
	
	public LatexDocument(Formula f, FormulaOutputFormat fmt) {
		super(f, fmt);
		init();
	}
	

	@Override
	public String getEnding() {
		return "\n"+"\\end{document}";
	}

	@Override
	public String getOpening() {
		return opening;
	}
	
	private void init(){
		opening="\\documentclass[a4paper,10pt]{article}"+"\n"+
				"\\usepackage[utf8]{inputenc}"+"\n"+
				"\\usepackage{pdfpages}"+"\n"+
				"\\usepackage{latexsym}"+"\n"+
				"\\usepackage{amssymb,amsthm,amsmath}"+"\n"+
				"\\usepackage{amsthm}"+"\n"+
				"\n"+

				"\\begin{document}"+"\n"+
				" \\newcommand{\\nxt}{{\\ensuremath\\raisebox{0.25ex}{\\text{\\scriptsize$\\bigcirc$}}}}"+"\n"+
				"\\newcommand{\\Rdiamond}{\\Diamond_{\\!F}}"+"\n"+
				"\\newcommand{\\Rbox}{\\Box_{\\!F}}"+"\n"+
				"\\newcommand{\\Rnext}{\\nxt_{\\!F}}"+"\n"+
				"\\newcommand{\\Ldiamond}{\\Diamond_{\\!P}}"+"\n"+
				"\\newcommand{\\Lbox}{\\Box_{\\!P}}"+"\n"+
				"\\newcommand{\\Lnext}{\\nxt_{\\!P}}"+"\n"+
				"\\newcommand{\\SVdiamond}{\\mathop{\\ooalign{$\\Diamond$ \\cr \\kern0.5ex"+"\n"+
				"    \\raisebox{0.35ex}{\\scalebox{0.7}{$*$}}} \\kern-0.9ex}}"+"\n"+
				"\\newcommand{\\SVbox}{\\mathop{\\ooalign{$\\Box$ \\cr \\kern0.42ex"+"\n"+
				"    \\raisebox{0.3ex}{\\scalebox{0.7}{$*$}}} \\kern-0.9ex}}"+"\n"+
				"\n";

	}
	

}
