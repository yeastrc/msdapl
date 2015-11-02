/**
 * MsSearchResultWrap.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.ValidationStatus;

/**
 * NOTE: This class is used internally by MsSearchResultDAOImpl.
 */
public class MsSearchResultWrap {

    private int runSearchId;
    private int scanId;
    private MsSearchResultIn result;
    
    public MsSearchResultWrap(MsSearchResultIn result, int runSearchId, int scanId) {
        this.runSearchId = runSearchId;
        this.scanId = scanId;
        this.result = result;
    }
    public int getRunSearchId() {
        return runSearchId;
    }
    public int getScanId() {
        return scanId;
    }
    public int getCharge() {
        return result.getCharge();
    }
    public BigDecimal getObservedMass() {
        return result.getObservedMass();
    }
    public ValidationStatus getValidationStatus() {
        return result.getValidationStatus();
    }
    public String getPeptideSequence() {
        return result.getResultPeptide().getPeptideSequence();
    }
    public String getPreResidueString() {
        return Character.toString(result.getResultPeptide().getPreResidue());
    }
    public String getPostResidueString() {
        return Character.toString(result.getResultPeptide().getPostResidue());
    }
    public int getSequenceLength() {
        return result.getResultPeptide().getSequenceLength();
    }
}
