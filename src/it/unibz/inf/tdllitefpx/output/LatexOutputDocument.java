package it.unibz.inf.tdllitefpx.output;

import org.gario.code.output.FormattableObj;
import org.gario.code.output.OutputFormat;


public class LatexOutputDocument extends org.gario.code.output.OutputDocument{
	
	public LatexOutputDocument(FormattableObj f, OutputFormat fmt) {
		super(f, fmt);
		// TODO Auto-generated constructor stub
	}
	
	public LatexOutputDocument(FormattableObj f){
		super(f, new LatexFormat());
	}

	@Override
	protected StringBuilder getBody() {
		try{
			StringBuilder sb = new StringBuilder(f.toString(fmt));
		
			return sb;
		}catch(Exception ex){
			System.err.println(ex.getMessage());
			return null;
		}
	}

	@Override
	protected String getEnding() {
		return "\\}$ "+"\n"+"\\end{document}";
	}

	@Override
	protected String getOpening() {
		return opening+"\n $\\{$\\\\ \n$";
	}
	
	String opening="\\documentclass[a4paper,10pt]{article}"+"\n"+
	"\\usepackage[utf8]{inputenc}"+"\n"+
	"\\usepackage{pdfpages}"+"\n"+
	"\\usepackage{latexsym}"+"\n"+
	"\\usepackage{amssymb,amsthm,amsmath}"+"\n"+
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
