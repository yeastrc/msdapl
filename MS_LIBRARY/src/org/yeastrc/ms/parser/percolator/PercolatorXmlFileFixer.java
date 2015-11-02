/**
 * PercolatorFileFixer.java
 * @author Vagisha Sharma
 * Aug 25, 2011
 */
package org.yeastrc.ms.parser.percolator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Takes as input a percolator xml file and a list of comma separated peptides.
 * 
 * The given input file is read and printed out on stdout.
 * If the input peptides are found twice in the <peptides> section of the input file, only
 * the first occurrence is printed on stdout.
 * 
 * Usage: java -classpath mslib_fat.jar org.yeastrc.ms.parser.percolator.PercolatorXmlFileFixer <input_percolator_xml> <list_of_comma_separated_peptides>
 * 
 */
public class PercolatorXmlFileFixer {

	
	
	public static void main(String[] args) throws IOException {
		
		String filepath = args[0];
		String peptidesArg = args[1];
		
		Map<String, Boolean> peptides = new HashMap<String, Boolean>();
		
		String[] tokens = peptidesArg.split(",");
		for(String token: tokens) {
			
			peptides.put(token.trim(), Boolean.FALSE);
		}
		
		
		BufferedReader reader = null;
		
		try {
			
			reader = new BufferedReader(new FileReader(filepath));
			
			String line = null;
			
			boolean inPeptidesSection = false;
			
			while((line = reader.readLine()) != null) {
				
				if(line.contains("<peptides>")) {
					inPeptidesSection = true;
					System.out.println(line);
					continue;
				}
				
				if(line.contains("</peptides>")) {
					inPeptidesSection = false;
					System.out.println(line);
					continue;
				}
				
				if(!inPeptidesSection) {
					System.out.println(line);
				}
				else {
					
					printPeptide(line, reader, peptides);
					
				}
				
			}
			
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e) {}
		}
	}

	private static void printPeptide(String currentLine, BufferedReader reader, Map<String, Boolean> peptides) throws IOException {
		
		// we should be at the <peptide element start
		String line = currentLine;
		
		boolean skip = false;
		
		for(String peptide: peptides.keySet()) {
			
			if(line.contains("\""+peptide+"\"")) {
				
				if(peptides.get(peptide)) {
					skip = true;
				}
				
				else {
					peptides.put(peptide, Boolean.TRUE);
				}
			}
		}
		
		if(!skip)
			System.out.println(line);
		
		while((line = reader.readLine()) != null) {
			
			if(line.contains("</peptide>")) {
				
				if(!skip)
					System.out.println(line);
				
				return;
			}
			
			if(!skip)
				System.out.println(line);
		}
	}
	
}
