package it.unibz.inf.qtl1.tests;

import java.util.Set;

import junit.framework.TestCase;
import it.unibz.inf.qtl1.NaturalTranslator;
import it.unibz.inf.qtl1.NotGroundException;
import it.unibz.inf.qtl1.atoms.Atom;
import it.unibz.inf.qtl1.atoms.Proposition;
import it.unibz.inf.qtl1.formulae.*;
import it.unibz.inf.qtl1.formulae.quantified.*;
import it.unibz.inf.qtl1.formulae.temporal.*;
import it.unibz.inf.qtl1.output.FormulaOutputFormat;
import it.unibz.inf.qtl1.output.NuSMVOutput;
import it.unibz.inf.qtl1.terms.*;


public class MainTest extends TestCase{
	Variable x =new Variable("x");
	Variable y =new Variable("y");
	
	public void testToString() throws Exception{
		Atom a = new Atom("R",2);
		a.setArg(0, new Variable("x"));
		Function f = new Function("f",1);
		f.setArg(0, new Constant("a"));
		a.setArg(1,f);
		
		String sA ="R(x,f(a))";
		assertEquals(a.toString(), sA);
		
		Formula notA = new NegatedFormula(a);
		Formula notA_A = new ConjunctiveFormula(notA, a);
		
		
		
		assertEquals("(!R(x,f(a)) & R(x,f(a)))",notA_A.toString());
		
		Formula impl = new ImplicationFormula(notA, notA_A);
		
		Formula or = new DisjunctiveFormula(impl, a);
		assertEquals("(!R(x,f(a)) -> (!R(x,f(a)) & R(x,f(a))))",impl.toString());
		assertEquals("((!R(x,f(a)) -> (!R(x,f(a)) & R(x,f(a)))) | R(x,f(a)))",or.toString());
		
		QuantifiedFormula qF = new ExistentialFormula(
				new ConjunctiveFormula(a, new UniversalFormula(or, new Variable("x"))),
				new Variable(("y")));
	
		//System.out.println(qF);
		assertEquals("Ey (R(x,f(a)) & Ax ((!R(x,f(a)) -> (!R(x,f(a)) & R(x,f(a)))) | R(x,f(a))))", qF.toString());
		
		
		
		Atom t = new Atom("rel1",0);
		ConjunctiveFormula tf = new ConjunctiveFormula(new AlwaysFuture(t), new AlwaysPast(t));
		tf.addConjunct(new SometimeFuture(t));
		tf.addConjunct(new SometimePast(t));
		tf.addConjunct(new NextFuture(t));
		tf.addConjunct(new NextPast(t));
		tf.addConjunct(new Since(t,new Until(t, t)));
		
		assertTrue(tf.getSubFormulae().size()==7);
//		assertEquals(tf.toString(),"((rel1 S (rel1 U rel1)) & Frel1 & Grel1 & Hrel1 & Orel1 & Xrel1 & Yrel1)");
		
		Always hg1 = new Always(t);
		Sometime of1 = new Sometime(t);
		
		Formula hg_of = new ConjunctiveFormula(hg1, of1);
		assertEquals(hg_of.toString(),"(HGrel1 & OFrel1)");

		FormulaOutputFormat fmt = new FormulaOutputFormat();
		fmt.setDefault();
		
		//System.out.println(qF);
		//System.err.println(qF.getFormattedFormula(fmt));
		//System.err.println(tf.getFormattedFormula(fmt));

		hg_of.getFormattedFormula(fmt);
		
		// Latex
		//LatexFormat lfmt = new LatexFormat();
		//System.err.println(qF.getFormattedFormula(lfmt));
		//System.err.println(tf.getFormattedFormula(lfmt));
		//System.err.println(hg_of.getFormattedFormula(lfmt));
	
		//System.out.print((new LatexDocumentCNF(tf, lfmt)).output());
		
		
		
	}
	
	public void testGetConstant(){
		Constant a= new Constant("a");
		Constant b= new Constant("b");
		Constant c= new Constant("c");
		Constant d= new Constant("d");
		Variable x = new Variable("x");
		
		Atom ab = new Atom("AB", 2);
		ab.setArg(0, a);
		ab.setArg(1, b);
		
		Atom cd = new Atom("CD", 3);
		cd.setArg(0, c);
		cd.setArg(1, d);
		cd.setArg(2, x);
		
		Formula f = new NegatedFormula(new UniversalFormula(new ExistentialFormula(
				new NextPast(new DisjunctiveFormula(new ConjunctiveFormula(ab, cd), ab)), x),
				x));
		
		Set<Constant> set= f.getConstants();
		assertEquals(set.size(),4);
		assertTrue(set.contains(a));
		assertTrue(set.contains(b));
		assertTrue(set.contains(c));
		assertTrue(set.contains(d));
		
		
		
		
		
	}
	
	public void testEquals(){
		
		// Terms
		Function f1 = new Function("f", 3);
		Function f2 = new Function("f", 3);
		Function g1 = new Function("g", 3);
		Function g1_ = new Function("g", 2);
		
		assertEquals(f1, f2);
		assertFalse(g1.equals(g1_));
		assertFalse(f1.equals(g1_));
		
		f1.setArg(0, f2);
		assertFalse(f1.equals(f2));
		
		// Variables
		Variable v1 = new Variable("v");
		Variable v2 = new Variable("v");
		Variable x = new Variable("x");
		
		assertEquals(v1, v2);
		assertFalse(v1.equals(x));
		
		
		assertFalse(v1.equals(f1));
		assertFalse(f1.equals(v1));
		
		f1.setArg(0, v1);
		f2.setArg(0, v2);
		
		assertEquals(f1, f2);

		// Proposition
		Proposition prop1 = new Proposition("R_c");
		Proposition prop2 = new Proposition("R_c");
		Proposition prop3 = new Proposition("R_d");
		
		assertEquals(prop1,prop2);
		assertFalse(prop1.equals(prop3));
		
		// Formulae
		Atom r1 = new Atom("R",2);
		Atom r1_ = new Atom("R",1);
		Atom r2 = new Atom("R",2);
		Atom p1 = new Atom("P",2);
		Atom p2  = new Atom("P",2);
		
		p1.setArg(0, x);
		p2.setArg(1, x);
		
		assertFalse(p1.equals(p2));
		
		p1.setArg(1, x);
		p2.setArg(0, x);
		
		assertEquals(p1, p2);
		
		assertEquals(r1, r2);
		assertFalse(r1.equals(r1_));
		
		ConjunctiveFormula c1= new ConjunctiveFormula(r1, p1);
		ConjunctiveFormula c2= new ConjunctiveFormula(r1, p2);
		
		assertEquals(c1,c2);
		// Quantified
		
		QuantifiedFormula e1 = new ExistentialFormula(r1, x);
		QuantifiedFormula e2 = new ExistentialFormula(r1, new Variable("v"));
		QuantifiedFormula u1 = new UniversalFormula(r1, x);
		QuantifiedFormula u2 = new UniversalFormula(r1, x);
		
		assertEquals(u2,u1);
		assertFalse(e1.equals(e2));
		assertFalse(e1.equals(u1));
		
		
		// Temporal

	}
	
	public void testGetFreeVars(){
		Variable x = new Variable("x");
		Variable y = new Variable("y");
		Atom l = new Atom("L",1);
		Atom r1 = new Atom("R",2);
		Atom r2 = new Atom("R",2);
		Atom p = new Atom("P",2);
		
		l.setArg(0, x);
		r1.setArg(0, x);
		r1.setArg(1, y);
		
		r2.setArg(0, x);
		r2.setArg(1, x);
		
		p.setArg(0, x);
		p.setArg(1, y);
		
		// (L(x) & Ax R(x,y) & Ax (P(x,y) & R(x,x)))
		
		ConjunctiveFormula f = new ConjunctiveFormula(l,new UniversalFormula(r1, x));
		f.addConjunct(new UniversalFormula(new ConjunctiveFormula(p, r2), x));
		
		assertTrue(l.getFreeVars().contains(x));
		assertTrue(l.getFreeVars().size()==1);
		
	}
	public void testMakeUniqueVar() throws Exception{
		Variable x = new Variable("x");
		Variable y = new Variable("y");
		Atom l = new Atom("L",1);
		Atom r1 = new Atom("R",2);
		Atom r2 = new Atom("R",2);
		Atom p = new Atom("P",2);
		
		l.setArg(0, x);
		r1.setArg(0, x);
		r1.setArg(1, y);
		
		r2.setArg(0, x);
		r2.setArg(1, x);
		
		p.setArg(0, x);
		p.setArg(1, y);
		
		// (L(x) & Ax R(x,y) & Ax (P(x,y) & R(x,x)))
		
		ConjunctiveFormula f = new ConjunctiveFormula(l,new UniversalFormula(r1, x));
		f.addConjunct(new UniversalFormula(new ConjunctiveFormula(p, r2), x));
		
//		assertEquals("(Ax (P(x,y) & R(x,x)) & Ax R(x,y) & L(x))", f.toString());
		
		f.makeUniqueVar();
//		System.out.println(f);
		assertEquals(f.getVariables().size(),4);
		
		Formula f2= new ConjunctiveFormula(l, new UniversalFormula(new ExistentialFormula(f, y),x));
		// (L(x) & Ax.Ey.(L(x) & Ax R(x,y) & Ax (P(x,y) & R(x,x))))
		
		f2.makeUniqueVar();
		assertEquals(f2.getVariables().size(),5);
	}
	
	public void testHasExistential(){
		ConjunctiveFormula f = new ConjunctiveFormula(new Atom("L",x),
				new UniversalFormula(new Atom("R",x,y), x));
		f.addConjunct(new UniversalFormula(new ConjunctiveFormula(new Atom("P",x,
				y), new Atom("R",x,y)), x));
		
		//System.out.println(f);
		assertFalse(f.hasExistential());
	
		ConjunctiveFormula f2 = new ConjunctiveFormula(new Atom("L",x),
				new UniversalFormula(new Atom("R",x,y), x));
		f2.addConjunct(new ExistentialFormula(new ConjunctiveFormula(new Atom("P",x,
				y), new Atom("R",x,y)), x));
		
		assertTrue(f2.hasExistential());
		
		Formula f3 = new ConjunctiveFormula(new Atom("P",x,y),
				new UniversalFormula(new Atom("R",y),y));
		
		assertFalse(f3.hasExistential());
	}
	
	public void testClone(){
		Atom r = new Atom("R",x,y);
		
		TemporalFormula f = new NextPast(r);
		assertTrue(f.equals((Formula)f.clone()));
		r.setArg(0, y);
		TemporalFormula fb = new NextPast(r);
		
		assertFalse(f.equals(fb));
		
		Formula hg = new Always(r);
		hg =(Formula) hg.clone();
		assertEquals(hg, hg.clone());
		
		
	}
	public void testRemoveUniversals() throws Exception{
		Set<Variable> unbound;
		ConjunctiveFormula f = new ConjunctiveFormula(new Atom("L",x),
				new UniversalFormula(new Atom("R",x,y), x));
		f.addConjunct(new UniversalFormula(new ConjunctiveFormula(new Atom("P",x,
				y), new Atom("R",x,y)), x));
		
		f.makeUniqueVar();
		unbound = f.removeUniversals();
		assertTrue(unbound.size()==2);
	
		ConjunctiveFormula f2 = new ConjunctiveFormula(new Atom("L",x),
				new UniversalFormula(new Atom("R",x,y), x));
		f2.addConjunct(new ExistentialFormula(new ConjunctiveFormula(new Atom("P",x,
				y), new Atom("R",x,y)), x));
		
		f2.makeUniqueVar();
		
		unbound = f2.removeUniversals();
		assertTrue(unbound.size()==1);
	
		
		Formula f3 = new ConjunctiveFormula(new Atom("P",x,y),
				new UniversalFormula(new Atom("R",y),y));
		
		f3.makeUniqueVar();
		unbound = f3.removeUniversals();
		assertTrue(unbound.size()==1);
		
		Formula f4= new NegatedFormula(new UniversalFormula(new UniversalFormula(new Atom("R",x,y),y),x));
		unbound =f4.removeUniversals();
		assertTrue(unbound.size()==2);
	}
	
	public void testMakeGround() throws Exception{
		Atom r = new Atom("R",x,y);
		Atom p_c = new Atom("P",new Constant("c"));
		Atom r_c = new Atom("R",new Constant("c"),new Constant("d"));
		
		//f1 = Axy. R(x,y) & P(c)
		Formula f1 = new ConjunctiveFormula(new UniversalFormula(
				new UniversalFormula(r, y), x),p_c);
		//System.out.println(f1);
		//System.out.println("-" + f1.makeGround());
		assertEquals(f1.makeGround().getSubFormulae().size(),2);
		
		//f2 = Axy. R(x,y) & R(c,d)
		Formula f2 = new ConjunctiveFormula(new UniversalFormula(
				new UniversalFormula(r, y), x),r_c);
		//System.out.println(f2);
		//System.out.println(f2.makeGround());
		assertEquals(f2.makeGround().getSubFormulae().size(),5);
		
		try{
			Formula f3=new ConjunctiveFormula(r,new ExistentialFormula(f2, x));
			f3.makeGround();
			assertTrue(false);
		}catch (ExistentialFormulaException ex){
			assertTrue(true);
		}
	}
	
	public void testAtomToProposition() throws Exception{
		Atom r = new Atom("R",2);
		Function f = new Function("f", 1);
		f.setArg(0, x);
		r.setArg(0, f);
		try{
			r.atomToProposition();
			assertTrue(false);
		}catch(NotGroundException ex){
			assertTrue(true);
		}
		f.setArg(0,new Constant("c"));
		r.setArg(1, new Constant("d"));

		assertTrue(r.atomToProposition().getArity()==0);
		

	}

	public void testAtomsToPropositions() throws Exception{
		Atom r = new Atom("R",2);
		Function f = new Function("f", 1);
		f.setArg(0,new Constant("c"));
		r.setArg(0, f);
		r.setArg(1, new Constant("d"));
		
		Formula conj = new ConjunctiveFormula(r,new UniversalFormula(r, y));
		conj.atomsToPropositions();
		//System.out.println(conj);
		assertTrue(conj.getConstants().size()==0);
		assertTrue(conj.getVariables().size()==0);
		assertTrue(conj.getPropositions().size()==1);
	}
	
	public void testMakePropositional() throws Exception{
		Atom r = new Atom("R",x,y);
		Atom r_c = new Atom("R",new Constant("c"),new Constant("d"));
		
		Formula f1 = new ConjunctiveFormula(new UniversalFormula(
				new UniversalFormula(r, y), x),r_c);
		
		Formula propF1 = f1.makePropositional();
		//System.out.println(propF1);
		//System.out.println(propF1.getPropositions());
		assertTrue(propF1.getPropositions().size()==4);
	}
	
	public void testIsPropositional() throws Exception{
		Atom r = new Atom("R",x,y);
		Atom r_c = new Atom("R",new Constant("c"),new Constant("d"));
		
		Formula f1 = new ConjunctiveFormula(new UniversalFormula(
				new UniversalFormula(r, y), x),r_c);
		
		Formula propF1 = f1.makePropositional();
		assertFalse(f1.isPropositional());
		assertTrue(propF1.isPropositional());
	}
	
	public void testNuSMVOutput() throws Exception{
		Atom r = new Atom("R",x,y);
		Atom r_c = new Atom("R",new Constant("c"),new Constant("d"));
		
		Formula f2 = new ConjunctiveFormula(new UniversalFormula(
				new UniversalFormula(r, y), x),r_c);
		
		Formula propF2 = f2.makePropositional();
		NuSMVOutput nusmv = new NuSMVOutput(propF2);
		nusmv.getOutput();
	}
	
	public void testBasicFormula() throws Exception{
		Atom p = new Atom("P",x);
		Atom r = new Atom("R",x);
		Constant c = new Constant("c");
		
		ConjunctiveFormula f = new ConjunctiveFormula();
		
		f.addConjunct(new Always(new UniversalFormula(new ImplicationFormula(
				p, new Always(r) 
				), x)));
		p.setArg(0, c);
		f.addConjunct(p);
		
		//System.out.println(f);
		assertEquals("(HGAx (P(x) -> HGR(x)) & P(c))",f.toString());
		//System.out.println(f.makeGround());
		assertEquals("(HG(P(c) -> HGR(c)) & P(c))",f.makeGround().toString());
	}
	

	public void testNNF(){
		Proposition p = new Proposition("p");
		Proposition q = new Proposition("q");
		
		ConjunctiveFormula cf = new ConjunctiveFormula(
				p,new ImplicationFormula(new NegatedFormula(q), p));
		
		ConjunctiveFormula cfNNF = new ConjunctiveFormula(
				p,new DisjunctiveFormula(q, p));

		assertEquals(cfNNF,cf.toNNF());
		assertEquals((new NegatedFormula(cf)).toNNF(),cf.negateToNNF());
		
		BimplicationFormula pBnotQ = new BimplicationFormula(p, new NegatedFormula(q));
		
		ConjunctiveFormula pBnotQ_NNF  = new ConjunctiveFormula();
		pBnotQ_NNF.addConjunct(new DisjunctiveFormula(new NegatedFormula(p),new NegatedFormula(q)));
		pBnotQ_NNF.addConjunct(new DisjunctiveFormula(p,q));
		
		assertEquals(pBnotQ_NNF, pBnotQ.toNNF());
		
	}
	
	
	public void testMakeTemporalStrict(){
		Proposition p = new Proposition("p");
		Formula strict = null;
		
		NextFuture Xp = new NextFuture(p);
		NextPast Yp =  new NextPast(p);
		
		AlwaysFuture Gp = new AlwaysFuture(p);
		AlwaysPast Hp = new AlwaysPast(p);
		
		SometimeFuture Fp = new SometimeFuture(p);
		SometimePast Op = new SometimePast(p);
		
		Formula f1 =  new SometimeFuture(new ConjunctiveFormula(Xp, Gp)); // F(Xp /\ Gp)
		
		/* Xp -> Xp 
		 * Yp -> Yp */
		
		strict = Xp.makeTemporalStrict();
		
		assertEquals(strict, Xp);
		
		strict = Yp.makeTemporalStrict();
		
		assertEquals(strict, Yp);
		
		/* Gp -> XGp
		 * Fp -> XFp */
		
		strict = Gp.makeTemporalStrict();
		
		assertEquals(new NextFuture(Gp), strict);
		
		strict = Fp.makeTemporalStrict();
		
		assertEquals(new NextFuture(Fp), strict);
		
		/* Hp -> YHp
		 * Op -> YOp */
		 
		strict = Hp.makeTemporalStrict();
		
		assertEquals(new NextPast(Hp), strict);
		
		strict = Op.makeTemporalStrict();
		
		assertEquals(new NextPast(Op), strict);
	}
	
	public void testNaturalConverter() throws Exception{
		/* Test formula:
		 * \phi = (OXa) /\ (Gb)
		 * 
		 *  Res:
		 *  Tr(\phi) = (DIAMOND_1_f /\ BOX_1_f) /\
		 *  			(a_f <-> a_p) /\ (b_f <-> b_p) /\
		 *  			(DIAMOND_1_f <-> DIAMOND_1_p) /\
		 *  			(BOX_1_f <-> BOX_1_p) /\
		 *  			
		 *  			XG (NXT_1_p <-> Y a_p) /\ 
		 *  			G (NXT_1_f <-> X a_f) /\
		 *  			
		 *  			XG (DIAMOND_1_f <-> (Y DIAMOND_1_f \/ NXT_1_f) ) /\
		 *  			G (DIAMOND_1_P <-> F NXT_1_p) /\
		 * 
		 *  			XG (BOX_1_p <-> (G BOX_1_p) /\ b_p) /\
		 *  			G (BOX_1_f <-> G b_f)			 
		 */
		
		Variable x = new Variable("x");
		
		Atom a = new Atom("a",x);
		Atom b = new Atom("b",x);
		
		Formula f = new UniversalFormula(new ConjunctiveFormula(
				new SometimePast(new NextFuture(a)),
				new AlwaysFuture(b)),x);
		
		Formula tr = (new NaturalTranslator(f)).getTranslation();
		
		System.out.println(tr);
		
		Formula t = new ConjunctiveFormula(tr, new Atom("DUMMY",new Constant("c")));
		System.out.println(t.makePropositional());
	}
}
