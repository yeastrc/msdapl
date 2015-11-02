/**
 * PeptideAAFrequencyResult.java
 * @author Vagisha Sharma
 * Feb 25, 2011
 */
package org.yeastrc.ms.domain.analysis.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public class PeptideTerminalAAResult {

	private int analysisId;
	private double scoreCutoff;
	private String scoreType;
	
	private Map<Character, AminoAcidTermCount> aaCounts = new HashMap<Character, AminoAcidTermCount>();
	
	private int totalResultCount;
	
	private int numResultsWithEnzTerm_0;
	private int numResultsWithEnzTerm_1;
	private int numResultsWithEnzTerm_2;
	
	private MsEnzyme enzyme;
	
	
	public int getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(int analysisId) {
		this.analysisId = analysisId;
	}

	public double getScoreCutoff() {
		return scoreCutoff;
	}

	public void setScoreCutoff(double scoreCutoff) {
		this.scoreCutoff = scoreCutoff;
	}

	public String getScoreType() {
		return scoreType;
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}

	public void setEnzyme(MsEnzyme enzyme) {
		this.enzyme = enzyme;
	}
	
	public MsEnzyme getEnzyme() {
		return this.enzyme;
	}
	
	public void setTotalResultCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
	}
	
	public int getTotalResultCount() {
		return this.totalResultCount;
	}
	
	public Set<Character> getAminoAcids() {
		return aaCounts.keySet();
	}
	
	public void setAminoAcidTermCounts(Map<Character, AminoAcidTermCount> aaCounts) {
		this.aaCounts = aaCounts;
	}
	
	public Map<Character, AminoAcidTermCount> getAminoAcidTermCounts() {
		return aaCounts;
	}
	
	
	public Set<Character> getTopThreeAminoAcidsNtermMinusOne() {
		
		List<Character> sorted = new ArrayList<Character>();
		sorted.addAll(getAminoAcids());
		Collections.sort(sorted, new Comparator<Character>() {
			
			@Override
			public int compare(Character o1, Character o2) {
				return Integer.valueOf(getNtermMinusOneCountForAA(o2)).compareTo(getNtermMinusOneCountForAA(o1));
			}
		});
		
		Set<Character> topThree = new HashSet<Character>();
		topThree.add(sorted.get(0));
		topThree.add(sorted.get(1));
		topThree.add(sorted.get(2));
		
		return topThree;
	}
	
	public Set<Character> getTopThreeAminoAcidsCterm() {
		
		List<Character> sorted = new ArrayList<Character>();
		sorted.addAll(getAminoAcids());
		Collections.sort(sorted, new Comparator<Character>() {
			
			@Override
			public int compare(Character o1, Character o2) {
				return Integer.valueOf(getCtermCountForAA(o2)).compareTo(getCtermCountForAA(o1));
			}
		});
		
		Set<Character> topThree = new HashSet<Character>();
		topThree.add(sorted.get(0));
		topThree.add(sorted.get(1));
		topThree.add(sorted.get(2));
		
		return topThree;
	}
	
	
	// -----------------------------------------------------------------------------------------------
	// N-Term -1 counts
	// -----------------------------------------------------------------------------------------------
	public int getNtermMinusOneCountForAA(char aa) {
		AminoAcidTermCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getNtermMinusOneCount();
	}
	
	public double getNtermMinusOneFreqForAA(char aa) {
		
		return roundOne((getNtermMinusOneCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	/**
	 * @return String representation of amino acid counts at the nterm - 1 index. 
	 * Example A:0;K:59;L:2;R:45;T:0
	 */
	public String getNtermMinusOneCountsString() {
		StringBuilder buf = new StringBuilder();
		char[] aaArr = new BaseAminoAcidUtils().getAminoAcidChars();
		for(char aa: aaArr) {
			buf.append(";"+aa+":"+getNtermMinusOneCountForAA(aa));
		}
		buf.deleteCharAt(0);
		return buf.toString();
	}
	
	// -----------------------------------------------------------------------------------------------
	// N-Term counts
	// -----------------------------------------------------------------------------------------------
	public int getNtermCountForAA(char aa) {
		AminoAcidTermCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getNtermCount();
	}
	
	public double getNtermFreqForAA(char aa) {
		return roundOne((getNtermCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	/**
	 * @return String representation of amino acid counts at the nterm index. 
	 * Example A:0;K:59;L:2;R:45;T:0
	 */
	public String getNtermCountsString() {
		StringBuilder buf = new StringBuilder();
		char[] aaArr = new BaseAminoAcidUtils().getAminoAcidChars();
		for(char aa: aaArr) {
			buf.append(";"+aa+":"+getNtermCountForAA(aa));
		}
		buf.deleteCharAt(0);
		return buf.toString();
	}
	
	// -----------------------------------------------------------------------------------------------
	// N-Term counts
	// -----------------------------------------------------------------------------------------------
	public int getCtermCountForAA(char aa) {
		AminoAcidTermCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getCtermCount();
	}
	
	public double getCtermFreqForAA(char aa) {
		return roundOne((getCtermCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	/**
	 * @return String representation of amino acid counts at the cterm index. 
	 * Example A:0;K:59;L:2;R:45;T:0
	 */
	public String getCtermCountsString() {
		StringBuilder buf = new StringBuilder();
		char[] aaArr = new BaseAminoAcidUtils().getAminoAcidChars();
		for(char aa: aaArr) {
			buf.append(";"+aa+":"+getCtermCountForAA(aa));
		}
		buf.deleteCharAt(0);
		return buf.toString();
	}
	
	// -----------------------------------------------------------------------------------------------
	// N-Term counts
	// -----------------------------------------------------------------------------------------------
	public int getCtermPlusOneCountForAA(char aa) {
		AminoAcidTermCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getCtermPlusOneCount();
	}
	
	public double getCtermPlusOneFreqForAA(char aa) {
		return roundOne((getCtermPlusOneCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	/**
	 * @return String representation of amino acid counts at the cterm index. 
	 * Example A:0;K:59;L:2;R:45;T:0
	 */
	public String getCtermPlusOneCountsString() {
		StringBuilder buf = new StringBuilder();
		char[] aaArr = new BaseAminoAcidUtils().getAminoAcidChars();
		for(char aa: aaArr) {
			buf.append(";"+aa+":"+getCtermPlusOneCountForAA(aa));
		}
		buf.deleteCharAt(0);
		return buf.toString();
	}
	
	
	// -----------------------------------------------------------------------------------------------
	// Methods for getting and setting the number of results with 0, 1 or 2 enzymatic termini
	// -----------------------------------------------------------------------------------------------
	public void setNumResultsWithEnzTerm_0(int numResultsWithEnzTerm_0) {
		this.numResultsWithEnzTerm_0 = numResultsWithEnzTerm_0;
	}

	public int getNumResultsWithEnzTerm_0() {
		return numResultsWithEnzTerm_0;
	}
	
	public double getPctResultsWithEnzTerm_0() {
		return roundOne((numResultsWithEnzTerm_0 * 100.0) / (double)totalResultCount);
	}

	public void setNumResultsWithEnzTerm_1(int numResultsWithEnzTerm_1) {
		this.numResultsWithEnzTerm_1 = numResultsWithEnzTerm_1;
	}

	public int getNumResultsWithEnzTerm_1() {
		return numResultsWithEnzTerm_1;
	}
	
	public double getPctResultsWithEnzTerm_1() {
		return roundOne((numResultsWithEnzTerm_1 * 100.0) / (double)totalResultCount);
	}

	public void setNumResultsWithEnzTerm_2(int numResultsWithEnzTerm_2) {
		this.numResultsWithEnzTerm_2 = numResultsWithEnzTerm_2;
	}
	
	public int getNumResultsWithEnzTerm_2() {
		return numResultsWithEnzTerm_2;
	}
	
	public double getPctResultsWithEnzTerm_2() {
		return roundOne((numResultsWithEnzTerm_2 * 100.0) / (double)totalResultCount);
	}
	
	
	public void combineWith(PeptideTerminalAAResult anotherResult) {
		
		this.totalResultCount += anotherResult.getTotalResultCount();
		this.numResultsWithEnzTerm_0 += anotherResult.getNumResultsWithEnzTerm_0();
		this.numResultsWithEnzTerm_1 += anotherResult.getNumResultsWithEnzTerm_1();
		this.numResultsWithEnzTerm_2 += anotherResult.getNumResultsWithEnzTerm_2();
		
		for(Character aa: anotherResult.getAminoAcids()) {
			AminoAcidTermCount myCount = this.getCounts(aa);
			if(myCount == null) {
				myCount = new AminoAcidTermCount(aa);
				this.aaCounts.put(aa, myCount);
			}
			
			AminoAcidTermCount theirCount = anotherResult.getCounts(aa);
			if(theirCount != null) {
				myCount.combineWith(theirCount);
			}
		}
	}
	
	private AminoAcidTermCount getCounts(char aa) {
		return aaCounts.get(aa);
	}
	
	private double roundOne(double num) {
        return Math.round(num*10.0)/10.0;
    }
	
	public String toString() {
		
		StringBuilder buf = new StringBuilder();
		buf.append("analysisID: "+this.analysisId+"\n");
		buf.append("scoreCutoff: "+this.scoreCutoff+"; scoreType: "+this.scoreType+"\n");
		buf.append("totalResultCount: "+this.totalResultCount+"\n");
		buf.append("numResultsWithEnzTerm_0: "+this.numResultsWithEnzTerm_0+"\n");
		buf.append("numResultsWithEnzTerm_1: "+this.numResultsWithEnzTerm_1+"\n");
		buf.append("numResultsWithEnzTerm_2: "+this.numResultsWithEnzTerm_2+"\n");
		if(this.enzyme != null)
			buf.append("enzyme: "+this.enzyme.getName()+"\n");
		buf.append("AA\tNterm-1\tNterm\tCterm\tCterm+1\n");
		for(Character aa: this.aaCounts.keySet()) {
			buf.append(aaCounts.get(aa).toString()+"\n");
		}
		return buf.toString();
	}
}
