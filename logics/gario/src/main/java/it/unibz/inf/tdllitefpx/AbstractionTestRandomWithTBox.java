package it.unibz.inf.tdllitefpx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import it.unibz.inf.tdllitefpx.abox.ABox;
import it.unibz.inf.tdllitefpx.abox.ABoxConceptAssertion;
import it.unibz.inf.tdllitefpx.concepts.Concept;
import it.unibz.inf.tdllitefpx.tbox.TBox;
import it.unibz.inf.tdllitefpx.tbox.TDLLiteN_TBox_AbsABox;

public class AbstractionTestRandomWithTBox {

	public static void main(String[] args) throws Exception {
		TDLLiteN_TBox_AbsABox ABS = new TDLLiteN_TBox_AbsABox();

		TBox t = new TBox();
		ABox a = new ABox();


		//t = ABS.getFTBox(6, 5, 2, 3, 5, 5);
		//a = ABS.getABox(461800, 2, 100, 5);

		//t = ABS.getFTBox(12, 5, 4, 3, 5, 5);
		//a = ABS.getABox(900000, 4, 100, 5);

		//int NbAssertion, int N, int Indv, int max
		//N = 6 line 1
		//t = ABS.getFTBox(20, 5, 6, 3, 5, 5);
		//a = ABS.getABox(310000, 6, 100, 5);

		//N = 6 line 2
		//t = ABS.getFTBox(20, 5, 6, 3, 5, 5);
		//a = ABS.getABox(420000, 6, 100, 5);

		//N = 6 line 3
		//t = ABS.getFTBox(20, 5, 6, 3, 5, 5);
		//a = ABS.getABox(550000, 6, 100, 5);

		//N = 6 line 4
		//t = ABS.getFTBox(20, 5, 6, 3, 5, 5);
		//a = ABS.getABox(740000, 6, 100, 5);

		//N = 6 line 5
		//t = ABS.getFTBox(20, 5, 6, 3, 5, 5);
		//a = ABS.getABox(980000, 6, 100, 5);

		//N = 6 line 6 with new value in random
		t = ABS.getFTBox(20, 5, 6, 3, 5, 5);
		a = ABS.getABox(1400000, 6, 100, 5);


		//N = 50 line 1
		//t = ABS.getFTBox(150, 5, 50, 3, 5, 5);
		//a = ABS.getABox(3015000, 50, 100, 5);

		//N = 50 line 2
		//t = ABS.getFTBox(150, 5, 50, 3, 5, 5);
		//a = ABS.getABox(4020000, 50, 100, 5);
		
		//N = 50 line 3
		//t = ABS.getFTBox(150, 5, 50, 3, 5, 5);
		//a = ABS.getABox(5022500, 50, 100, 5);

		//N = 50 line 4
		//t = ABS.getFTBox(150, 5, 50, 3, 5, 5);
		//a = ABS.getABox(7027000, 50, 100, 5);

	 	int total = 0;
		int i = 1;
		for (Entry<String, Set<Concept>> entry : ABS.getCountCA().entrySet()) {
			String key = entry.getKey();
			System.out.println(key);
			Set<Concept> value = entry.getValue();
			System.out.println(i + " --- Number of ABox Concept Assertions: " + value.size());
			total = total + value.size();
			for (Concept ca : value) {
				System.out.print(ca.toString() + ", ");
			}
			System.out.println("");
			i++;
		}
		System.out.println("");
		System.out.println("Total Number of Concepts: " + total);
		
		Map<String, Integer> statsABox = a.getStatsABox();
		System.out.println("");
		System.out.println("------TDLITE ABOX");
		
		String keyA;
		keyA="Concept_Assertions:";
		System.out.println(keyA+ statsABox.get(keyA));
		keyA="Role_Assertions:";
		System.out.println(keyA+ statsABox.get(keyA));

		TDLLiteNABSFPXReasoner.buildCheckTBoxAbsABoxSAT(t, true, "test", a);
		//TDLLiteNABSFPXReasoner.justAbsReasoner(t, true, "test", a);

	}

}


