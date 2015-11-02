/**
 * AnalysisFilterResultsForm.java
 * @author Vagisha Sharma
 * Aug 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

/**
 * 
 */
public class AnalysisFilterResultsForm extends FilterResultsForm {

    private int searchAnalysisId;
    
    private boolean peptidesView = false;
    
    public int getSearchAnalysisId() {
        return searchAnalysisId;
    }

    public void setSearchAnalysisId(int searchAnalysisId) {
        this.searchAnalysisId = searchAnalysisId;
    }
    
    public boolean isPeptidesView() {
        return peptidesView;
    }

    public void setPeptidesView(boolean peptidesView) {
        this.peptidesView = peptidesView;
    }
}
