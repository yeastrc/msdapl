/**
 * PepXmlMascotSearchScan.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml.mascot.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.MascotPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;
import org.yeastrc.ms.domain.search.pepxml.impl.PepXmlSearchScan;
import org.yeastrc.ms.domain.search.pepxml.mascot.PepXmlMascotSearchScanIn;

/**
 * 
 */
public class PepXmlMascotSearchScan extends PepXmlSearchScan<MascotPeptideProphetResultIn, MascotSearchResultIn>
    implements PepXmlMascotSearchScanIn {

    private List<MascotPeptideProphetResultIn> results = new ArrayList<MascotPeptideProphetResultIn>();
    
    @Override
    public List<MascotPeptideProphetResultIn> getScanResults() {
        return results;
    }

    public void addSearchResult(MascotPeptideProphetResultIn result) {
        this.results.add(result);
    }
}
