/**
 * SearchResult.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.parser.sqtFile.DbLocus;

/**
 * 
 */
public class SearchResult implements MsSearchResultIn {

    private String originalSequence;
    private char validationStatus;
    private int charge;
    private BigDecimal observedMass;
    private int scanNumber;
    private List<MsSearchResultProteinIn> matchingLoci;
    private MsSearchResultPeptide resultPeptide;

    public SearchResult() {
        matchingLoci = new ArrayList<MsSearchResultProteinIn>();
    }

    @Override
    public int getScanNumber() {
        return this.scanNumber;
    }

    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
    }
    
    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getCharge() {
        return charge;
    }
    
    public void setObservedMass(BigDecimal observedMass) {
        this.observedMass = observedMass;
    }
    
    public BigDecimal getObservedMass() {
        return this.observedMass;
    }

    public void addMatchingLocus(String accession, String description) {
        DbLocus locus = new DbLocus(accession, description);
        addMatchingProteinMatch(locus);
    }

    public void addMatchingProteinMatch(MsSearchResultProteinIn locus) {
        matchingLoci.add(locus);
    }

    public List<MsSearchResultProteinIn> getProteinMatchList() {
        return this.matchingLoci;
    }
    
    /**
     * @return the validationStatus
     */
    public ValidationStatus getValidationStatus() {
        return ValidationStatus.instance(this.validationStatus);
    }

    /**
     * @param validationStatus the validationStatus to set
     */
    public void setValidationStatus(char validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    /**
     * @param originalSequence the sequence to set
     */
    public void setOriginalPeptideSequence(String originalSequence) {
        this.originalSequence = originalSequence;
    }
    
    public String getOriginalPeptideSequence() {
        return originalSequence;
    }
    
    public void setResultPeptide(MsSearchResultPeptide peptide) {
        this.resultPeptide = peptide;
    }
    
    public MsSearchResultPeptide getResultPeptide() {
        return this.resultPeptide;
    }
}
