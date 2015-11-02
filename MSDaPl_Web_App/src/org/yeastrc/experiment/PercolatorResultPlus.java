/**
 * PercolatorResultWScan.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.utils.RoundingUtilsMSLIBRARY;

/**
 *
 */
public class PercolatorResultPlus implements PercolatorResult {

    private final PercolatorResult result;
    private PercolatorPeptideResult peptideResult;
    private SequestResultData sequestData;
    

	private ProlucidResultData prolucidData;
    


	private final int scanNumber;
    private final BigDecimal retentionTime;
    private double area = -1.0;
    private String filename;


    public PercolatorResultPlus(PercolatorResult result, MsScan scan) {
        this.result = result;
        this.scanNumber = scan.getStartScanNum();
        this.retentionTime = scan.getRetentionTime();
        if(scan instanceof MS2Scan) {
            MS2Scan scan2 = (MS2Scan) scan;
            area = scan2.getBullsEyeArea();
        }
    }



    /**
     * get qvalue rounded to 3 significant digits
     * @return
     */
	@Override
    public String getQvalueRounded3SignificantDigits() {

    	String qValue3sigDigits = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( result.getQvalue() );

    	return qValue3sigDigits;
    }

    public void setPeptideResult(PercolatorPeptideResult peptideResult) {
    	this.peptideResult = peptideResult;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

//    public SequestResultData getSequestData() {
//        return sequestData;
//    }

    public int getScanNumber() {
        return scanNumber;
    }

    public BigDecimal getRetentionTime() {
        return retentionTime;
    }

    public double getArea() {
        return area;
    }

    public double getPeptideQvalue() {
    	if(this.peptideResult != null)
    		return peptideResult.getQvalueRounded();
    	else
    		return -1.0;
    }

    public double getPeptidePosteriorErrorProbability() {
    	if(this.peptideResult != null)
    		return peptideResult.getPosteriorErrorProbabilityRounded();
    	else
    		return -1.0;
    }

    @Override
    public Double getDiscriminantScore() {
        return result.getDiscriminantScore();
    }

    @Override
    public Double getDiscriminantScoreRounded() {
        return result.getDiscriminantScoreRounded();
    }


	@Override
	public String getDiscriminantScoreRounded3SignificantDigits() {

		return result.getDiscriminantScoreRounded3SignificantDigits();
	}


    @Override
    public double getPosteriorErrorProbability() {
        return result.getPosteriorErrorProbability();
    }

    @Override
    public double getPosteriorErrorProbabilityRounded() {
        return result.getPosteriorErrorProbabilityRounded();
    }


	@Override
	public String getPosteriorErrorProbabilityRounded3SignificantDigits() {

		return result.getPosteriorErrorProbabilityRounded3SignificantDigits();
	}


    @Override
    public BigDecimal getPredictedRetentionTime() {
        return result.getPredictedRetentionTime();
    }

    @Override
    public double getQvalue() {
        return result.getQvalue();
    }

    @Override
    public double getQvalueRounded() {
        return result.getQvalueRounded();
    }

    @Override
	public Double getPvalue() {
		return result.getPvalue();
	}

	@Override
	public Double getPvalueRounded() {
		return result.getPvalueRounded();
	}


	@Override
	public String getPvalueRounded3SignificantDigits() {

		return result.getPvalueRounded3SignificantDigits();
	}


	@Override
	public int getPeptideResultId() {
		return result.getPeptideResultId();
	}

    @Override
    public int getRunSearchAnalysisId() {
        return result.getRunSearchAnalysisId();
    }

    @Override
    public int getId() {
        return result.getId();
    }

    @Override
    public int getPercolatorResultId() {
        return result.getPercolatorResultId();
    }

    @Override
    public List<MsSearchResultProtein> getProteinMatchList() {
        return result.getProteinMatchList();
    }

    public String getOtherProteinsShortHtml() {
        if(result.getProteinMatchList() == null)
            return null;
        else {
            StringBuilder buf = new StringBuilder();
            int i = 0;
            for(MsSearchResultProtein protein: result.getProteinMatchList()) {
                if(i == 0) {
                    i++;
                    continue;
                }
                buf.append("<br>"+makeShort(protein.getAccession()));
            }
            if(buf.length() > 0)
                buf.delete(0, "<br>".length());
            return buf.toString();
        }
    }

    public String getOneProteinShort() {
        if(result.getProteinMatchList() == null)
            return null;
        else {
        	return makeShort(result.getProteinMatchList().get(0).getAccession());
        }
    }

    private String makeShort(String string) {
    	if(string.length() > 23)
    		return string.substring(0, 20)+"...";
    	else
    		return string;
    }

    public int getProteinCount() {
        return result.getProteinMatchList().size();
    }

    @Override
    public int getRunSearchId() {
        return result.getRunSearchId();
    }

    @Override
    public int getScanId() {
        return result.getScanId();
    }

    @Override
    public int getCharge() {
        return result.getCharge();
    }

    @Override
    public BigDecimal getObservedMass() {
        return result.getObservedMass();
    }

    @Override
    public MsSearchResultPeptide getResultPeptide() {
        return result.getResultPeptide();
    }

    @Override
    public ValidationStatus getValidationStatus() {
        return result.getValidationStatus();
    }

//    public void setSequestData(SequestResultData sequestData) {
//        this.sequestData = sequestData;
//    }

    @Override
    public void setCharge(int charge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObservedMass(BigDecimal mass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setResultPeptide(MsSearchResultPeptide resultPeptide) {
        throw new UnsupportedOperationException();
    }




    public SequestResultData getSequestData() {
		return sequestData;
	}
	public void setSequestData(SequestResultData sequestData) {
		this.sequestData = sequestData;
	}
	public ProlucidResultData getProlucidData() {
		return prolucidData;
	}
	public void setProlucidData(ProlucidResultData prolucidData) {
		this.prolucidData = prolucidData;
	}



}
