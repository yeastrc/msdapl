/**
 * SequestFilterResultsForm.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.ms.domain.search.sequest.SequestResultFilterCriteria;

/**
 * 
 */
public class SequestFilterResultsForm extends FilterResultsForm {

    private int searchId;
    
    private String minXCorr_1;
    private String minXCorr_2;
    private String minXCorr_3;
    private String minXCorr_H;
    
    private String minDeltaCN;
    
    private String minSp;
    
    private String xcorrRank;
    
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors =  super.validate(mapping, request);
        
        if(xcorrRank != null && xcorrRank.trim().length() > 0) {
            try {Integer.parseInt(xcorrRank);}
            catch(NumberFormatException e) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for xCorrRank"));}
        }
        if(minXCorr_1 != null && minXCorr_1.trim().length() > 0) {
            try{Double.parseDouble(minXCorr_1);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. XCorr (charge 1)"));}
        }
        
        if(minXCorr_2 != null && minXCorr_2.trim().length() > 0) {
            try{Double.parseDouble(minXCorr_2);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. XCorr (charge 2)"));}
        }
        
        if(minXCorr_3 != null && minXCorr_3.trim().length() > 0) {
            try{Double.parseDouble(minXCorr_3);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. XCorr (charge 3)"));}
        }
        
        if(minXCorr_H != null && minXCorr_H.trim().length() > 0) {
            try{Double.parseDouble(minXCorr_H);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. XCorr (charge > 3)"));}
        }
        
        if(minDeltaCN != null && minDeltaCN.trim().length() > 0) {
            try{Double.parseDouble(minDeltaCN);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. DeltaCN"));}
        }
        
        if(minSp != null && minSp.trim().length() > 0) {
            try{Double.parseDouble(minSp);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Sp"));}
        }
        
        
        return errors;
    }
    
    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public String getXcorrRank() {
        return xcorrRank;
    }

    public Integer getXcorrRank_Integer() {
        if(xcorrRank != null && xcorrRank.trim().length() > 0)
            return Integer.parseInt(xcorrRank);
        return null;
    }
    
    public void setXcorrRank(String xcorrRank) {
        this.xcorrRank = xcorrRank;
    }
    
    public String getMinXCorr_1() {
        return minXCorr_1;
    }

    public Double getMinXCorr_1Double() {
        if(minXCorr_1 != null && minXCorr_1.trim().length() > 0)
            return Double.parseDouble(minXCorr_1);
        return null;
    }
    
    public void setMinXCorr_1(String minXCorr_1) {
        this.minXCorr_1 = minXCorr_1;
    }

    
    public String getMinXCorr_2() {
        return minXCorr_2;
    }

    public Double getMinXCorr_2Double() {
        if(minXCorr_2 != null && minXCorr_2.trim().length() > 0)
            return Double.parseDouble(minXCorr_2);
        return null;
    }
    
    public void setMinXCorr_2(String minXCorr_2) {
        this.minXCorr_2 = minXCorr_2;
    }

    
    public String getMinXCorr_3() {
        return minXCorr_3;
    }
    
    public Double getMinXCorr_3Double() {
        if(minXCorr_3 != null && minXCorr_3.trim().length() > 0)
            return Double.parseDouble(minXCorr_3);
        return null;
    }

    public void setMinXCorr_3(String minXCorr_3) {
        this.minXCorr_3 = minXCorr_3;
    }

    
    public String getMinXCorr_H() {
        return minXCorr_H;
    }

    public Double getMinXCorr_HDouble() {
        if(minXCorr_H != null && minXCorr_H.trim().length() > 0)
            return Double.parseDouble(minXCorr_H);
        return null;
    }
    
    public void setMinXCorr_H(String minXCorr_H) {
        this.minXCorr_H = minXCorr_H;
    }

    
    
    public String getMinDeltaCN() {
        return minDeltaCN;
    }

    public Double getMinDeltaCNDouble() {
        if(minDeltaCN != null && minDeltaCN.trim().length() > 0)
            return Double.parseDouble(minDeltaCN);
        return null;
    }
    
    public void setMinDeltaCN(String minDeltaCN) {
        this.minDeltaCN = minDeltaCN;
    }
    
    
    public String getMinSp() {
        return minSp;
    }

    public Double getMinSpDouble() {
        if(minSp != null && minSp.trim().length() > 0)
            return Double.parseDouble(minSp);
        return null;
    }
    
    public void setMinSp(String minSp) {
        this.minSp = minSp;
    }



    public SequestResultFilterCriteria getFilterCriteria() {
        SequestResultFilterCriteria criteria = new SequestResultFilterCriteria();
        
        criteria.setMinScan(getMinScanInt());
        criteria.setMaxScan(getMaxScanInt());
        
        criteria.setMinCharge(getMinChargeInt());
        criteria.setMaxCharge(getMaxChargeInt());
        
        criteria.setMinObservedMass(getMinObsMassDouble());
        criteria.setMaxObservedMass(getMaxObsMassDouble());
        
        criteria.setMinRetentionTime(getMinRTDouble());
        criteria.setMaxRetentionTime(getMaxRTDouble());
        
        criteria.setPeptide(getPeptide());
        criteria.setExactPeptideMatch(getExactPeptideMatch());
        
        criteria.setShowOnlyModified(isShowModified() && !isShowUnmodified());
        criteria.setShowOnlyUnmodified(isShowUnmodified() && !isShowModified());
        
        criteria.setMaxXcorrRank(getXcorrRank_Integer());
        criteria.setMinXCorr_1(getMinXCorr_1Double());
        criteria.setMinXCorr_2(getMinXCorr_2Double());
        criteria.setMinXCorr_3(getMinXCorr_3Double());
        criteria.setMinXCorr_H(getMinXCorr_HDouble());
        
        criteria.setMinDeltaCn(getMinDeltaCNDouble());
        criteria.setMinSp(getMinSpDouble());
        
        criteria.setFileNames(filteredFileNames());
        
        return criteria;
    }

}
