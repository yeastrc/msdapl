/**
 * 
 */
package org.yeastrc.ms.parser.barista;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * BaristaXmlPsmResult.java
 * @author Vagisha Sharma
 * Jul 25, 2011
 * 
 */
public class BaristaXmlPsmResult {

	private int baristaId;
	private double qvalue = -1.0;
	private Double score = null;
	private int scanNumber = -1;
	private int charge = -1;
	private BigDecimal observedMass = null;
	private MsSearchResultPeptide resultPeptide;
	private String file;
	
	public boolean isComplete() {
    	
		return (resultPeptide != null && qvalue != -1.0 &&
				scanNumber != -1 && charge != -1 &&
				score != null &&
				observedMass != null);
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		try {
			buf.append("sequence: "+resultPeptide.getFullModifiedPeptide());
		} catch (ModifiedSequenceBuilderException e) {
			buf.append("sequence: ERROR building full modified sequence: "+e.getMessage());
		}
		buf.append("\n");
		buf.append("BaristaID: "+baristaId);
		buf.append("\n");
		buf.append("Scan: "+scanNumber);
		buf.append("\n");
		buf.append("Charge: "+charge);
		buf.append("\n");
		buf.append("ObservedMass: "+observedMass);
		buf.append("\n");
		buf.append("qvalue: "+qvalue);
		buf.append("\n");
		buf.append("score: "+score);
		buf.append("\n");
		buf.append("File: "+file);
		buf.append("\n");
		
		return buf.toString();
	}

	public int getBaristaId() {
		return baristaId;
	}

	public void setBaristaId(int baristaId) {
		this.baristaId = baristaId;
	}

	public double getQvalue() {
		return qvalue;
	}

	public void setQvalue(double qvalue) {
		this.qvalue = qvalue;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public BigDecimal getObservedMass() {
		return observedMass;
	}

	public void setObservedMass(BigDecimal observedMass) {
		this.observedMass = observedMass;
	}

	public MsSearchResultPeptide getResultPeptide() {
		return resultPeptide;
	}

	public void setResultPeptide(MsSearchResultPeptide resultPeptide) {
		this.resultPeptide = resultPeptide;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
