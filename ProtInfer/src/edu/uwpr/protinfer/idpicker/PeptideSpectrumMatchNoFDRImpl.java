/**
 * PeptideSpectrumMatchImpl.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.SpectrumMatch;



/**
 * 
 */
public class PeptideSpectrumMatchNoFDRImpl implements PeptideSpectrumMatchNoFDR {

    private SpectrumMatchNoFDRImpl specMatch;
    private PeptideHit peptide;
    
    public PeptideSpectrumMatchNoFDRImpl() {
        specMatch = new SpectrumMatchNoFDRImpl();
    }
    
    @Override
    public int getCharge() {
        return specMatch.getCharge();
    }
    
    @Override
    public int getResultId() {
        return specMatch.getResultId();
    }
    
    @Override
    public int getSearchResultId() {
        return specMatch.getSearchResultId();
    }

    @Override
    public PeptideHit getPeptideHit() {
        return peptide;
    }

    @Override
    public String getPeptideSequence() {
        return peptide.getPeptide().getPeptideSequence();
    }

    @Override
    public int getScanId() {
        return specMatch.getScanId();
    }

    @Override
    public SpectrumMatch getSpectrumMatch() {
        return specMatch;
    }

    public void setPeptide(PeptideHit peptide) {
        this.peptide = peptide;
    }


    public void setSpectrumMatch(SpectrumMatchNoFDRImpl specMatch) {
        this.specMatch = specMatch;
    }
}
