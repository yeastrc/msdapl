/**
 * PeptideProphetResultFilterCriteria.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

import org.yeastrc.ms.domain.search.ResultFilterCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;

/**
 * 
 */
public class PeptideProphetResultFilterCriteria extends ResultFilterCriteria {

    private Double minProbability;
    private Double maxProbability;
    
    public boolean hasFilters() {
        if(super.hasFilters())
            return true;
        
        return (hasProbabilityFilter());
    }
    
    public boolean superHasFilters() {
        return super.hasFilters();
    }

    //-------------------------------------------------------------
    // PROBABILITY FILTER
    //-------------------------------------------------------------
    public Double getMinProbability() {
        return minProbability;
    }

    public void setMinProbability(Double minProbability) {
        this.minProbability = minProbability;
    }

    public Double getMaxProbability() {
        return maxProbability;
    }

    public void setMaxProbability(Double maxProbability) {
        this.maxProbability = maxProbability;
    }

    public boolean hasProbabilityFilter() {
      return (minProbability != null || maxProbability != null);
    }
    
    public String makeProbabilityFilterSql() {
        return makeFilterSql(SORT_BY.PEPTP_PROB.getColumnName(), minProbability, maxProbability);
    }
    
}
