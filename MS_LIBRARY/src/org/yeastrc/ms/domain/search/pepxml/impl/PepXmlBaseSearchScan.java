/**
 * PepXmlBaseSearchScan.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.BasePeptideProphetResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.pepxml.PepXmlBaseSearchScanIn;

/**
 * 
 */
public class PepXmlBaseSearchScan extends PepXmlSearchScan<BasePeptideProphetResultIn, MsSearchResultIn>
    implements PepXmlBaseSearchScanIn {

    private List<BasePeptideProphetResultIn> results = new ArrayList<BasePeptideProphetResultIn>();
    
    @Override
    public void addSearchResult(BasePeptideProphetResultIn result) {
        results.add(result);
    }

    @Override
    public List<BasePeptideProphetResultIn> getScanResults() {
        return results;
    }

}
