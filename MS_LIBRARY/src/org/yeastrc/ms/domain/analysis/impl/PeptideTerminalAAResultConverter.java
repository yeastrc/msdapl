/**
 * PeptideTerminalAAResultConverter.java
 * @author Vagisha Sharma
 * Mar 4, 2011
 */
package org.yeastrc.ms.domain.analysis.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.general.EnzymeFactory;
import org.yeastrc.ms.domain.general.EnzymeFactoryException;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.impl.EnzymeBean;

/**
 * 
 */
public class PeptideTerminalAAResultConverter {

	private PeptideTerminalAAResultConverter () {}
	
	private static final Logger log = Logger.getLogger(PeptideTerminalAAResultConverter.class);
	
	public static PeptideTerminalAAResult convert(PeptideTerminalAAResultDb resultDb) {
		
		if(resultDb == null)
			return null;
		
		PeptideTerminalAAResult result = new PeptideTerminalAAResult();
		
		result.setAnalysisId(resultDb.getAnalysisId());
		
		result.setTotalResultCount(resultDb.getTotalResultCount());
		
		result.setScoreCutoff(resultDb.getScoreCutoff());
		result.setScoreType(resultDb.getScoreType());
		
		result.setNumResultsWithEnzTerm_0(resultDb.getNumResultsWithEnzTerm_0());
		result.setNumResultsWithEnzTerm_1(resultDb.getNumResultsWithEnzTerm_1());
		result.setNumResultsWithEnzTerm_2(resultDb.getNumResultsWithEnzTerm_2());
		
		int enzymeId = resultDb.getEnzymeId();
		if(enzymeId != 0) {
			MsEnzyme enzyme = DAOFactory.instance().getEnzymeDAO().loadEnzyme(enzymeId);
			result.setEnzyme(enzyme);
		}
		else if (resultDb.getEnzyme() != null) {
			String enzymeName = resultDb.getEnzyme();
			try {
				result.setEnzyme(EnzymeFactory.getEnzyme(enzymeName));
			} catch (EnzymeFactoryException e) {
				
				EnzymeBean enz = new EnzymeBean();
				enz.setName(enzymeName);
				enz.setNocut("UNKNOWN");
				enz.setCut("UNKNOWN");
				result.setEnzyme(enz);
				log.error("Cannot find enzyme for name: "+enzymeName);
			}
		}
		
		Map<Character, AminoAcidTermCount> aaCounts = new HashMap<Character, AminoAcidTermCount>();
		
		// Nterm - 1 
		String str = resultDb.getNtermMinusOneAminoAcidCount();
		if(str != null) {
			String[] counts = str.split(";");
			for(String count: counts) {
				char aa = count.substring(0,count.indexOf(":")).charAt(0);
				Integer aacount = Integer.parseInt(count.substring(count.indexOf(":")+1));
				AminoAcidTermCount aaTermCount = aaCounts.get(aa);
				if(aaTermCount == null) {
					aaTermCount = new AminoAcidTermCount(aa);
					aaCounts.put(aa, aaTermCount);
				}
				aaTermCount.setNtermMinusOneCount(aacount);
			}
		}
		
		// Nterm
		str = resultDb.getNtermAminoAcidCount();
		if(str != null) {
			String[] counts = str.split(";");
			for(String count: counts) {
				char aa = count.substring(0,count.indexOf(":")).charAt(0);
				Integer aacount = Integer.parseInt(count.substring(count.indexOf(":")+1));
				AminoAcidTermCount aaTermCount = aaCounts.get(aa);
				if(aaTermCount == null) {
					aaTermCount = new AminoAcidTermCount(aa);
					aaCounts.put(aa, aaTermCount);
				}
				aaTermCount.setNtermCount(aacount);
			}
		}
		
		// Cterm
		str = resultDb.getCtermAminoAcidCount();
		if(str != null) {
			String[] counts = str.split(";");
			for(String count: counts) {
				char aa = count.substring(0,count.indexOf(":")).charAt(0);
				Integer aacount = Integer.parseInt(count.substring(count.indexOf(":")+1));
				AminoAcidTermCount aaTermCount = aaCounts.get(aa);
				if(aaTermCount == null) {
					aaTermCount = new AminoAcidTermCount(aa);
					aaCounts.put(aa, aaTermCount);
				}
				aaTermCount.setCtermCount(aacount);
			}
		}
		
		// Cterm + 1
		str = resultDb.getCtermPlusOneAminoAcidCount();
		if(str != null) {
			String[] counts = str.split(";");
			for(String count: counts) {
				char aa = count.substring(0,count.indexOf(":")).charAt(0);
				Integer aacount = Integer.parseInt(count.substring(count.indexOf(":")+1));
				AminoAcidTermCount aaTermCount = aaCounts.get(aa);
				if(aaTermCount == null) {
					aaTermCount = new AminoAcidTermCount(aa);
					aaCounts.put(aa, aaTermCount);
				}
				aaTermCount.setCtermPlusOneCount(aacount);
			}
		}
		
		result.setAminoAcidTermCounts(aaCounts);
		
		return result;
	}
	
	public static PeptideTerminalAAResultDb convert(PeptideTerminalAAResult result) {
		
		if(result == null)
			return null;
		
		PeptideTerminalAAResultDb resultDb = new PeptideTerminalAAResultDb();
		resultDb.setAnalysisId(result.getAnalysisId());
		resultDb.setScoreCutoff(result.getScoreCutoff());
		resultDb.setScoreType(result.getScoreType());
		resultDb.setTotalResultCount(result.getTotalResultCount());
		resultDb.setNumResultsWithEnzTerm_0(result.getNumResultsWithEnzTerm_0());
		resultDb.setNumResultsWithEnzTerm_1(result.getNumResultsWithEnzTerm_1());
		resultDb.setNumResultsWithEnzTerm_2(result.getNumResultsWithEnzTerm_2());
		MsEnzyme enzyme = result.getEnzyme();
		if(enzyme != null) {
			resultDb.setEnzyme(enzyme.getName());
			if(enzyme.getId() > 0) {
				resultDb.setEnzymeId(enzyme.getId());
			}
		}
		resultDb.setNtermMinusOneAminoAcidCount(result.getNtermMinusOneCountsString());
		resultDb.setNtermAminoAcidCount(result.getNtermCountsString());
		resultDb.setCtermAminoAcidCount(result.getCtermCountsString());
		resultDb.setCtermPlusOneAminoAcidCount(result.getCtermPlusOneCountsString());
		
		return resultDb;
	}
}
