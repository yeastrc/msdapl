/**
 * ScanReader.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 2:50:50 PM
 */

package org.yeastrc.ms2.spectra;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Responsible for reading scans for an MS2 object
 */
public interface MS2ScanReader {

	/**
	 * Is there another scan to read?
	 * @return true if there is, false if there is not
	 */
	public boolean hasNext() throws Exception;

	/**
	 * Reads the next scan and returns the MS2Scan object
	 * @return The next MS2Scan
	 * @throws Exception if there is a problem
	 */
	public MS2Scan readNext() throws Exception;

	/**
	 * Closes the scan reader, which closes any underlying open files,
	 * database connections, or whatever else may be being used.  Always do
	 * this when done reading scans.
	 */
	public void close();

	/**
	 * Get the MS2 to which this ScanReader belongs
	 * @return The MS2 to which this ScanReader belongs
	 */
	public MS2 getMS2();
	
}
