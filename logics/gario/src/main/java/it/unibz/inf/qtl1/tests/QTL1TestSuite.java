package it.unibz.inf.qtl1.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class QTL1TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for it.unibz.inf.qtl1.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(MainTest.class);
		//$JUnit-END$
		return suite;
	}

}
