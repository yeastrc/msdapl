/**
 * XtandemFilterResultsForm.java
 * @author Vagisha Sharma
 * Oct 27, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultFilterCriteria;

/**
 * 
 */
public class XtandemFilterResultsForm extends FilterResultsForm {

private int searchId;
    
    private String minHyperScore;
    private String minNextScore;
    private String minBscore;
    private String minYscore;
    private String minExpect;
    
    private String maxRank;
    
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors =  super.validate(mapping, request);
        
        if(maxRank != null && maxRank.trim().length() > 0) {
            try {Integer.parseInt(maxRank);}
            catch(NumberFormatException e) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Xtandem rank"));}
        }
        if(minHyperScore != null && minHyperScore.trim().length() > 0) {
            try{Double.parseDouble(minHyperScore);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min.  HyperScore"));}
        }
        
        if(minNextScore != null && minNextScore.trim().length() > 0) {
            try{Double.parseDouble(minNextScore);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. NextScore"));}
        }
        
        if(minBscore != null && minBscore.trim().length() > 0) {
            try{Double.parseDouble(minBscore);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. B-Score"));}
        }
        
        if(minYscore != null && minYscore.trim().length() > 0) {
            try{Double.parseDouble(minYscore);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Y-Score"));}
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
    
    public String getMinHyperScore() {
        return minHyperScore;
    }

    public Double getMinHyperScoreDouble() {
        if(minHyperScore != null && minHyperScore.trim().length() > 0)
            return Double.parseDouble(minHyperScore);
        return null;
    }
    
    public void setMinHyperScore(String minHyperScore) {
        this.minHyperScore = minHyperScore;
    }

    
    public String getMinNextScore() {
        return minNextScore;
    }

    public Double getMinNextScoreDouble() {
        if(minNextScore != null && minNextScore.trim().length() > 0)
            return Double.parseDouble(minNextScore);
        return null;
    }
    
    public void setMinNextScore(String minNextScore) {
        this.minNextScore = minNextScore;
    }

    
    public String getMinBscore() {
        return minBscore;
    }
    
    public Double getMinBscoreDouble() {
        if(minBscore != null && minBscore.trim().length() > 0)
            return Double.parseDouble(minBscore);
        return null;
    }

    public void setMinBscore(String minBscore) {
        this.minBscore = minBscore;
    }
    
    public String getMinYscore() {
        return minYscore;
    }
    
    public Double getMinYscoreDouble() {
        if(minYscore != null && minYscore.trim().length() > 0)
            return Double.parseDouble(minYscore);
        return null;
    }

    public void setMinYscore(String minYscore) {
        this.minYscore = minYscore;
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

    public XtandemResultFilterCriteria getFilterCriteria() {
        XtandemResultFilterCriteria criteria = new XtandemResultFilterCriteria();
        
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
        criteria.setMinHyperScore(getMinHyperScoreDouble());
        criteria.setMinNextScore(getMinNextScoreDouble());
        criteria.setMinBscore(getMinBscoreDouble());
        criteria.setMinYscore(getMinYscoreDouble());
        criteria.setMinExpectScore(getMinExpectDouble());
        
        criteria.setFileNames(filteredFileNames());
        
        return criteria;
    }
}
