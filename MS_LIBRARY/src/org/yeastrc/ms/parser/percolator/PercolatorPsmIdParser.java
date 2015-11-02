/**
 * PercolatorPsmIdParser.java
 * @author Vagisha Sharma
 * Sep 16, 2010
 */
package org.yeastrc.ms.parser.percolator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class PercolatorPsmIdParser {

	
	// Example: 091120-DDAvsDIA-yeast-DIA-880-940-01_22800_2_1
    private static Pattern idPattern = Pattern.compile("^(.*)_(\\d+)_(\\d+)_\\d$");
    
    private PercolatorPsmIdParser() {}
    
	public static PercolatorXmlPsmId parse(String idString) throws IllegalArgumentException {
		
		
		if(idString == null || idString.trim().length() == 0) 
			throw new IllegalArgumentException("Id string was either null or empty");
		
		Matcher m = idPattern.matcher(idString);
		if(m.matches()) {
			try {
				String fileName = m.group(1);
				int scanNumber = Integer.parseInt(m.group(2));
				int charge = Integer.parseInt(m.group(3));
				return new PercolatorXmlPsmId(fileName, scanNumber, charge);
			}
			catch(Exception e) {
				throw new IllegalArgumentException("Error parsing id string: "+idString, e);
			}
		}
		else {
			throw new IllegalArgumentException("Id string does not match required pattern");
		}
	}

	
	public static void main(String[] args) {
		String id = "091120-DDAvsDIA-yeast-DIA-880-940-01_22800_2_1";
		PercolatorPsmIdParser parser = new PercolatorPsmIdParser();
		PercolatorXmlPsmId psmId = parser.parse(id);
		System.out.println(psmId.getFileName());
		System.out.println(psmId.getCharge());
		System.out.println(psmId.getScanNumber());
	}
}
