/**
 * Bin.java
 * @author Vagisha Sharma
 * Oct 5, 2010
 */
package org.yeastrc.ms.service.percolator.stats;

/**
 * 
 */
public class Bin implements Comparable<Bin>{

	private double binStart;
	private double binEnd;
	private int binCount;
	
	public Bin(double binStart, double binEnd) {
		this.binStart = binStart;
		this.binEnd = binEnd;
	}
	
	public double getBinStart() {
		return binStart;
	}

	public void setBinStart(double binStart) {
		this.binStart = binStart;
	}

	public double getBinEnd() {
		return binEnd;
	}

	public void setBinEnd(double binEnd) {
		this.binEnd = binEnd;
	}

	public int getBinCount() {
		return binCount;
	}

	public void setBinCount(int binCount) {
		this.binCount = binCount;
	}

	@Override
	public int compareTo(Bin o) {
		return Double.valueOf(binStart).compareTo(o.binStart);
	}
	
	public String toString() {
		return binStart+" - "+binEnd+":  "+binCount;
	}
}
