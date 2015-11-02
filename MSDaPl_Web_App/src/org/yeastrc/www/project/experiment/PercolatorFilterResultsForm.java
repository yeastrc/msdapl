/**
 * PercolatorFilterResultsForm.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;

/**
 * 
 */
public class PercolatorFilterResultsForm extends AnalysisFilterResultsForm {

    
    private String minQValue = null;
    private String maxQValue = null;
    
    private String minPep = null;
    private String maxPep = null;
    
    private String minDs = null;
    private String maxDs = null;
    
    private boolean usePEP = true;
    
    private boolean peptideResults = false;

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        if(minQValue != null && minQValue.trim().length() > 0) {
            try{Double.parseDouble(minQValue);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. q-value"));}
        }
        if(maxQValue != null && maxQValue.trim().length() > 0) {
            try{Double.parseDouble(maxQValue);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. q-value"));}
        }
        
        if(minPep != null && minPep.trim().length() > 0) {
            try{Double.parseDouble(minPep);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. PEP"));}
        }
        if(minPep != null && minPep.trim().length() > 0) {
            try{Double.parseDouble(minPep);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. PEP"));}
        }
        
        if(minDs != null && minDs.trim().length() > 0) {
            try{Double.parseDouble(minDs);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Discriminant Score"));}
        }
        if(maxDs != null && maxDs.trim().length() > 0) {
            try{Double.parseDouble(maxDs);}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. Discriminant Score"));}
        }
        
        return errors;
    }
    
    public boolean isUsePEP() {
        return usePEP;
    }

    public void setUsePEP(boolean usePEP) {
        this.usePEP = usePEP;
    }
    
    public String getMinQValue() {
        return minQValue;
    }
    public Double getMinQValueDouble() {
        if(minQValue != null && minQValue.trim().length() > 0)
            return Double.parseDouble(minQValue);
        return null;
    }
    public void setMinQValue(String minQValue) {
        this.minQValue = minQValue;
    }
    
    
    public String getMaxQValue() {
        return maxQValue;
    }
    public Double getMaxQValueDouble() {
        if(maxQValue != null && maxQValue.trim().length() > 0)
            return Double.parseDouble(maxQValue);
        return null;
    }
    public void setMaxQValue(String maxQValue) {
        this.maxQValue = maxQValue;
    }
    
    
    public String getMinPep() {
        return minPep;
    }
    public Double getMinPepDouble() {
        if(minPep != null && minPep.trim().length() > 0)
            return Double.parseDouble(minPep);
        return null;
    }
    public void setMinPep(String minPep) {
        this.minPep = minPep;
    }
    
    
    public String getMaxPep() {
        return maxPep;
    }
    public Double getMaxPepDouble() {
        if(maxPep != null && maxPep.trim().length() > 0)
            return Double.parseDouble(maxPep);
        return null;
    }
    public void setMaxPep(String maxPep) {
        this.maxPep = maxPep;
    }
    
    
    public String getMinDs() {
        return minDs;
    }
    public Double getMinDsDouble() {
        if(minDs != null && minDs.trim().length() > 0)
            return Double.parseDouble(minDs);
        return null;
    }
    public void setMinDs(String minDs) {
        this.minDs = minDs;
    }

    
    public String getMaxDs() {
        return maxDs;
    }
    public Double getMaxDsDouble() {
        if(maxDs != null && maxDs.trim().length() > 0)
            return Double.parseDouble(maxDs);
        return null;
    }
    public void setMaxDs(String maxDs) {
        this.maxDs = maxDs;
    }
    
    public boolean isPeptideResults() {
		return peptideResults;
	}

	public void setPeptideResults(boolean peptideResults) {
		this.peptideResults = peptideResults;
	}
	

	public PercolatorResultFilterCriteria getFilterCriteria() {
        PercolatorResultFilterCriteria criteria = new PercolatorResultFilterCriteria();
        
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
        
        List<SelectableModificationBean> modList = this.getModificationList();
        if(modList != null && modList.size() > 0) {
        	List<Integer> modIdFilters = new ArrayList<Integer>();
        	boolean allSelected = true;
        	for(SelectableModificationBean modBean: modList) {
        		if(modBean.isSelected())
        			modIdFilters.add(modBean.getId());
        		else
        			allSelected = false;
        	}
        	if(modIdFilters.size() > 0 && !allSelected)
        		criteria.setModificationIdFilters(modIdFilters);
        }
        
        
        criteria.setMinQValue(getMinQValueDouble());
        criteria.setMaxQValue(getMaxQValueDouble());
        
        criteria.setMinPep(getMinPepDouble());
        criteria.setMaxPep(getMaxPepDouble());
        
        criteria.setMinDs(getMinDsDouble());
        criteria.setMaxDs(getMaxDsDouble());
        
        criteria.setFileNames(filteredFileNames());
        
        return criteria;
    }

}
