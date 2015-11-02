/**
 * PercolatorXmlPeptideResult.java
 * @author Vagisha Sharma
 * Sep 16, 2010
 */
package org.yeastrc.ms.parser.percolator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * 
 */
public class PercolatorXmlPeptideResult implements PercolatorPeptideResultIn {

	private MsSearchResultPeptide resultPeptide;
    private double pep = -1.0;
    private Double discriminantScore = null;
    private double qvalue = -1.0;
    private double pvalue = -1.0;
    private BigDecimal observedMass;
	
	private List<MsSearchResultProteinIn> matchingLoci;
    private List<PercolatorXmlPsmId> matchingPsmIds;
    
    private boolean isDecoy = false;
    
    public PercolatorXmlPeptideResult() {
    	matchingLoci = new ArrayList<MsSearchResultProteinIn>();
    	matchingPsmIds = new ArrayList<PercolatorXmlPsmId>();
    }
    
    public boolean isComplete() {
    	
		return (resultPeptide != null && pep != -1.0 && qvalue != -1.0 &&
				observedMass != null &&
				matchingPsmIds.size() > 0 &&
				matchingLoci.size() > 0);
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
		buf.append("pep: "+pep);
		buf.append("\n");
		buf.append("discriminantScore: "+discriminantScore);
		buf.append("\n");
		buf.append("pvalue: "+pvalue);
		buf.append("\n");
		buf.append("observedMass: "+observedMass);
		buf.append("\n");
		if(matchingLoci.size() == 0)
			buf.append("NO MATCHING PROTEINS\n");
		else {
			buf.append("Proteins:\n");
			for(MsSearchResultProteinIn locus: matchingLoci) {
				buf.append(locus.getAccession()+"\n");
			}
		}
		if(matchingPsmIds.size() == 0)
			buf.append("NO MATCHING PSMs\n");
		else {
			buf.append("PSMs:\n");
			for(PercolatorXmlPsmId psm: matchingPsmIds) {
				buf.append(psm+"\n");
			}
		}
		
		buf.append("\n");
		
		return buf.toString();
	}
    
    @Override
	public MsSearchResultPeptide getResultPeptide() {
		return this.resultPeptide;
	}

	public void setResultPeptide(MsSearchResultPeptide peptide) {
		this.resultPeptide = peptide;
	}
	
	@Override
    public double getQvalue() {
        return qvalue;
    }
    
    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }
    
    @Override
    public double getPosteriorErrorProbability() {
        return pep;
    }
    
    public void setPosteriorErrorProbability(double pep) {
        this.pep = pep;
    }
    
    
    public BigDecimal getObservedMass() {
		return observedMass;
	}

	public void setObservedMass(BigDecimal observedMass) {
		this.observedMass = observedMass;
	}

	@Override
    public Double getDiscriminantScore() {
        return discriminantScore;
    }
    
    public void setDiscriminantScore(Double score) {
        this.discriminantScore = score;
    }

    @Override
    public double getPvalue() {
    	return pvalue;
    }
    
    public void setPvalue(double pvalue) {
    	this.pvalue = pvalue;
    }

	@Override
	public List<PercolatorXmlPsmId> getPsmIds() {
		return this.matchingPsmIds;
	}
	
	public void addMatchingPsmId(String id) throws IllegalArgumentException {
		if(id != null)
			matchingPsmIds.add(PercolatorPsmIdParser.parse(id));
	}

	public void addMatchingLocus(String accession, String description) {
        DbLocus locus = new DbLocus(accession, description);
        matchingLoci.add(locus);
    }
	
	@Override
	public List<MsSearchResultProteinIn> getProteinMatchList() {
        return this.matchingLoci;
    }
	
	public boolean isDecoy() {
		return isDecoy;
	}

	public void setDecoy(boolean isDecoy) {
		this.isDecoy = isDecoy;
	}
}
