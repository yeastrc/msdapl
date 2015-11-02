/**
 * IdPickerFilterForm.java
 * @author Vagisha Sharma
 * Mar 19, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.www.proteinfer.ProteinInferFilterForm;

/**
 * 
 */
public class IdPickerFilterForm extends ProteinInferFilterForm {

	private boolean joinGroupProteins = true;
	private boolean excludeSubsetProteins = false;
	private boolean excludeNonSubsetProteins = false;
	private boolean excludeParsimoniousProteins = false;
	private boolean excludeNonParsimoniousProteins = false;
    private boolean collapseGroups = false; // Used for downloads only
    private boolean printPeptides = false; // Used for downloads only
    private boolean printDescription = false; // used for downloads only
    
    
    public boolean isJoinGroupProteins() {
        return joinGroupProteins;
    }

	public void setJoinGroupProteins(boolean joinGroupProteins) {
        this.joinGroupProteins = joinGroupProteins;
    }
    
	public boolean isExcludeSubsetProteins() {
		return excludeSubsetProteins;
	}

	public void setExcludeSubsetProteins(boolean excludeSubsetProteins) {
		this.excludeSubsetProteins = excludeSubsetProteins;
	}

	public boolean isExcludeNonSubsetProteins() {
		return excludeNonSubsetProteins;
	}

	public void setExcludeNonSubsetProteins(boolean excludeNonSubsetProteins) {
		this.excludeNonSubsetProteins = excludeNonSubsetProteins;
	}

	public boolean isExcludeParsimoniousProteins() {
		return excludeParsimoniousProteins;
	}

	public void setExcludeParsimoniousProteins(boolean excludeParsimoniousProteins) {
		this.excludeParsimoniousProteins = excludeParsimoniousProteins;
	}

	public boolean isExcludeNonParsimoniousProteins() {
		return excludeNonParsimoniousProteins;
	}

	public void setExcludeNonParsimoniousProteins(
			boolean excludeNonParsimoniousProteins) {
		this.excludeNonParsimoniousProteins = excludeNonParsimoniousProteins;
	}

	public boolean isCollapseGroups() {
        return collapseGroups;
    }

    public void setCollapseGroups(boolean collapseGroups) {
        this.collapseGroups = collapseGroups;
    }

    public boolean isPrintPeptides() {
        return printPeptides;
    }

    public void setPrintPeptides(boolean printPeptides) {
        this.printPeptides = printPeptides;
    }
    
    public boolean isPrintDescriptions() {
        return printDescription;
    }

    public void setPrintDescriptions(boolean printDescription) {
        this.printDescription = printDescription;
    }
    
    public ProteinFilterCriteria getFilterCriteria(PeptideDefinition peptideDef) {
    	
    	ProteinFilterCriteria filterCriteria = super.getFilterCriteria(peptideDef);
    	
    	filterCriteria.setGroupProteins(isJoinGroupProteins());
    	filterCriteria.setParsimonious(!excludeParsimoniousProteins);
    	filterCriteria.setNonParsimonious(!excludeNonParsimoniousProteins);
    	filterCriteria.setNonSubset(!excludeNonSubsetProteins);
    	filterCriteria.setSubset(!excludeSubsetProteins);
    		
        if(isCollapseGroups()) 
            filterCriteria.setSortBy(SORT_BY.GROUP_ID);
        else
            filterCriteria.setSortBy(ProteinFilterCriteria.defaultSortBy());
        
       
        
        return filterCriteria;
    }
}
