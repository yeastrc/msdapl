/**
 * MS2DatabaseObject.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 2:23:24 PM
 */

package org.yeastrc.ms2.spectra;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Class definition goes here
 */

public class MS2DatabaseObject extends MS2 {

	// private constructor
	private MS2DatabaseObject() {
		super();
	}

	/**
	 * Reads the next scan in the MS2 data<br>
	 * Decided to make scans available piecemeal like this, due to the sheer
	 * size of MS2 runs... some runs are over 2 gigs if we return all scans at once.
	 * Most likely, scans won't be returned this way, but rather referenced by
	 * name.
	 * 
	 * @return The next scan as a MS2Scan object, or null if done reading (at the end)
	 * @throws Exception If there is a problem reading the next scan
	 */
	public MS2Scan readScan() throws Exception {
		return null;
	}
	
	public MS2ScanReader getScanReader() { return null; }
	
}
