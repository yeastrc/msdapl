/**
 * MS2ChargeIndependentAnalysis.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 1:57:55 PM
 */

package org.yeastrc.ms2.spectra;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Represents an "I" line in the MS2 file.  Charge independent analysis for a particular scan.
 */
public class MS2ChargeIndependentAnalysis {

	private int id;				// unique id number (used by database)
	private MS2Scan scan;		// the that was analyzed
	private String header;		// the label of the analysis data
	private String value;		// the value of the analysis data
	
	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}
	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the scan
	 */
	public MS2Scan getScan() {
		return scan;
	}
	/**
	 * @param scan the scan to set
	 */
	public void setScan(MS2Scan scan) {
		this.scan = scan;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
