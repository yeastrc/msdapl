package edu.uwpr.protinfer.filter.fdr;

public interface FdrCandidate  {

    public abstract boolean isTargetMatch();
    
    public abstract boolean isDecoyMatch();
    
    public abstract void setFdr(double fdr);
    
    public abstract double getFdr();
}
