/**
 * MS2ScanCharge.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 1:58:48 PM
 */

package org.yeastrc.ms2.spectra;

import java.util.*;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Represents a "Z" line from a MS2 file.  Describes the charge for a scan,
 * a scan can have multiple predicted charges
 */
public class MS2ScanCharge {

	private int id;			// unique id number (used by database)
	private MS2Scan scan;	// the scan this is describing
	private int charge;		// the charge state
	private float mass;		// predicted [M+H]+ (mass)
	private Vector<MS2ChargeDependentAnalysis> analysis;	// the charge dependent analysis objects for this scan charge

	/**
	 * @return the analysis
	 */
	public Vector<MS2ChargeDependentAnalysis> getAnalysis() {
		return analysis;
	}
	/**
	 * @param analysis the analysis to set
	 */
	public void setAnalysis(Vector<MS2ChargeDependentAnalysis> analysis) {
		this.analysis = analysis;
	}
	/**
	 * @return the charge
	 */
	public int getCharge() {
		return charge;
	}
	/**
	 * @param charge the charge to set
	 */
	public void setCharge(int charge) {
		this.charge = charge;
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
	 * @return the mass
	 */
	public float getMass() {
		return mass;
	}
	/**
	 * @param mass the mass to set
	 */
	public void setMass(float mass) {
		this.mass = mass;
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
	
}
