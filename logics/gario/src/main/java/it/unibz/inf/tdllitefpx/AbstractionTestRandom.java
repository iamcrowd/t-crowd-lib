package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.abox.ABox;

import it.unibz.inf.tdllitefpx.tbox.TDLLiteN_AbsABox;

public class AbstractionTestRandom {

	public static void main(String[] args) throws Exception {
		TDLLiteN_AbsABox ABS = new TDLLiteN_AbsABox();

		ABox a = new ABox();
		
		//int NbAssertion, int I, int N, int T
		a = ABS.getABox(461800, 100, 2, 5);
		//a = ABS.getABox(325000, 100, 2, 5);
		
		Map<String, Integer> statsABox = a.getStatsABox();
		System.out.println("");
		System.out.println("------TDLITE ABOX");
		
		String keyA;
		keyA="Concept_Assertions:";
		System.out.println(keyA+ statsABox.get(keyA));
		keyA="Role_Assertions:";
		System.out.println(keyA+ statsABox.get(keyA));

		TDLLiteNABSFPXReasoner.buildAbstract(a, 3);

	}

}


