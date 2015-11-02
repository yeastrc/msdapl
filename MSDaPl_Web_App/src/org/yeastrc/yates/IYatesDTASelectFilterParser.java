/*
 * IYatesDTASelectFilterParser.java
 * Created on Oct 13, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.util.List;
import java.io.BufferedReader;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 13, 2004
 */

public interface IYatesDTASelectFilterParser {

	/**
	 * Parse the DTASelect file represented in the supplied BufferedReader
	 * @param br The BufferedReader for the DTASelect file
	 * @return A List of YatesResult objects, parsed from the BufferedReader
	 * @throws Exception If there is a problem.
	 */
	public List parseFile(BufferedReader br, int databaseID) throws Exception;
	
}
