package it.unibz.inf.tdllitefpx;

import java.util.Map;

import it.unibz.inf.tdllitefpx.abox.ABox;

import it.unibz.inf.tdllitefpx.tbox.TD_LITE_N_AbsABox;

public class AbstractionTestRandom {

	public static void main(String[] args) throws Exception {
		TD_LITE_N_AbsABox ABS = new TD_LITE_N_AbsABox();

		ABox a = new ABox();
		
//		int size, int Lc, int N, int Q, int Pr, int Pt
		
		a = ABS.getABox(100, 10, 2, 5);
		
		Map<String, Integer> statsABox = a.getStatsABox();
		System.out.println("");
		System.out.println("------TDLITE ABOX");
		
		String keyA;
		keyA="Concept_Assertion:";
		System.out.println(keyA+ statsABox.get(keyA));
		keyA="Roles_Assertion:";
		System.out.println(keyA+ statsABox.get(keyA));
		
//		int NbAssertion,int N, int sizeInd, int max
		
		//a = ABS.getABox(100500,2,100,5);

		TDLLiteNABSFPXReasoner.buildAbstract(a, 3);

	}

}


