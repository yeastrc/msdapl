/**
 * PeptideProphetResultDataBean.java
 * @author Vagisha Sharma
 * Jun 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;

/**
 * 
 */
public class PeptideProphetResultDataBean extends PeptideProphetResultData implements PeptideProphetResultDataWId {

    private int searchResultId;
    private int runSearchAnalysisId;
    @Override
    public int getSearchResultId() {
        return searchResultId;
    }

    public void setSearchResultId(int searchResultId) {
        this.searchResultId = searchResultId;
    }
    
    @Override
    public int getRunSearchAnalysisId() {
        return runSearchAnalysisId;
    }
    
    public void setRunSearchAnalysisId(int analysisId) {
        this.runSearchAnalysisId = analysisId;
    }
}
