package org.gario.code.output;

import java.io.BufferedWriter;
import java.io.FileWriter;

public abstract class OutputDocument {

		protected FormattableObj f;
		protected OutputFormat fmt;
		
		public OutputDocument(FormattableObj f, OutputFormat fmt){
			this.f=f;
			this.fmt=fmt;
		}
		
		protected abstract String getOpening();
		protected abstract String getEnding();
		protected abstract StringBuilder getBody();
		
		public String getOutput(){
			StringBuilder out=new StringBuilder();
			
			out.append(getOpening());
			try{
				out.append(getBody());
			}catch (Exception ex){
				ex.printStackTrace();
				out.append(ex.getMessage());
			}
			out.append(getEnding());
			
			return out.toString();
		}

		
		
		
		public void toFile(String fileName) throws Exception{
		    // Create file 
		    
			FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write(this.getOutput());

		    //Close the output stream
		    out.close();
			    
		}
		
		public String toString(){ return this.getOutput(); }
	}

