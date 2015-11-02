package edu.uwpr.protinfer.filter;


public interface FilterCriteria <T extends Filterable>{

    public abstract boolean filter(T filterable);
}
