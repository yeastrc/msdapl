package org.yeastrc.www.proteinfer.proteinProphet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.www.proteinfer.ProteinInferFilterForm;

public class ProteinProphetFilterForm extends ProteinInferFilterForm {

    
    private boolean joinProphetGroupProteins = true;
    private boolean excludeSubsumed = false;
    
    private String minGroupProbability = "0.0";
    private String maxGroupProbability = "1.0";
    
    private String minProteinProbability = "0.0";
    private String maxProteinProbability = "1.0";
    
    private String minPeptideProbability = "0.0";
    
    
	public ProteinProphetFilterForm () {}
    
    
    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        return errors;
    }

	// MIN PEPTIDE_PROPHET PEPTIDE PROBABILITY
    public String getMinPeptideProbability() {
		return minPeptideProbability;
	}

    public double getMinPeptideProbabilityDouble() {
        if(minPeptideProbability == null || minPeptideProbability.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minPeptideProbability);
    }
    
	public void setMinPeptideProbability(String minPeptideProbability) {
		this.minPeptideProbability = minPeptideProbability;
	}
	
    
    // MIN PROTEIN PROPHET GROUP PROBABILITY
    public String getMinGroupProbability() {
        return minGroupProbability;
    }

	public double getMinGroupProbabilityDouble() {
        if(minGroupProbability == null || minGroupProbability.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minGroupProbability);
    }
    public void setMinGroupProbability(String minProbability) {
        this.minGroupProbability = minProbability;
    }
    
    // MAX PROTEIN PROPHET GROUP PROBABILITY
    public String getMaxGroupProbability() {
        return maxGroupProbability;
    }
    public double getMaxGroupProbabilityDouble() {
        if(maxGroupProbability == null || maxGroupProbability.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxGroupProbability);
    }
    public void setMaxGroupProbability(String maxProbability) {
        this.maxGroupProbability = maxProbability;
    }
    
    // MIN PROTEIN PROPHET PROTEIN PROBABILITY
    public String getMinProteinProbability() {
        return minProteinProbability;
    }
    public double getMinProteinProbabilityDouble() {
        if(minProteinProbability == null || minProteinProbability.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minProteinProbability);
    }
    public void setMinProteinProbability(String minProbability) {
        this.minProteinProbability = minProbability;
    }
    
    // MAX PROTEIN PROPHET PROTEIN PROBABILITY
    public String getMaxProteinProbability() {
        return maxProteinProbability;
    }
    public double getMaxProteinProbabilityDouble() {
        if(maxProteinProbability == null || maxProteinProbability.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxProteinProbability);
    }
    public void setMaxProteinProbability(String maxProbability) {
        this.maxProteinProbability = maxProbability;
    }
    
   
    // PROTEIN GROUPS
    public boolean isJoinProphetGroupProteins() {
        return joinProphetGroupProteins;
    }

    public void setJoinProphetGroupProteins(boolean joinGroupProteins) {
        this.joinProphetGroupProteins = joinGroupProteins;
    }
    
    public boolean isExcludeSubsumed() {
        return excludeSubsumed;
    }

    public void setExcludeSubsumed(boolean excludeSubsumed) {
        this.excludeSubsumed = excludeSubsumed;
    }
    
    public ProteinProphetFilterCriteria getFilterCriteria(PeptideDefinition peptideDef) {
    	
    	ProteinFilterCriteria pfc = super.getFilterCriteria(peptideDef);
    	ProteinProphetFilterCriteria filterCriteria = new ProteinProphetFilterCriteria(pfc);
    	
    	filterCriteria.setMinPeptideProbability(getMinPeptideProbabilityDouble());
    	filterCriteria.setMinProteinProbability(getMinProteinProbabilityDouble());
    	filterCriteria.setMaxProteinProbability(getMaxProteinProbabilityDouble());
    	filterCriteria.setMinGroupProbability(getMinGroupProbabilityDouble());
    	filterCriteria.setMaxGroupProbability(getMaxGroupProbabilityDouble());
    	
    	filterCriteria.setGroupProteins(isJoinProphetGroupProteins());
    	
    	if(isExcludeSubsumed())
    		filterCriteria.setParsimoniousOnly();
    	
    	filterCriteria.setSortBy(SORT_BY.PROBABILITY_GRP);
    	filterCriteria.setSortOrder(SORT_ORDER.DESC);
        return filterCriteria;
    }
}
