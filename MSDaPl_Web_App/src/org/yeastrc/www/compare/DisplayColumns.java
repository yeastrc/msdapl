/**
 * ColumnFilters.java
 * @author Vagisha Sharma
 * Apr 26, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.Serializable;

/**
 * 
 */
public class DisplayColumns implements Serializable {

	private boolean showPresent = true;
	private boolean showFastaId = true;
	private boolean showCommonName = true;
	private boolean showDescription = true;
	private boolean showMolWt = true;
	private boolean showPi = true;
	private boolean showTotalSeq = true;
	private boolean showNumSeq = true;
	private boolean showNumIons = true;
	private boolean showNumUniqIons = true;
	private boolean showSpectrumCount = true;
	private boolean showNsaf = false;
	
	public static final char present = 'P';
	public static final char fasta = 'F';
	public static final char commonName = 'N';
	public static final char description = 'D';
	public static final char molWt = 'M';
	public static final char pi = 'I';
	public static final char totalSeq = 'T';
	public static final char numSeq = 'Q';
	public static final char numIons = 'O';
	public static final char numUniqueIons = 'U';
	public static final char numSpectrumCount = 'S';
	public static final char nsaf = 'A';
	
	
	public boolean isShowPresent() {
		return showPresent;
	}
	public void setShowPresent(boolean showPresent) {
		this.showPresent = showPresent;
	}
	public boolean isShowFastaId() {
		return showFastaId;
	}
	public void setShowFastaId(boolean showFastaId) {
		this.showFastaId = showFastaId;
	}
	public boolean isShowCommonName() {
		return showCommonName;
	}
	public void setShowCommonName(boolean showCommonName) {
		this.showCommonName = showCommonName;
	}
	public boolean isShowDescription() {
		return showDescription;
	}
	public void setShowDescription(boolean showDescription) {
		this.showDescription = showDescription;
	}
	public boolean isShowMolWt() {
		return showMolWt;
	}
	public void setShowMolWt(boolean showMolWt) {
		this.showMolWt = showMolWt;
	}
	public boolean isShowPi() {
		return showPi;
	}
	public void setShowPi(boolean showPi) {
		this.showPi = showPi;
	}
	public boolean isShowTotalSeq() {
		return showTotalSeq;
	}
	public void setShowTotalSeq(boolean showTotalSeq) {
		this.showTotalSeq = showTotalSeq;
	}
	public boolean isShowNumSeq() {
		return showNumSeq;
	}
	public void setShowNumSeq(boolean showNumSeq) {
		this.showNumSeq = showNumSeq;
	}
	public boolean isShowNumIons() {
		return showNumIons;
	}
	public void setShowNumIons(boolean showNumIons) {
		this.showNumIons = showNumIons;
	}
	public boolean isShowNumUniqIons() {
		return showNumUniqIons;
	}
	public void setShowNumUniqIons(boolean showNumUniqIons) {
		this.showNumUniqIons = showNumUniqIons;
	}
	public boolean isShowSpectrumCount() {
		return showSpectrumCount;
	}
	public void setShowSpectrumCount(boolean showSpectrumCount) {
		this.showSpectrumCount = showSpectrumCount;
	}
	
	public boolean isShowNsaf() {
		return showNsaf;
	}
	public void setShowNsaf(boolean showNsaf) {
		this.showNsaf = showNsaf;
	}
	
	public void setNoDisplay(char columnCode) {
		
		switch (columnCode) {
		case present:  			this.showPresent = false; break;
		case fasta:    			this.showFastaId = false; break;
		case commonName: 		this.showCommonName = false; break;
		case description:		this.showDescription = false; break;
		case molWt: 			this.showMolWt = false; break;
		case pi:				this.showPi = false; break;
		case totalSeq:			this.showTotalSeq = false; break;
		case numSeq:			this.showNumSeq = false; break;
		case numIons:			this.showNumIons = false; break;
		case numUniqueIons:		this.showNumUniqIons = false; break;
		case numSpectrumCount:	this.showSpectrumCount = false; break;
		case nsaf:				this.showNsaf = false; break;
		default:
			break;
		}
	}
	
	public String getNoDisplayColCommaSeparated() {
		String str = "";
		if(!this.isShowPresent())		str += ","+present;
		if(!this.isShowFastaId())		str += ","+fasta;
		if(!this.isShowCommonName()) 	str += ","+commonName;
		if(!this.isShowDescription())	str += ","+description;
		if(!this.isShowMolWt())			str += ","+molWt;
		if(!this.isShowPi())			str += ","+pi;
		if(!this.isShowTotalSeq())		str += ","+totalSeq;
		if(!this.isShowNumSeq())		str += ","+numSeq;
		if(!this.isShowNumIons())		str += ","+numIons;
		if(!this.isShowNumUniqIons())	str += ","+numUniqueIons;
		if(!this.isShowSpectrumCount())	str += ","+numSpectrumCount;
		if(!this.isShowNsaf())			str += ","+nsaf;
		
		if(str.length() > 0)
			str = str.substring(1);
		return str;
			
	}
}
