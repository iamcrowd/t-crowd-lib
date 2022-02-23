package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.abox.ABox;

import it.unibz.inf.tdllitefpx.tbox.TD_LITE_N_AbsABox;

public class AbstractionTestRandom {

	public static void main(String[] args) throws Exception {
		TD_LITE_N_AbsABox ABS = new TD_LITE_N_AbsABox();

		ABox a = new ABox();
		
		//		int NbAssertion, int sizeInd, int N, int max
		a = ABS.getABox(10, 2, 2, 5);
		
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


