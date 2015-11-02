/**
 * PeptideTerminalAAResultBuilder.java
 * @author Vagisha Sharma
 * Mar 4, 2011
 */
package org.yeastrc.ms.domain.analysis.impl;

import java.util.HashMap;
import java.util.Map;

import org.yeastrc.ms.domain.general.MsEnzyme;

/**
 * 
 */
public class PeptideTerminalAAResultBuilder {

	private int analysisId;
	private Map<Character, AminoAcidTermCount> aaCounts = new HashMap<Character, AminoAcidTermCount>();
	private int totalResultCount;
	private double scoreCutoff;
	private String scoreType;
	private MsEnzyme enzyme;
	private int numResultsWithEnzTerm_0;
	private int numResultsWithEnzTerm_1;
	private int numResultsWithEnzTerm_2;
	
	private PeptideTerminalAAResult result = null;

	
	public PeptideTerminalAAResultBuilder (int analysisId, MsEnzyme enzyme) {
		this.enzyme = enzyme;
		this.analysisId = analysisId;
	}
	
	public PeptideTerminalAAResult getResult() {
		
		if(result == null) {
			
			result = new PeptideTerminalAAResult();
			result.setAnalysisId(analysisId);
			result.setEnzyme(enzyme);
			result.setTotalResultCount(totalResultCount);
			result.setScoreCutoff(scoreCutoff);
			result.setScoreType(scoreType);
			result.setAminoAcidTermCounts(aaCounts);
			result.setNumResultsWithEnzTerm_0(numResultsWithEnzTerm_0);
			result.setNumResultsWithEnzTerm_1(numResultsWithEnzTerm_1);
			result.setNumResultsWithEnzTerm_2(numResultsWithEnzTerm_2);
		}
		
		return result;
	}	
	
	public void setTotalResultCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
	}
	
	public void setScoreCutoff(double scoreCutoff) {
		this.scoreCutoff = scoreCutoff;
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}

	public void addEnzymaticTerminiCount(int numEnzymaticTermini) {
		switch (numEnzymaticTermini) {
			case 0:
				numResultsWithEnzTerm_0++;
				break;
			case 1:
				numResultsWithEnzTerm_1++;
				break;
			case 2:
				numResultsWithEnzTerm_2++;
				break;
		}
	}
	
	
	public void addNtermMinusOneCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.addNtermMinusOneCount();
	}
	
	public void setNtermMinusOneCount(char aa, int aacount) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.setNtermMinusOneCount(aacount);
	}
	
	public void addNtermCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.addNtermCount();
	}
	
	public void setNtermCount(char aa, int aacount) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.setNtermCount(aacount);
	}
	
	
	public void addCtermCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.addCtermCount();
	}
	
	public void setCtermCount(char aa, int aacount) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.setCtermCount(aacount);
	}
	
	public void addCtermPlusOneCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.addCtermPlusOneCount();
	}
	
	public void setCtermPlusOneCount(char aa, int aacount) {
		if(aa == '-')
			return;
		AminoAcidTermCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidTermCount(aa);
			aaCounts.put(aa, count);
		}
		count.setCtermPlusOneCount(aacount);
	}
}
