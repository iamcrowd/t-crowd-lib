package org.gario.code.output;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class StatsOutputDocument {
		
		boolean aboxtime;
		
		public StatsOutputDocument(boolean abox){
			if (abox) {
				aboxtime = true;
			} else {
				aboxtime = false;
			}
		}

		/**
		 * TBox-QTL1-QTLN-LTL
		 * 
		 * @param fileName
		 * @param tbox2qtl
		 * @param qtl2qtln
		 * @param qtln2ltl
		 * @param size
		 * @throws Exception
		 */
		public void toStatsFile(String fileName, long tbox2qtl, long qtl2qtln, long qtln2ltl, int size) throws Exception{
		    // Create file 
		    
			FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		    if (aboxtime) {
		    	out.write("TBox to QTL1 time (ms):\t" + tbox2qtl + "\n");
		    	out.write("ABox to QTL1 time (ms):\t" + qtl2qtln +"\n");
		    	out.write("QTL1 to LTL time (ms):\t" + qtln2ltl +"\n");
		    	out.write("Number of Propositions:\t" + size +"\n");
		    } else {
		    	out.write("TBox to QTL1 time (ms):\t" + tbox2qtl + "\n");
		    	out.write("QTL1 to QTLN time (ms):\t" + qtl2qtln +"\n");
		    	out.write("QTLN to LTL time (ms):\t" + qtln2ltl +"\n");
		    	out.write("Number of Propositions:\t" + size +"\n");
		    }
		    
		    //Close the output stream
		    out.close();
		}
		
		/**
		 * TBox-QTL1-LTL
		 * 
		 * @param fileName
		 * @param tbox2qtl
		 * @param qtl2ltl
		 * @param size
		 * @throws Exception
		 */
		public void toStatsFile(String fileName, long tbox2qtl, long qtl2ltl, int size) throws Exception{
		    // Create file 
		    
			FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		    out.write("TBox to QTL1 time (ms):\t" + tbox2qtl +"\n");
		    out.write("QTL1 to LTL time (ms):\t" + qtl2ltl +"\n");
		    out.write("Number of Propositions:\t" + size +"\n");

		    //Close the output stream
		    out.close();
		}
		
		/**
		 * TBox-QTL1-QTLN-ABox-LTL
		 *
		 * @param fileName
		 * @param tbox2qtl
		 * @param qtl2qtln
		 * @param qtlnABox
		 * @param qtln2ltl
		 * @param size
		 * @throws Exception
		 */
		public void toStatsFile(String fileName, long tbox2qtl, long qtl2qtln, long qtlnABox, long qtln2ltl, int size) throws Exception{
		    // Create file 
		    
			FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		    out.write("TBox to QTL1 time (ms):\t" + tbox2qtl + "\n");
		    out.write("QTL1 to QTLN time (ms):\t" + qtl2qtln + "\n");
		    out.write("ABox to QTLN time (ms):\t" + qtlnABox + "\n");
		    out.write("QTLN to LTL time (ms):\t" + qtln2ltl + "\n");
		    out.write("Number of Propositions:\t" + size + "\n");

		    //Close the output stream
		    out.close();
		}
		
}

