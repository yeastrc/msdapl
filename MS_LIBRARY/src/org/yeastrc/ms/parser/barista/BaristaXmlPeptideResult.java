/**
 * 
 */
package org.yeastrc.ms.parser.barista;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * BaristaXmlPeptideResult.java
 * @author Vagisha Sharma
 * Jul 25, 2011
 * 
 */
public class BaristaXmlPeptideResult {

	private MsSearchResultPeptide resultPeptide;
    private Double score = null;
    private double qvalue = -1.0;
    
    private List<MsSearchResultProteinIn> proteins;
    private Set<Integer> baristaPsmIds;
    private int mainBaristaPsmId = -1;
    
    public BaristaXmlPeptideResult() {
    	proteins = new ArrayList<MsSearchResultProteinIn>();
    	baristaPsmIds = new HashSet<Integer>();
    }
    
    public boolean isComplete() {
    	
    	return (resultPeptide != null && qvalue != -1.0 &&
    			mainBaristaPsmId != -1 &&
				score != null &&
    			baristaPsmIds.size() > 0 &&
				proteins.size() > 0);
	}
    
    public String toString() {
		StringBuilder buf = new StringBuilder();
		try {
			buf.append("sequence: "+resultPeptide.getFullModifiedPeptide());
		} catch (ModifiedSequenceBuilderException e) {
			buf.append("sequence: ERROR building full modified sequence: "+e.getMessage());
		}
		buf.append("\n");
		buf.append("qvalue: "+qvalue);
		buf.append("\n");
		buf.append("score: "+score);
		buf.append("\n");
		if(proteins.size() == 0)
			buf.append("NO MATCHING PROTEINS\n");
		else {
			buf.append("Proteins:\n");
			for(MsSearchResultProteinIn locus: proteins) {
				buf.append(locus.getAccession()+"\n");
			}
		}
		if(baristaPsmIds.size() == 0)
			buf.append("NO PSMs\n");
		else {
			buf.append("PSMs:\n");
			for(Integer baristaPsmId: baristaPsmIds) {
				buf.append(baristaPsmId+"\n");
			}
		}
		
		buf.append("\n");
		
		return buf.toString();
	}

	public MsSearchResultPeptide getResultPeptide() {
		return resultPeptide;
	}

	public void setResultPeptide(MsSearchResultPeptide resultPeptide) {
		this.resultPeptide = resultPeptide;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public double getQvalue() {
		return qvalue;
	}

	public void setQvalue(double qvalue) {
		this.qvalue = qvalue;
	}

	public List<MsSearchResultProteinIn> getProteins() {
		return proteins;
	}

	public void addProtein(String accession) {
        DbLocus locus = new DbLocus(accession, null);
        proteins.add(locus);
    }

	public List<Integer> getBaristaPsmIds() {
		return new ArrayList<Integer>(baristaPsmIds);
	}

	public void addBaristaPsmIds(int baristaId) {
		this.baristaPsmIds.add(baristaId);
	}

	public int getMainBaristaPsmId() {
		return mainBaristaPsmId;
	}

	public void setMainBaristaPsmId(int mainBaristaPsmId) {
		this.mainBaristaPsmId = mainBaristaPsmId;
	}
}
