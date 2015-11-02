/**
 * PeptideProphet_ROC.java
 * @author Vagisha Sharma
 * Jul 23, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class PeptideProphetROC {

    private int searchAnalysisId;
    private List<PeptideProphetROCPoint> rocPoints;
    
    public PeptideProphetROC() {
        rocPoints = new ArrayList<PeptideProphetROCPoint>();
    }
    
    public int getSearchAnalysisId() {
        return searchAnalysisId;
    }

    public void setSearchAnalysisId(int searchAnalysisId) {
        this.searchAnalysisId = searchAnalysisId;
        for(PeptideProphetROCPoint point: rocPoints) {
            point.setSearchAnalysisId(searchAnalysisId);
        }
    }

    public List<PeptideProphetROCPoint> getRocPoints() {
        return rocPoints;
    }
    
    public void addRocPoint(PeptideProphetROCPoint point) {
        this.rocPoints.add(point);
        point.setSearchAnalysisId(this.searchAnalysisId);
    }
    
    public void setRocPoints(List<PeptideProphetROCPoint> rocPoints) {
        this.rocPoints = rocPoints;
        for(PeptideProphetROCPoint point: rocPoints)
            point.setSearchAnalysisId(searchAnalysisId);
    }

    public double getMinProbabilityForError(double error) {
        double closestError = Double.MIN_VALUE;
        double probability = -1.0;
        
        for(PeptideProphetROCPoint point: rocPoints) {
            if(probability == -1.0) {
                closestError = point.getError();
                probability = point.getMinProbability();
                continue;
            }
            double diff = Math.abs(error - point.getError());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getError();
                probability = point.getMinProbability();
            }
        }
        if(probability == -1.0)
        	return 0.0;
        
        return probability;
    }
    
    public double getClosestError(double error) {
        
    	if(rocPoints == null || rocPoints.size() == 0)
    	{
    		return 1.0;
    	}
    	
        double closestError = rocPoints.get(0).getError();
        
        for(PeptideProphetROCPoint point: rocPoints) {
            double diff = Math.abs(error - point.getError());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getError();
            }
        }
        return closestError;
    }
}


