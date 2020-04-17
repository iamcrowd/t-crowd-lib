package org.gario.code.output;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class StatsOutputDocument {
		
		public StatsOutputDocument(){}

		public void toStatsFile(String fileName, long tbox2qtl, long qtl2qtln, long qtln2ltl, int size) throws Exception{
		    // Create file 
		    
			FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		    out.write(tbox2qtl+"\n");
		    out.write(qtl2qtln+"\n");
		    out.write(qtln2ltl+"\n");
		    out.write(size+"\n");
		    
		    //Close the output stream
		    out.close();
		}
		
		public void toStatsFile(String fileName, long tbox2qtl, long qtl2ltl, int size) throws Exception{
		    // Create file 
		    
			FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		    out.write(tbox2qtl+"\n");
		    out.write(qtl2ltl+"\n");
		    out.write(size+"\n");

		    //Close the output stream
		    out.close();
		}
		
		public void toStatsFile(String fileName, long tbox2qtl, long qtl2qtln, long qtlnABox, long qtln2ltl, int size) throws Exception{
		    // Create file 
		    
			FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		    out.write(tbox2qtl+"\n");
		    out.write(qtl2qtln+"\n");
		    out.write(qtlnABox+"\n");
		    out.write(qtln2ltl+"\n");
		    out.write(size+"\n");

		    //Close the output stream
		    out.close();
		}
		
		
}

