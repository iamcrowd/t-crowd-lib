package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.tbox.TDLLiteN_TBox_AbsABox;

public class AbstractionTestRandomWithTBox {

	public static void main(String[] args) throws Exception {
		TDLLiteN_TBox_AbsABox ABS = new TDLLiteN_TBox_AbsABox();

		TBox t = new TBox();
		ABox a = new ABox();

		t = ABS.getFTBox(10, 5, 2, 3, 5, 5);
		
		//int NbAssertion, int sizeInd, int N, int max
		a = ABS.getABox(100500, 2, 100, 5);
		
		Map<String, Integer> statsABox = a.getStatsABox();
		System.out.println("");
		System.out.println("------TDLITE ABOX");
		
		String keyA;
		keyA="Concept_Assertions:";
		System.out.println(keyA+ statsABox.get(keyA));
		keyA="Role_Assertions:";
		System.out.println(keyA+ statsABox.get(keyA));

		TDLLiteNABSFPXReasoner.buildCheckTBoxAbsABoxSAT(t, true, "test", a);

	}

}


