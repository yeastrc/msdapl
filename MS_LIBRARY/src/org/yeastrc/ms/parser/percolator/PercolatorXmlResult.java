/**
 * PercolatorXmlResult.java
 * @author Vagisha Sharma
 * Sep 16, 2010
 */
package org.yeastrc.ms.parser.percolator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * 
 */
public class PercolatorXmlResult implements PercolatorResultIn {

	private String id;
    private double pep = -1.0;
    private Double discriminantScore = null;
    private double qvalue = -1.0;
    private double pvalue = -1.0;
    private BigDecimal observedMass;
    private BigDecimal predictedRT;
    
    private PercolatorXmlPsmId psmId;
	
	private List<MsSearchResultProteinIn> matchingLoci;
    private MsSearchResultPeptide resultPeptide;
    
    private boolean isDecoy = false;
    
    public PercolatorXmlResult() {
        matchingLoci = new ArrayList<MsSearchResultProteinIn>();
    }
    
    public String getId() {
		return id;
	}

	public void setId(String id) throws IllegalArgumentException {
		this.id = id;
		this.psmId = PercolatorPsmIdParser.parse(id);
	}
	
	public boolean isComplete() {
		return (id != null && pep != -1.0 && qvalue != -1.0 
				&& observedMass != null
				&& resultPeptide != null &&
				matchingLoci.size() > 0);
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id: "+id);
		buf.append("\tfileName: "+psmId.getFileName());
		buf.append("\tscanNumber: "+psmId.getScanNumber());
		buf.append("\tcharge: "+psmId.getCharge());
		buf.append("\n");
		if(resultPeptide != null) {
			
//			try {
			// 02.22.13 Changed from getFullModifiedPeptide to getFullModifiedPeptidePS because
			//          PercolatorXmlFileReader$PeptideUnparsedModifications.getFullModifiedPeptide() is unsupported.
				buf.append("peptide: "+resultPeptide.getFullModifiedPeptidePS());
//			} catch (ModifiedSequenceBuilderException e) {
//				buf.append("ERROR building peptide sequence for: "+resultPeptide.getPeptideSequence());
//			}
		}
		else
			buf.append("peptide: NULL");
		buf.append("\n");
		buf.append("qvalue: "+qvalue);
		buf.append("\n");
		buf.append("pep: "+pep);
		buf.append("\n");
		buf.append("discriminantScore: "+discriminantScore);
		buf.append("\n");
		buf.append("pvalue: "+pvalue);
		buf.append("\n");
		buf.append("predictedRT: "+predictedRT);
		buf.append("\n");
		buf.append("observedMass: "+observedMass);
		buf.append("\n");
		if(matchingLoci.size() == 0)
			buf.append("NO MATCHING PROTEINS");
		else {
			buf.append("Proteins:\n");
			for(MsSearchResultProteinIn locus: matchingLoci) {
				buf.append(locus.getAccession()+"\n");
			}
		}
		buf.append("\n");
		
		return buf.toString();
	}

	public String getFileName() {
		return this.psmId.getFileName();
	}
	
	public PercolatorXmlPsmId getPsmId() {
		return this.psmId;
	}
	
	@Override
    public Double getDiscriminantScore() {
        return discriminantScore;
    }
    
    public void setDiscriminantScore(Double score) {
        this.discriminantScore = score;
    }

    @Override
    public double getPosteriorErrorProbability() {
        return pep;
    }
    
    public void setPosteriorErrorProbability(double pep) {
        this.pep = pep;
    }

    @Override
    public double getQvalue() {
        return qvalue;
    }
    
    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }

    @Override
    public double getPvalue() {
    	return pvalue;
    }
    
    public void setPvalue(double pvalue) {
    	this.pvalue = pvalue;
    }

    
	@Override
	public BigDecimal getPredictedRetentionTime() {
		return predictedRT;
	}
	
	public void setPredictedRetentionTime(BigDecimal predictedRT) {
		this.predictedRT = predictedRT;
	}

	@Override
	public int getScanNumber() {
		return this.psmId.getScanNumber();
	}

	@Override
	public void setScanNumber(int scanNumber) {
		this.psmId.setScanNumber(scanNumber);
	}

	@Override
	public int getCharge() {
		return this.psmId.getCharge();
	}
	
	@Override
	public void setCharge(int charge) {
		this.psmId.setCharge(charge);
	}

	@Override
	public BigDecimal getObservedMass() {
		//throw new UnsupportedOperationException("Observed Mass not read from Percolator XML outout");
		return this.observedMass;
	}
	
	@Override
	public void setObservedMass(BigDecimal mass) {
		//throw new UnsupportedOperationException("Observed Mass not read from Percolator XML outout");
		this.observedMass = mass;
	}
	
	public void addMatchingLocus(String accession, String description) {
        DbLocus locus = new DbLocus(accession, description);
        addMatchingProteinMatch(locus);
    }
	
	@Override
	public void addMatchingProteinMatch(MsSearchResultProteinIn match) {
		matchingLoci.add(match);
	}

	@Override
	public List<MsSearchResultProteinIn> getProteinMatchList() {
        return this.matchingLoci;
    }

	@Override
	public MsSearchResultPeptide getResultPeptide() {
		return this.resultPeptide;
	}
	
	@Override
	public void setResultPeptide(MsSearchResultPeptide resultPeptide) {
		this.resultPeptide = resultPeptide;
	}

	@Override
	public ValidationStatus getValidationStatus() {
		throw new UnsupportedOperationException("Validation status not read from Percolator XML outout");
	}

	public boolean isDecoy() {
		return isDecoy;
	}

	public void setDecoy(boolean isDecoy) {
		this.isDecoy = isDecoy;
	}
	
}
