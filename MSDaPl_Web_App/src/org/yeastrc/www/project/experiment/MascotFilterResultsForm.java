package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.ms.domain.search.mascot.MascotResultFilterCriteria;

public class MascotFilterResultsForm extends FilterResultsForm {

    private int searchId;
    
    private String minIonScore;
    private String minIdentityScore;
    private String minHomologyScore;
    private String minExpect;
    
    private String maxRank;
    
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors =  super.validate(mapping, request);
        
        if(maxRank != null && maxRank.trim().length() > 0) {
            try {Integer.parseInt(maxRank);}
            catch(NumberFormatException e) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Mascot rank"));}
        }
        if(minIonScore != null && minIonScore.trim().length() > 0) {
            try{Double.parseDouble(minIonScore);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Ion Score"));}
        }
        
        if(minIdentityScore != null && minIdentityScore.trim().length() > 0) {
            try{Double.parseDouble(minIdentityScore);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Identity Score"));}
        }
        
        if(minHomologyScore != null && minHomologyScore.trim().length() > 0) {
            try{Double.parseDouble(minHomologyScore);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Homology Score"));}
        }
        
        if(minExpect != null && minExpect.trim().length() > 0) {
            try{Double.parseDouble(minExpect);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Expect"));}
        }
        
        
        return errors;
    }
    
    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public String getMaxRank() {
        return maxRank;
    }

    public Integer getMaxRank_Integer() {
        if(maxRank != null && maxRank.trim().length() > 0)
            return Integer.parseInt(maxRank);
        return null;
    }
    
    public void setMaxRank(String rank) {
        this.maxRank = rank;
    }
    
    public String getMinIonScore() {
        return minIonScore;
    }

    public Double getMinIonScoreDouble() {
        if(minIonScore != null && minIonScore.trim().length() > 0)
            return Double.parseDouble(minIonScore);
        return null;
    }
    
    public void setMinIonScore(String minIonScore) {
        this.minIonScore = minIonScore;
    }

    
    public String getMinIdentityScore() {
        return minIdentityScore;
    }

    public Double getMinIdentityScoreDouble() {
        if(minIdentityScore != null && minIdentityScore.trim().length() > 0)
            return Double.parseDouble(minIdentityScore);
        return null;
    }
    
    public void setMinIdentityScore(String minIdentityScore) {
        this.minIdentityScore = minIdentityScore;
    }

    
    public String getMinHomologyScore() {
        return minHomologyScore;
    }
    
    public Double getMinHomologyScoreDouble() {
        if(minHomologyScore != null && minHomologyScore.trim().length() > 0)
            return Double.parseDouble(minHomologyScore);
        return null;
    }

    public void setMinHomologyScore(String minHomologyScore) {
        this.minHomologyScore = minHomologyScore;
    }

    
    public String getMinExpect() {
        return minExpect;
    }

    public Double getMinExpectDouble() {
        if(minExpect != null && minExpect.trim().length() > 0)
            return Double.parseDouble(minExpect);
        return null;
    }
    
    public void setMinExpect(String minExpect) {
        this.minExpect = minExpect;
    }

    public MascotResultFilterCriteria getFilterCriteria() {
        MascotResultFilterCriteria criteria = new MascotResultFilterCriteria();
        
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
        
        criteria.setRank(getMaxRank_Integer());
        criteria.setMinIonScore(getMinIonScoreDouble());
        criteria.setMinIdentityScore(getMinIdentityScoreDouble());
        criteria.setMinHomologyScore(getMinHomologyScoreDouble());
        criteria.setMinExpectScore(getMinExpectDouble());
        
        criteria.setFileNames(filteredFileNames());
        
        return criteria;
    }
}
