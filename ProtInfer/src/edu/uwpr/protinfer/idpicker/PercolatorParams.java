/**
 * PercolatorParams.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam;

/**
 * 
 */
public class PercolatorParams {
    
    private double psmQvalue;
    private double peptideQvalue;
    private boolean hasPsmQvalueCutoff = false;
    private boolean hasPeptideQvalueCutoff = false;
    
    private double psmPep;
    private double peptidePep;
    private boolean hasPsmPepCutoff = false;
    private boolean hasPeptidePepCutoff = false;
    
    private double psmDiscriminantScore;
    private double peptideDiscriminantScore;
    private boolean hasPsmDiscriminantScoreCutoff = false;
    private boolean hasPeptideDiscriminantScoreCutoff = false;
    
    
    private IDPickerParams idpParams = null;
    
    public PercolatorParams(IDPickerParams params) {
        
        this.idpParams = params;
        List<IdPickerParam> moreFilters = params.getMoreFilters();
        for(IdPickerParam filter: moreFilters) {
            if(filter.getName().equalsIgnoreCase("qval_percolator")) {
                psmQvalue = Double.parseDouble(filter.getValue());
                hasPsmQvalueCutoff = true;
            }
            if(filter.getName().equalsIgnoreCase("peptide_qval_percolator")) {
                peptideQvalue = Double.parseDouble(filter.getValue());
                if(peptideQvalue < 1.0)
                	hasPeptideQvalueCutoff = true;
            }
            else if(filter.getName().equalsIgnoreCase("pep_percolator")) {
                psmPep = Double.parseDouble(filter.getValue());
                hasPsmPepCutoff = true;
            }
            else if(filter.getName().equalsIgnoreCase("peptide_pep_percolator")) {
                peptidePep = Double.parseDouble(filter.getValue());
                if(peptidePep < 1.0)
                	hasPeptidePepCutoff = true;
            }
            else if(filter.getName().equalsIgnoreCase("discriminantScore_percolator")) {
                psmDiscriminantScore = Double.parseDouble(filter.getValue());
                hasPsmDiscriminantScoreCutoff = true;
            }
            else if(filter.getName().equalsIgnoreCase("peptide_discriminantScore_percolator")) {
                peptideDiscriminantScore = Double.parseDouble(filter.getValue());
                hasPeptideDiscriminantScoreCutoff = true;
            }
        }
    }
    
    public double getPsmQvalueCutoff() {
        return psmQvalue;
    }
    
    public double getPeptideQvalueCutoff() {
    	return peptideQvalue;
    }
    
    public double getPsmPEPCutoff() {
        return psmPep;
    }
    
    public double getPeptidePEPCutoff() {
        return peptidePep;
    }
    
    public double getPsmDiscriminantScoreCutoff() {
        return psmDiscriminantScore;
    }
    
    public double getPeptideDiscriminantScoreCutoff() {
        return peptideDiscriminantScore;
    }
    
    public IDPickerParams getIdPickerParams() {
        return idpParams;
    }

    public boolean hasPsmQvalueCutoff() {
        return hasPsmQvalueCutoff;
    }
    
    public boolean hasPeptideQvalueCutoff() {
        return hasPeptideQvalueCutoff;
    }

    public boolean hasPsmPepCutoff() {
        return hasPsmPepCutoff;
    }
    
    public boolean hasPeptidePepCutoff() {
        return hasPeptidePepCutoff;
    }

    public boolean hasPsmDiscriminantScoreCutoff() {
        return hasPsmDiscriminantScoreCutoff;
    }
    
    public boolean hasPeptideDiscriminantScoreCutoff() {
        return hasPeptideDiscriminantScoreCutoff;
    }

	public boolean isUsePeptideLevelScores() {
		return this.hasPeptideQvalueCutoff || hasPeptidePepCutoff || hasPeptideDiscriminantScoreCutoff;
	}
	
	public boolean isUsePsmLevelScores() {
		return this.hasPsmQvalueCutoff || hasPsmPepCutoff || hasPsmDiscriminantScoreCutoff;
	}
}
