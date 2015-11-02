/**
 * PepXmlSequestSearchScan.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml.sequest.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.pepxml.impl.PepXmlSearchScan;
import org.yeastrc.ms.domain.search.pepxml.sequest.PepXmlSequestSearchScanIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;

/**
 * 
 */
public class PepXmlSequestSearchScan extends PepXmlSearchScan<SequestPeptideProphetResultIn, SequestSearchResultIn>
        implements PepXmlSequestSearchScanIn {

    private List<SequestPeptideProphetResultIn> results = new ArrayList<SequestPeptideProphetResultIn>();
    
    @Override
    public List<SequestPeptideProphetResultIn> getScanResults() {
        return results;
    }

    public void addSearchResult(SequestPeptideProphetResultIn result) {
        this.results.add(result);
    }
}
