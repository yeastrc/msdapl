/**
 * ProteinProphetRoc.java
 * @author Vagisha Sharma
 * Jul 31, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ProteinProphetROC implements Serializable {

    private int proteinferId;
    private List<ProteinProphetROCPoint> rocPoints;
    
    public ProteinProphetROC() {
        rocPoints = new ArrayList<ProteinProphetROCPoint>();
    }
    
    public int getProteinferId() {
        return proteinferId;
    }

    public void setProteinferId(int proteinferId) {
        this.proteinferId = proteinferId;
        if(rocPoints != null) {
        	for(ProteinProphetROCPoint point: rocPoints)
        		point.setProteinferId(proteinferId);
        }
    }

    public List<ProteinProphetROCPoint> getRocPoints() {
        return rocPoints;
    }
    
    public void addRocPoint(ProteinProphetROCPoint point) {
        this.rocPoints.add(point);
        point.setProteinferId(this.proteinferId);
    }
    
    public void setRocPoints(List<ProteinProphetROCPoint> rocPoints) {
        this.rocPoints = rocPoints;
        for(ProteinProphetROCPoint point: this.rocPoints)
        	point.setProteinferId(this.proteinferId);
    }

    public double getMinProbabilityForError(double error) {
        double closestError = Double.MIN_VALUE;
        double probability = -1.0;
        
        for(ProteinProphetROCPoint point: rocPoints) {
            if(probability == -1.0) {
                closestError = point.getFalsePositiveErrorRate();
                probability = point.getMinProbability();
                continue;
            }
            double diff = Math.abs(error - point.getFalsePositiveErrorRate());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getFalsePositiveErrorRate();
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
    	
        double closestError = rocPoints.get(0).getFalsePositiveErrorRate();
        
        for(ProteinProphetROCPoint point: rocPoints) {
            double diff = Math.abs(error - point.getFalsePositiveErrorRate());
            double oldDiff = Math.abs(error - closestError);
            if(diff < oldDiff) {
                closestError = point.getFalsePositiveErrorRate();
            }
        }
        return closestError;
    }
}
