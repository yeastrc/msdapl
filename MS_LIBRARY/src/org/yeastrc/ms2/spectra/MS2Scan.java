/**
 * MS2Scan.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 1:44:38 PM
 */

package org.yeastrc.ms2.spectra;

import java.util.Vector;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Represents a specific scan from a MS2 file.
 */
public class MS2Scan {

	private int id;							// unique id of this scan (for database)
	private MS2 run;						// the ms2 run to which this scan belongs
	private int start;						// first scan
	private int end;						// second scan
	private float preMZ;					// pre m/z
	private Vector<Vector<Float>> data;		// mass intensity pairs, with a reading on each line ([m/z] [intensity])
	private Vector<MS2ScanCharge> charges;	// the charge states for this scan
	private Vector<MS2ChargeIndependentAnalysis> iAnalysis;  //the charge independent analysis
	
	
	/**
	 * @return the iAnalysis
	 */
	public Vector<MS2ChargeIndependentAnalysis> getIAnalysis() {
		return iAnalysis;
	}
	/**
	 * @param analysis the iAnalysis to set
	 */
	public void setIAnalysis(Vector<MS2ChargeIndependentAnalysis> analysis) {
		iAnalysis = analysis;
	}
	/**
	 * @return the charges
	 */
	public Vector<MS2ScanCharge> getCharges() {
		return charges;
	}
	/**
	 * @param charges the charges to set
	 */
	public void setCharges(Vector<MS2ScanCharge> charges) {
		this.charges = charges;
	}
	/**
	 * @return the data
	 */
	public Vector<Vector<Float>> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Vector<Vector<Float>> data) {
		this.data = data;
	}
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
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
	 * @return the preMZ
	 */
	public float getPreMZ() {
		return preMZ;
	}
	/**
	 * @param preMZ the preMZ to set
	 */
	public void setPreMZ(float preMZ) {
		this.preMZ = preMZ;
	}
	/**
	 * @return the run
	 */
	public MS2 getRun() {
		return run;
	}
	/**
	 * @param run the run to set
	 */
	public void setRun(MS2 run) {
		this.run = run;
	}
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	
}
