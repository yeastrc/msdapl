/**
 * AminoAcidTermCount.java
 * @author Vagisha Sharma
 * Mar 4, 2011
 */
package org.yeastrc.ms.domain.analysis.impl;

import org.apache.log4j.Logger;


/**
 * 
 */
public class AminoAcidTermCount {

	private final char aa;
	private int ntermMinusOneCount;
	private int ntermCount;
	private int ctermCount;
	private int ctermPlusOneCount;
	
	private static final Logger log = Logger.getLogger(AminoAcidTermCount.class);
	
	public AminoAcidTermCount(char aa) {
		this.aa = aa;
	}
	public char getAa() {
		return aa;
	}
	
	public int getNtermMinusOneCount() {
		return ntermMinusOneCount;
	}
	
	public void setNtermMinusOneCount(int ntermMinusOneCount) {
		this.ntermMinusOneCount = ntermMinusOneCount;
	}
	
	void addNtermMinusOneCount() {
		this.ntermMinusOneCount++;
	}
	
	public int getNtermCount() {
		return ntermCount;
	}
	
	
	public void setNtermCount(int ntermCount) {
		this.ntermCount = ntermCount;
	}
	
	void addNtermCount() {
		this.ntermCount++;
	}
	
	public int getCtermCount() {
		return ctermCount;
	}
	
	public void setCtermCount(int ctermCount) {
		this.ctermCount = ctermCount;
	}
	
	void addCtermCount() {
		this.ctermCount++;
	}
	
	public int getCtermPlusOneCount() {
		return ctermPlusOneCount;
	}
	
	
	public void setCtermPlusOneCount(int ctermPlusOneCount) {
		this.ctermPlusOneCount = ctermPlusOneCount;
	}
	
	void addCtermPlusOneCount() {
		this.ctermPlusOneCount++;
	}
	
	void combineWith(AminoAcidTermCount other) {
		if(other == null) 
			return;
		if(other.getAa() != this.aa){
			log.error("Cannot combine with counts for amino acid: "+this.aa+" with "+other.getAa());
			return;
		}
		this.ntermMinusOneCount += other.getNtermMinusOneCount();
		this.ntermCount += other.getNtermCount();
		this.ctermCount += other.getCtermCount();
		this.ctermPlusOneCount += other.getCtermPlusOneCount();
	}
	
	public String toString() {
		
		StringBuilder buf = new StringBuilder();
		buf.append(this.aa+"\t"+this.ntermMinusOneCount+"\t"+this.ntermCount+"\t"+this.ctermCount+"\t"+this.ctermPlusOneCount);
		return buf.toString();
	}
}
