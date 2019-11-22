package it.gilia.tcrowd.utils;

import java.util.Objects;


public class PathsManager{
	
	public PathsManager() {
		
	}
	
	/**
	 * Get a temporal path from an input full path string
	 * 
	 * @param input_path includes a full path and the input file name.
	 * @return a String with a temporal path for outputs files.
	 * @implNote A full path starts with "/" so that first string before the slash is the empty string.
	 */
	public String getPathToTmp(String input_path) {

		String[] o_path = input_path.split("/");
		o_path[o_path.length-1] = "";
		String new_path = String.join("/", o_path);
		return new_path;
	}
}