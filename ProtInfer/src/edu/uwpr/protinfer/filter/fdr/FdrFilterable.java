package edu.uwpr.protinfer.filter.fdr;

import edu.uwpr.protinfer.filter.Filterable;

public interface FdrFilterable extends Filterable {

    public abstract double getFdr();
    
}
