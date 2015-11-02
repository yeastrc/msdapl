package edu.uwpr.protinfer.pepxml;

import java.math.BigDecimal;
import java.util.List;

import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;

public class SequestSearchHit {

    private PeptideHit peptide;
    private int numPredictedIons;
    private int numMatchedIons;
    private BigDecimal calcNeutralMass;
    
    private BigDecimal xcorr;
    private BigDecimal deltaCn;
    private BigDecimal spScore;
    private int spRank;
    private int xcorrRank;
    
    
    /**
     * @return the peptide
     */
    public PeptideHit getPeptide() {
        return peptide;
    }

    /**
     * @param peptide the peptide to set
     */
    public void setPeptide(PeptideHit peptide) {
        this.peptide = peptide;
    }

    /**
     * @return the numPredictedIons
     */
    public int getNumPredictedIons() {
        return numPredictedIons;
    }

    /**
     * @param numPredictedIons the numPredictedIons to set
     */
    public void setNumPredictedIons(int numPredictedIons) {
        this.numPredictedIons = numPredictedIons;
    }

    /**
     * @return the numMatchedIons
     */
    public int getNumMatchedIons() {
        return numMatchedIons;
    }

    /**
     * @param numMatchedIons the numMatchedIons to set
     */
    public void setNumMatchedIons(int numMatchedIons) {
        this.numMatchedIons = numMatchedIons;
    }

    /**
     * @return the calcNeutralMass
     */
    public BigDecimal getCalcNeutralMass() {
        return calcNeutralMass;
    }

    /**
     * @param calcNeutralMass the calcNeutralMass to set
     */
    public void setCalcNeutralMass(BigDecimal calcNeutralMass) {
        this.calcNeutralMass = calcNeutralMass;
    }

    public List<Protein> getProteins() {
        return peptide.getProteinList();
    }
    
    public Protein getFirstProtein() {
        return getProteins().get(0);
    }
    
    public boolean hasUniqueProtein() {
        return getProteins().size() == 1;
    }
    
    /**
     * @return the xcorr
     */
    public BigDecimal getXcorr() {
        return xcorr;
    }

    /**
     * @param xcorr the xcorr to set
     */
    public void setXcorr(BigDecimal xcorr) {
        this.xcorr = xcorr;
    }

    /**
     * @return the deltaCn
     */
    public BigDecimal getDeltaCn() {
        return deltaCn;
    }

    /**
     * @param deltaCn the deltaCn to set
     */
    public void setDeltaCn(BigDecimal deltaCn) {
        this.deltaCn = deltaCn;
    }

    /**
     * @return the spScore
     */
    public BigDecimal getSpScore() {
        return spScore;
    }

    /**
     * @param spScore the spScore to set
     */
    public void setSpScore(BigDecimal spScore) {
        this.spScore = spScore;
    }

    /**
     * @return the spRank
     */
    public int getSpRank() {
        return spRank;
    }

    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        this.spRank = spRank;
    }

    /**
     * @return the xcorrRank
     */
    public int getXcorrRank() {
        return xcorrRank;
    }

    /**
     * @param xcorrRank the xcorrRank to set
     */
    public void setXcorrRank(int xcorrRank) {
        this.xcorrRank = xcorrRank;
    }
    
    public boolean isDecoyHit() {
       return this.peptide.isDecoyPeptide();
    }
}
