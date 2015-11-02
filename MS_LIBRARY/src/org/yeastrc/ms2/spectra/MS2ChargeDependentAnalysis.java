/**
 * MS2ChargeDependentAnalysis.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 1:59:54 PM
 */

package org.yeastrc.ms2.spectra;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Abstraction of the "D" lines from MS2 files.  Describes the charge-dependent analysis for a
 * particular charge for a particular scan
 */
public class MS2ChargeDependentAnalysis {

	private int id;							// unique id (used for database)
	private MS2ScanCharge scanCharge;		// the predicated charge for a particular scan
	private String header;					// the label for the analysis
	private String value;					// the value for the analysis
	
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
	 * @return the scanCharge
	 */
	public MS2ScanCharge getScanCharge() {
		return scanCharge;
	}
	/**
	 * @param scanCharge the scanCharge to set
	 */
	public void setScanCharge(MS2ScanCharge scanCharge) {
		this.scanCharge = scanCharge;
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
