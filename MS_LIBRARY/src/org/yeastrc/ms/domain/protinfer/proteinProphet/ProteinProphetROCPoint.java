/**
 * ProteinProphetROCPoint.java
 * @author Vagisha Sharma
 * Jul 31, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.io.Serializable;

public class ProteinProphetROCPoint implements Serializable {
    
    private int proteinferId;
    private double sensitivity;
    private double falsePositiveErrorRate;
    private double minProbability;
    private int numCorrect;
    private int numIncorrect;
    
    public double getSensitivity() {
        return sensitivity;
    }
    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }
    public double getFalsePositiveErrorRate() {
        return falsePositiveErrorRate;
    }
    public void setFalsePositiveErrorRate(double error) {
        this.falsePositiveErrorRate = error;
    }
    public double getMinProbability() {
        return minProbability;
    }
    public void setMinProbability(double probability) {
        this.minProbability = probability;
    }
    public int getNumCorrect() {
        return numCorrect;
    }
    public void setNumCorrect(int numCorrect) {
        this.numCorrect = numCorrect;
    }
    public int getNumIncorrect() {
        return numIncorrect;
    }
    public void setNumIncorrect(int numIncorrect) {
        this.numIncorrect = numIncorrect;
    }
    public int getProteinferId() {
        return proteinferId;
    }
    public void setProteinferId(int proteinferId) {
        this.proteinferId = proteinferId;
    }
}