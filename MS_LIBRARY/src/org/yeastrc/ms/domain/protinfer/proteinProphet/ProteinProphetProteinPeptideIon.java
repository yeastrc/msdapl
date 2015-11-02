/**
 * ProteinProphetProteinPeptide.java
 * @author Vagisha Sharma
 * Jul 17, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

/**
 * 
 */
public class ProteinProphetProteinPeptideIon extends GenericProteinferIon<ProteinferSpectrumMatch> {

    private int piProteinId = 0;
   
    private double initialProbability = -1.0;
    private double nspAdjProbability = -1.0;
    private double weight = -1.0;
    private double numSiblingPeptides = -1.0;
    private boolean isContributingEvidence;
    private List<ProteinferSpectrumMatch> psmList;
    
    private List<Modification> modifications;
    private String unmodifiedSequence;
    
    

    public ProteinProphetProteinPeptideIon() {
        psmList = new ArrayList<ProteinferSpectrumMatch>();
        modifications = new ArrayList<Modification>();
    }
    
    public double getInitialProbability() {
        return initialProbability;
    }
    public void setInitialProbability(double initialProbability) {
        this.initialProbability = initialProbability;
    }
    public double getNspAdjProbability() {
        return nspAdjProbability;
    }
    public void setNspAdjProbability(double nspAdjProbability) {
        this.nspAdjProbability = nspAdjProbability;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public boolean getIsContributingEvidence() {
        return isContributingEvidence;
    }
    public void setIsContributingEvidence(boolean isContributingEvidence) {
        this.isContributingEvidence = isContributingEvidence;
    }
    public double getNumSiblingPeptides() {
        return numSiblingPeptides;
    }
    public void setNumSiblingPeptides(double numSiblingPeptides) {
        this.numSiblingPeptides = numSiblingPeptides;
    }
    
    public List<ProteinferSpectrumMatch> getPsmList() {
        return psmList;
    }
    
    public void addPsm(ProteinferSpectrumMatch psm) {
        this.psmList.add(psm);
    }
    
    public List<Modification> getModifications() {
        return modifications;
    }
    
    public void addModification(Modification mod) {
        this.modifications.add(mod);
    }
    
    public int getPiProteinId() {
        return piProteinId;
    }

    public void setPiProteinId(int piProteinId) {
        this.piProteinId = piProteinId;
    }

    public String getUnmodifiedSequence() {
        return unmodifiedSequence;
    }

    public void setUnmodifiedSequence(String unmodifiedSequence) {
        this.unmodifiedSequence = unmodifiedSequence;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append("\t");
        buf.append("initProb: "+initialProbability);
        buf.append("\t");
        buf.append("nspAdjProb: "+nspAdjProbability);
        buf.append("\t");
        buf.append("wt: "+weight);
        buf.append("\t");
        buf.append("numsiblingPept: "+numSiblingPeptides);
        buf.append("\t");
        buf.append("contrib_evidence: "+this.isContributingEvidence);
        return buf.toString();
    }
}
