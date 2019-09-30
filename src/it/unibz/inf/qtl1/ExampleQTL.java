package it.unibz.inf.qtl1;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.ConjunctiveFormula;
import it.unibz.inf.qtl1.formulae.Formula;
import it.unibz.inf.qtl1.formulae.ImplicationFormula;
import it.unibz.inf.qtl1.formulae.NegatedFormula;
import it.unibz.inf.qtl1.formulae.quantified.UniversalFormula;
import it.unibz.inf.qtl1.formulae.temporal.Always;
import it.unibz.inf.qtl1.formulae.temporal.AlwaysFuture;
import it.unibz.inf.qtl1.formulae.temporal.NextFuture;
import it.unibz.inf.qtl1.formulae.temporal.Sometime;
import it.unibz.inf.qtl1.formulae.temporal.SometimePast;
import it.unibz.inf.qtl1.output.LatexDocumentCNF;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.terms.Constant;
import it.unibz.inf.qtl1.terms.Variable;


public class ExampleQTL {

	public static void main(String[] args) throws Exception{

		//String fileName="/ram/ter2Fol";
		String fileName="qtl2ltl";
	
	
		ExampleQTL t = new ExampleQTL();

		Formula f = t.getFormula();
	
		System.out.println("Saving in "+fileName);
		System.out.println("Original Formula:");
		System.out.println("Num of Conjuncts: "+f.getSubFormulae().size());
		System.out.println("Num of Constants: "+f.getConstants().size());
	
		Formula ltl = f.makePropositional();
	
		(new LatexDocumentCNF(f)).toFile(fileName+".tex");
		(new LatexDocumentCNF(ltl)).toFile(fileName+"_ltl.tex");
		(new NuSMVOutput(ltl)).toFile(fileName+".smv");
	
	
		// ./NuSMV -bmc -dcx
	
		System.out.println("LTL Formula:");
		System.out.println("Num of Conjuncts: "+ltl.getSubFormulae().size());
		System.out.println("Num of Propositions: "+ltl.getPropositions().size());
		System.out.println("Num of Constants: "+ltl.getConstants().size());
	

	}

	
	public Formula getFormula(){
		ConjunctiveFormula formula =  new ConjunctiveFormula();
		formula.addConjunct(getTBox());
		formula.addConjunct(getEpsilon());
		//Constant c = new Constant("c");
		//formula.addConjunct(new Atom("P",c));
		
		return formula;
	}
	
	private Formula getTBox(){
		return new ConjunctiveFormula(getT0(), getT1());
		//return getT1();
	}
	
	Variable x = new Variable("x");
	Atom integer = new Atom("Integer",x);
	Atom manager = new Atom("Manager",x);
	Atom topManager = new Atom("TopManager",x);
	Atom project = new Atom("Project",x);
	Atom exproject = new Atom("ExProject",x);
	Atom manages = new Atom("Manages",x);
	
	Atom e1payslipnumberinv = new Atom("E_1PaySlipNumberInv",x);
	Atom e1payslipnumber = new Atom("E_1PaySlipNumber",x);
	Atom e2payslipnumberinv = new Atom("E_2PaySlipNumberInv",x);
	Atom e2payslipnumber = new Atom("E_2PaySlipNumber",x);
	Atom e1salary = new Atom("E_1Salary",x);
	Atom e2salary = new Atom("E_2Salary",x);
	Atom e1salaryinv = new Atom("E_1SalaryInv",x);
	Atom employee = new Atom("Employee",x);
	
	Atom e1man = new Atom("E_1man",x);
	Atom e2man = new Atom("E_2man",x);
	Atom e1manInv = new Atom("E_1manInv",x);
	Atom e2manInv = new Atom("E_2manInv",x);
	Atom e1prj = new Atom("E_1prj",x);
	Atom e2prj = new Atom("E_2prj",x);
	Atom e1prjinv = new Atom("E_1prjInv",x);
	Atom e2prjinv = new Atom("E_2prjInv",x);

	
	private Formula getT0(){
		ConjunctiveFormula T0= new ConjunctiveFormula();
		// D
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				integer, new NegatedFormula(employee)
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				integer, new NegatedFormula(manager)
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				integer , new NegatedFormula(topManager) 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				integer , new NegatedFormula(project) 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				integer , new NegatedFormula(exproject) 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				integer , new NegatedFormula(manages)
				), x)));
		// A
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1payslipnumberinv, integer
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1salaryinv, integer
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				employee, e1payslipnumber
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				employee, new NegatedFormula(e2payslipnumber) 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				employee, e1salary 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				employee, new NegatedFormula(e2salary)
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e2payslipnumber, e1payslipnumber 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e2salary, e1salary 
				), x)));
		
		// R		
		
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				manages, e1man 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1man, manages 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new NegatedFormula(
				e2man
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1manInv, topManager 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				topManager, e1manInv 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				topManager, new NegatedFormula(e2manInv) 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				manages, e1prj 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1prj, manages 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new NegatedFormula(
				 e2prj
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1prjinv, project 
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				project, e1prjinv  
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				project, new NegatedFormula(e2prjinv)  
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e2man, e1man  
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e2prj, e1prj  
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e2manInv, e1manInv  
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e2prjinv, e1prjinv  
				), x)));
		
		// H
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				topManager, manager  
				), x)));
		T0.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				manager, employee  
				), x)));
		
		
		return T0;
	}
	
	private Formula getT1(){
		ConjunctiveFormula T1 = new ConjunctiveFormula();
		
		// TS
		T1.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				employee, new Always(employee)  
				), x)));
		T1.addConjunct(new Always(new UniversalFormula(new Sometime(
				new NegatedFormula(manager)
				), x)));
		T1.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1payslipnumber, new Always(e1payslipnumber)  
				), x)));
		T1.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e2payslipnumber, new Always(e2payslipnumber)  
				), x)));
		T1.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				e1payslipnumberinv, new Always(e1payslipnumberinv)  
				), x)));
		
		// TRANS 
		T1.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				project, new NextFuture(exproject)  
				), x)));
				
		// EVO
		T1.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				manager, new AlwaysFuture(manager)  
				), x)));
		T1.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				manager, new SometimePast(employee)  
				), x)));
		
		return T1;
	}
	
	
	Proposition pPaySlipNumber = new Proposition("pPaySlipNumber");
	Constant dPaySlipNumber = new Constant("dPaySlipNumber");
	Constant dPaySlipNumberInv = new Constant("dPaySlipNumberInv");
	
	
	Proposition pSalary = new Proposition("pSalary");
	Constant dSalary = new Constant("dSalary");
	Constant dSalaryInv = new Constant("dSalaryInv");
	
	Proposition pman = new Proposition("pman");
	Constant dman = new Constant("dman");
	Constant dmanInv = new Constant("dmanInv");
	
	Proposition pprj = new Proposition("pprj");
	Constant dprj= new Constant("dprj");
	Constant dprjInv= new Constant("dprjInv");
	
	
	private Formula getEpsilon(){
		ConjunctiveFormula e = new ConjunctiveFormula();
		
		ConjunctiveFormula e1 = new ConjunctiveFormula();
		
		e1payslipnumber.setArg(0, dPaySlipNumber);
		e1payslipnumberinv.setArg(0, dPaySlipNumberInv);
		
		e1.addConjunct(new ConjunctiveFormula(
				new ImplicationFormula(pPaySlipNumber, e1payslipnumber),
				new ImplicationFormula(pPaySlipNumber, e1payslipnumberinv)
				));
		
		e1payslipnumber.setArg(0, x);
		e1payslipnumberinv.setArg(0, x);
		
		e1.addConjunct(new Always(new UniversalFormula(new ConjunctiveFormula(
				new ImplicationFormula(e1payslipnumber, new Always(pPaySlipNumber)),
				new ImplicationFormula(e1payslipnumberinv, new Always(pPaySlipNumber))
				), x)));
		
		ConjunctiveFormula e2 = new ConjunctiveFormula();
		
		e1salary.setArg(0, dSalary);
		e1salaryinv.setArg(0, dSalaryInv);
		
		e2.addConjunct(new ConjunctiveFormula(
				new ImplicationFormula(pSalary, e1salary),
				new ImplicationFormula(pSalary, e1salaryinv)
				));
		
		e1salary.setArg(0, x);
		e1salaryinv.setArg(0, x);
		
		e2.addConjunct(new Always(new UniversalFormula(new ConjunctiveFormula(
				new ImplicationFormula(e1salary, new Always(pSalary)),
				new ImplicationFormula(e1salaryinv, new Always(pSalary))
				), x)));
		
		ConjunctiveFormula e3 = new ConjunctiveFormula();

		e1man.setArg(0, dman);
		e1manInv.setArg(0, dmanInv);
		
		e3.addConjunct(new ConjunctiveFormula(
				new ImplicationFormula(pman, e1man),
				new ImplicationFormula(pman, e1manInv)
				));
		
		e1man.setArg(0, x);
		e1manInv.setArg(0, x);
		
		e3.addConjunct(new Always(new UniversalFormula(new ConjunctiveFormula(
				new ImplicationFormula(e1man, new Always(pman)),
				new ImplicationFormula(e1manInv, new Always(pman))
				), x)));
		
		ConjunctiveFormula e4 = new ConjunctiveFormula();
		

		e1prj.setArg(0, dprj);
		e1prjinv.setArg(0, dprjInv);
		
		e4.addConjunct(new ConjunctiveFormula(
				new ImplicationFormula(pprj, e1prj),
				new ImplicationFormula(pprj, e1prjinv)
				));
		
		e1prj.setArg(0, x);
		e1prjinv.setArg(0, x);
		
		e4.addConjunct(new Always(new UniversalFormula(new ConjunctiveFormula(
				new ImplicationFormula(e1prj, new Always(pprj)),
				new ImplicationFormula(e1prjinv, new Always(pprj))
				), x)));
		
		e.addConjunct(e1);
		e.addConjunct(e2);
		e.addConjunct(e3);
		e.addConjunct(e4);
		return e;
	}
	
}
