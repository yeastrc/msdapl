package edu.uwpr.protinfer.idpicker;

import edu.uwpr.protinfer.infer.PeptideHit;

public class PeptideSpectrumMatchIDPImpl implements PeptideSpectrumMatchIDP {

    private SpectrumMatchIDPImpl specMatch;
    private PeptideHit peptide;
    private double score;   // e.g. Sequest's xCorr
//    private double relativeScore;  // e.g. Sequest's DeltaCN
    
    public PeptideSpectrumMatchIDPImpl() {
        specMatch = new SpectrumMatchIDPImpl();
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
    public SpectrumMatchIDP getSpectrumMatch() {
        return specMatch;
    }


    public void setPeptide(PeptideHit peptide) {
        this.peptide = peptide;
    }

    public void setSpectrumMatch(SpectrumMatchIDPImpl specMatch) {
        this.specMatch = specMatch;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public double getScore() {
        return score;
    }

//    public void setRelativeScore(double score) {
//        this.relativeScore = score;
//    }
//    
//    public double getRelativeScore() {
//        return relativeScore;
//    }
    
    @Override
    public boolean isDecoyMatch() {
        return peptide.isDecoyPeptide();
    }

    @Override
    public boolean isTargetMatch() {
        return !peptide.isDecoyPeptide();
    }
    
    @Override
    public double getFdr() {
        return specMatch.getFdr();
    }

    @Override
    public void setFdr(double fdr) {
        this.specMatch.setFdr(fdr);
    }

    @Override
    public boolean isAccepted() {
        return specMatch.isAccepted();
    }

    @Override
    public void setAccepted(boolean accepted) {
        specMatch.setAccepted(accepted);
    }
}
