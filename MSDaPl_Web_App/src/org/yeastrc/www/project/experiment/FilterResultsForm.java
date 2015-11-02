/**
 * FilterResultsForm.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;


/**
 * 
 */
public class FilterResultsForm extends ActionForm {

    
    private String minScan;
    private String maxScan;
    
    private String minCharge;
    private String maxCharge;
    
    private String minRT;
    private String maxRT;
    
    private String minObsMass;
    private String maxObsMass;
    
    private String peptide = null;
    private boolean exactMatch = true;
    
    private String fileNameFilter = null;
    
    private boolean showModified = true;
    private boolean showUnmodified = true;
    private List<SelectableModificationBean> modificationList;
    
    private SORT_BY sortBy;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;
    
    private int pageNum = 1;
    private int numPerPage = 50;
    
    private boolean doDownload = false;
    

	public boolean isDoDownload() {
		return doDownload;
	}

	public void setDoDownload(boolean doDownload) {
		this.doDownload = doDownload;
	}

	public int getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

	public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        if(minScan != null && minScan.trim().length() > 0) {
            try{Integer.parseInt(minScan.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Scan"));}
        }
        if(maxScan != null && maxScan.trim().length() > 0) {
            try{Integer.parseInt(maxScan.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. Scan"));}
        }
        
        if(minCharge != null && minCharge.trim().length() > 0) {
            try{Integer.parseInt(minCharge.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Charge"));}
        }
        if(maxCharge != null && maxCharge.trim().length() > 0) {
            try{Integer.parseInt(maxCharge.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. Charge"));}
        }
        
        if(minRT != null && minRT.trim().length() > 0) {
            try{Double.parseDouble(minRT.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. RT"));}
        }
        if(maxRT != null && maxRT.trim().length() > 0) {
            try{Double.parseDouble(maxRT.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. RT"));}
        }
        
        if(minObsMass != null && minObsMass.trim().length() > 0) {
            try{Double.parseDouble(minObsMass.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Min. Observed Mass"));}
        }
        if(maxObsMass != null && maxObsMass.trim().length() > 0) {
            try{Double.parseDouble(maxObsMass.trim());}
            catch(NumberFormatException e){
                errors.add(ActionMessages.GLOBAL_MESSAGE, 
                        new ActionMessage("error.general.errorMessage", "Invalid value for Max. Observed Mass"));}
        }
        
        return errors;
    }
    
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        // These need to be set to false because if a checkbox is not checked the browser does not
        // send its value in the request.
        // http://struts.apache.org/1.1/faqs/newbie.html#checkboxes
        showModified = false;
        showUnmodified = false;
        exactMatch = false;
    }
    
    public SORT_BY getSortBy() {
        return this.sortBy;
    }
    public String getSortByString() {
        if(sortBy == null)  return null;
        return this.sortBy.name();
    }
    
    public void setSortBy(SORT_BY sortBy) {
        this.sortBy = sortBy;
    }
    public void setSortByString(String sortBy) {
        this.sortBy = SORT_BY.getSortByForName(sortBy);
    }
    
    
    public SORT_ORDER getSortOrder() {
        return this.sortOrder;
    }
    public String getSortOrderString() {
        if(sortOrder == null)   return null;
        return this.sortOrder.name();
    }
    
    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }
    public void setSortOrderString(String sortOrder) {
        this.sortOrder = SORT_ORDER.getSortByForName(sortOrder);
    }
    
    public String getMinScan() {
        return minScan;
    }
    
    public Integer getMinScanInt() {
        if(minScan != null && minScan.trim().length() > 0)
            return Integer.parseInt(minScan);
        return null;
    }
    
    public String getMaxScan() {
        return maxScan;
    }
   
    public Integer getMaxScanInt() {
        if(maxScan != null && maxScan.trim().length() > 0)
            return Integer.parseInt(maxScan);
        return null;
    }
    
    public String getMinCharge() {
        return minCharge;
    }
    public Integer getMinChargeInt() {
        if(minCharge != null && minCharge.trim().length() > 0)
            return Integer.parseInt(minCharge);
        return null;
    }
    
    
    public String getMaxCharge() {
        return maxCharge;
    }
    public Integer getMaxChargeInt() {
        if(maxCharge != null && maxCharge.trim().length() > 0)
            return Integer.parseInt(maxCharge);
        return null;
    }
    
    
    public String getMinRT() {
        return minRT;
    }
    public Double getMinRTDouble() {
        if(minRT != null && minRT.trim().length() > 0)
            return Double.parseDouble(minRT);
        return null;
    }
    
    
    public String getMaxRT() {
        return maxRT;
    }
    public Double getMaxRTDouble() {
        if(maxRT != null && maxRT.trim().length() > 0)
            return Double.parseDouble(maxRT);
        return null;
    }
    
    
    public String getMinObsMass() {
        return minObsMass;
    }
    public Double getMinObsMassDouble() {
        if(minObsMass != null && minObsMass.trim().length() > 0)
            return Double.parseDouble(minObsMass);
        return null;
    }
    
    
    public String getMaxObsMass() {
        return maxObsMass;
    }
    public Double getMaxObsMassDouble() {
        if(maxObsMass != null && maxObsMass.trim().length() > 0)
            return Double.parseDouble(maxObsMass);
        return null;
    }
    
    
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        if(peptide != null && peptide.trim().length() == 0)
            this.peptide = null;
        else
            this.peptide = peptide;
    }
    
    public boolean getExactPeptideMatch() {
        return exactMatch;
    }
    public void setExactPeptideMatch(boolean exact) {
        this.exactMatch = exact;
    }
    
    public boolean isShowModified() {
        return showModified;
    }
    public void setShowModified(boolean showModified) {
        this.showModified = showModified;
    }
    public boolean isShowUnmodified() {
        return showUnmodified;
    }
    public void setShowUnmodified(boolean showUnmodified) {
        this.showUnmodified = showUnmodified;
    }
    public void setModificationList(List<SelectableModificationBean> modificationList) {
    	this.modificationList = modificationList;
    }
    public List<SelectableModificationBean> getModificationList() {
    	return this.modificationList;
    }
    public SelectableModificationBean getModification(int index) {
    	if(modificationList == null)
    		modificationList = new ArrayList<SelectableModificationBean>();
    	while(index >= modificationList.size()) {
    		modificationList.add(new SelectableModificationBean());
    	}
    	return modificationList.get(index);
    }
    
    public ResultSortCriteria getSortCriteria() {
        ResultSortCriteria criteria = new ResultSortCriteria(sortBy, sortOrder);
        return criteria;
    }

    public void setMinScan(String minScan) {
        this.minScan = minScan;
    }

    public void setMaxScan(String maxScan) {
        this.maxScan = maxScan;
    }

    public void setMinCharge(String minCharge) {
        this.minCharge = minCharge;
    }
    
    public void setMaxCharge(String maxCharge) {
        this.maxCharge = maxCharge;
    }

    public void setMinRT(String minRT) {
        this.minRT = minRT;
    }

    public void setMaxRT(String maxRT) {
        this.maxRT = maxRT;
    }

    public void setMinObsMass(String minObsMass) {
        this.minObsMass = minObsMass;
    }

    public void setMaxObsMass(String maxObsMass) {
        this.maxObsMass = maxObsMass;
    }

    public String getFileNameFilter() {
        return fileNameFilter;
    }

    public void setFileNameFilter(String fileNameFilter) {
        if(fileNameFilter != null)
            this.fileNameFilter = fileNameFilter.trim();
    }
    
    public String[] filteredFileNames() {
        if(fileNameFilter == null || fileNameFilter.length() == 0)
            return new String[0];
        String[] filenames = fileNameFilter.split(",");
        for(int i = 0; i < filenames.length; i++) {
            filenames[i] = filenames[i].trim();
        }
        return filenames;
    }
    
    public boolean isExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}
}
