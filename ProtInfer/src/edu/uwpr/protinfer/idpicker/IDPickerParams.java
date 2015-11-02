package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam;

import edu.uwpr.protinfer.ProgramParam.SCORE;

public class IDPickerParams {

    private float maxFdr = 0.05f;
    private SCORE scoreForFDR;
//    private float maxRelativeFdr = 0.05f;
//    private float decoyRatio = 1.0f;
//    private boolean doParsimonyAnalysis = true;
    private boolean doFdrCalculation = true;
    private String decoyPrefix = "";
    private boolean useIdPickerFDRFormula = true;
    
    
    private PeptideDefinition peptideDefinition;
    private int minPeptides = 1;
    private int minUniquePeptides = 0;
    private float minCoverage = 0;
//    private int minPeptideSpectra = 1;
    private int minPeptideLength = 1;
    private boolean removeAmbiguousSpectra = true;
    private boolean refreshPeptideProteinMatches = false;
    private boolean doItoLSubstitution = false;
    private boolean removeAsterisks = false;
    
    // If true, NSAF will be calculated for both parsimonious and non-parsimonious proteins
    private boolean calculateAllNsaf = false;  
    
    List<IdPickerParam> moreFilters = new ArrayList<IdPickerParam>();
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if(doFdrCalculation) {
            buf.append("Max FDR: "+maxFdr+"\n");
//            buf.append("Max rel. FDR: "+maxRelativeFdr+"\n");
//            buf.append("Decoy Ratio: "+decoyRatio+"\n");
            buf.append("Decoy Prefix: "+decoyPrefix+"\n");
            buf.append("Use IDPicker FDR formula: "+useIdPickerFDRFormula+"\n");
        }
        buf.append("Peptide Definition: "+peptideDefinition.toString()+"\n");
        buf.append("Min Peptides: "+minPeptides+"\n");
        buf.append("Min Unique Peptides: "+minUniquePeptides+"\n");
        buf.append("Min Coverage: "+minCoverage+"\n");
        buf.append("Min Peptide Length: "+minPeptideLength+"\n");
//        buf.append("Min Spectra / Peptide: "+minPeptideSpectra+"\n");
        buf.append("Remove Ambiguous Spectra: "+removeAmbiguousSpectra+"\n");
        buf.append("Refresh Protein Matches: "+refreshPeptideProteinMatches+"\n");
        buf.append("Allow I/L substitutions: "+doItoLSubstitution+"\n");
        buf.append("Remove asterisks: "+removeAsterisks+"\n");
        buf.append("Calculate NSAF for all proteins: "+calculateAllNsaf+"\n");
        buf.append("MORE PARAMS: \n");
        for(IdPickerParam param: moreFilters) {
            buf.append(param.toString()+"\n");
        }
        return buf.toString();
    }
    
    
    public boolean useIdPickerFDRFormula() {
        return useIdPickerFDRFormula;
    }
    public void setUseIdPickerFDRFormula(boolean useIdPickerFDRFormula) {
        this.useIdPickerFDRFormula = useIdPickerFDRFormula;
    }
    public String getDecoyPrefix() {
        return decoyPrefix;
    }
    public void setDecoyPrefix(String decoyPrefix) {
        this.decoyPrefix = decoyPrefix;
    }
    public float getMaxFdr() {
        return maxFdr;
    }
    public void setMaxFdr(float maxFdr) {
        this.maxFdr = maxFdr;
    }
//    public float getMaxRelativeFdr() {
//        return maxRelativeFdr;
//    }
//    public void setMaxRelativeFdr(float maxRelativeFdr) {
//        this.maxRelativeFdr = maxRelativeFdr;
//    }
//    public float getDecoyRatio() {
//        return decoyRatio;
//    }
//    public void setDecoyRatio(float decoyRatio) {
//        this.decoyRatio = decoyRatio;
//    }
//    
//    public boolean getDoParsimonyAnalysis() {
//        return doParsimonyAnalysis;
//    }
//    public void setDoParsimonyAnalysis(boolean doParsimonyAnalysis) {
//        this.doParsimonyAnalysis = doParsimonyAnalysis;
//    }
    
    public boolean getDoFdrCalculation() {
        return doFdrCalculation;
    }
    public void setDoFdrCalculation(boolean doFdrCalculation) {
        this.doFdrCalculation = doFdrCalculation;
    }
    
    public List<IdPickerParam> getMoreFilters() {
        return moreFilters;
    }
    public void addMoreFilters(List<IdPickerParam> moreFilters) {
        this.moreFilters = moreFilters;
    }
    public PeptideDefinition getPeptideDefinition() {
        return peptideDefinition;
    }
    public void setPeptideDefinition(PeptideDefinition peptideDefinition) {
        this.peptideDefinition = peptideDefinition;
    }
    public int getMinPeptides() {
        return minPeptides;
    }
    public void setMinPeptides(int minPeptides) {
        this.minPeptides = minPeptides;
    }
    public int getMinUniquePeptides() {
        return minUniquePeptides;
    }
    public void setMinUniquePeptides(int minUniquePeptides) {
        this.minUniquePeptides = minUniquePeptides;
    }
    public float getMinCoverage() {
        return minCoverage;
    }
    public void setMinCoverage(float minCoverage) {
        this.minCoverage = minCoverage;
    }
//    public int getMinPeptideSpectra() {
//        return minPeptideSpectra;
//    }
//    public void setMinPeptideSpectra(int minPeptideSpectra) {
//        this.minPeptideSpectra = minPeptideSpectra;
//    }
    public int getMinPeptideLength() {
        return minPeptideLength;
    }
    public void setMinPeptideLength(int minPeptideLength) {
        this.minPeptideLength = minPeptideLength;
    }
    public boolean isRemoveAmbiguousSpectra() {
        return removeAmbiguousSpectra;
    }
    public void setRemoveAmbiguousSpectra(
            boolean removeSpectraWithMultiplePeptides) {
        this.removeAmbiguousSpectra = removeSpectraWithMultiplePeptides;
    }
    public boolean isRefreshPeptideProteinMatches() {
    	return this.refreshPeptideProteinMatches;
    }
    public void setRefreshPeptideProteinMatches(boolean refreshPeptideProteinMatches) {
    	this.refreshPeptideProteinMatches = refreshPeptideProteinMatches;
    }
    public boolean isDoItoLSubstitution() {
		return doItoLSubstitution;
	}
	public void setDoItoLSubstitution(boolean doItoLSubstitution) {
		this.doItoLSubstitution = doItoLSubstitution;
	}
	public boolean isRemoveAsterisks() {
		return removeAsterisks;
	}
	public void setRemoveAsterisks(boolean removeAsterisks) {
		this.removeAsterisks = removeAsterisks;
	}

	public boolean isCalculateAllNsaf() {
		return calculateAllNsaf;
	}

	public void setCalculateAllNsaf(boolean calculateAllNsaf) {
		this.calculateAllNsaf = calculateAllNsaf;
	}

	public boolean isUseIdPickerFDRFormula() {
        return useIdPickerFDRFormula;
    }
    public SCORE getScoreForFDR() {
        return scoreForFDR;
    }

    public void setScoreForFDR(SCORE scoreForFDR) {
        this.scoreForFDR = scoreForFDR;
    }
    
}
