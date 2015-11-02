/*
 * YatesCycleParser.java
 * Created on Oct 13, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 13, 2004
 */

public class YatesCycleParser {

	/**
	 * Search the given directory for cycle files, and save it to the database for the given runID
	 * @param directoryName
	 * @param runID
	 * @throws Exception
	 */
	public void findAndSaveCycles(String directoryName, int runID) throws Exception {
		
		// Make sure we have a valid directory
		File directory = new File (directoryName);
		if (!directory.exists())
			throw new Exception ("Invalid directory name.");
		
		Set<String> filenames = new HashSet<String>();
		File[] files = directory.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			
			if (!files[i].getName().endsWith(".ms2") && !files[i].getName().endsWith("sqt"))
				continue;
			
			String name = files[i].getName();
			name = name.replaceAll("\\.ms2", "");
			name = name.replaceAll("\\.sqt", "");
			
			filenames.add(name);
			
			// clean up
			name = null;
			files[i] = null;
		}
		
		files = null;
		
		// If we didn't find anything, just leave
		if (filenames.size() == 0)
			return;
		
		Iterator iter = filenames.iterator();
		while (iter.hasNext()) {
			String filename = (String)(iter.next());
			
			YatesCycle cycle = new YatesCycle();
			cycle.setRunID(runID);
			cycle.setFileName(filename);
			cycle.save();

			try {
				File file = new File (directoryName, filename + ".ms2");
				YatesMS2 ms2Data = null;
				try {
					ms2Data = new YatesMS2();
					ms2Data.setCycleID(cycle.getID());
					ms2Data.setMS2File(file);
					ms2Data.save();
				} finally {

					// clean this up
					file = null;			
					ms2Data.setMS2Data("");
					ms2Data = null;
				}
				
			} catch (Exception e) { ; }
			finally { System.gc(); }

			try {
				File file = new File (directoryName, filename + ".sqt");
				YatesSQT sqtData = null;
				try {
					sqtData = new YatesSQT();
					sqtData.setCycleID(cycle.getID());
					sqtData.setSQTFile(file);
					sqtData.save();
				} finally {
				
					// clean this up			
					file = null;
					sqtData.setSQTData("");
					sqtData = null;
				}
				
			} catch (Exception e) { ; }
			finally { System.gc(); }
		}
	}
}
