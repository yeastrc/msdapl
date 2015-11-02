package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataIn;

public class PeptideProphetResultData implements PeptideProphetResultDataIn {

    private double probability = -1.0;
    private double probabilityNet_0 = -1.0;
    private double probabilityNet_1 = -1.0;
    private double probabilityNet_2 = -1.0;
    private double fVal;
    private double massDiff = 0.0;
    private int ntt = -1;
    private int nmc = -1;
    private boolean hasAllNttProb = false;
    protected static final Pattern allNttPattern = Pattern.compile("^\\((\\d\\.?\\d*)\\s*,\\s*(\\d\\.?\\d*)\\s*,\\s*(\\d\\.?\\d*)\\s*\\)$");

    public PeptideProphetResultData() {
        super();
    }

    public boolean hasAllNttProb() {
        return hasAllNttProb;
    }

    public void setAllNttProb(String allNttProb) {
        if(allNttProb != null) {
            Matcher m = allNttPattern.matcher(allNttProb);
            if(m.matches()) {
                probabilityNet_0 = Double.parseDouble(m.group(1));
                probabilityNet_1 = Double.parseDouble(m.group(2));
                probabilityNet_2 = Double.parseDouble(m.group(3));
            }
            hasAllNttProb = true;
        }
    }

    @Override
    public double getProbabilityNet_0() {
       return probabilityNet_0;
    }

    @Override
    public double getProbabilityNet_1() {
        return probabilityNet_1;
    }

    @Override
    public double getProbabilityNet_2() {
        return probabilityNet_2;
    }

    public void setProbabilityNet_0(double probabilityNet_0) {
        this.probabilityNet_0 = probabilityNet_0;
    }

    public void setProbabilityNet_1(double probabilityNet_1) {
        this.probabilityNet_1 = probabilityNet_1;
    }

    public void setProbabilityNet_2(double probabilityNet_2) {
        this.probabilityNet_2 = probabilityNet_2;
    }

    @Override
    public double getMassDifference() {
        return massDiff;
    }

    public void setMassDifference(double massDiff) {
        this.massDiff = massDiff;
    }

    @Override
    public int getNumMissedCleavages() {
        return nmc;
    }

    public void setNumMissedCleavages(int nmc) {
        this.nmc = nmc;
    }

    @Override
    public int getNumEnzymaticTermini() {
        return ntt;
    }

    public void setNumEnzymaticTermini(int ntt) {
        this.ntt = ntt;
    }

    @Override
    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public double getfVal() {
        return fVal;
    }

    public void setfVal(double fVal) {
        this.fVal = fVal;
    }
    
    public static void main(String[] args) {
        String allnttProb = "(0.0000,0.0029,0.3436)";
        Matcher m = allNttPattern.matcher(allnttProb);
        if(m.matches()) {
            System.out.println("NTT_0: "+m.group(1));
            System.out.println("NTT_1: "+m.group(2));
            System.out.println("NTT_2: "+m.group(3));
        }
    }

}