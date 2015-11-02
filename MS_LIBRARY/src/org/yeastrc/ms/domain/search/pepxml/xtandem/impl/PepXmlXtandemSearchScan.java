/**
 * PepXmlXtandemSearchScan.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml.xtandem.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.XtandemPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.pepxml.impl.PepXmlSearchScan;
import org.yeastrc.ms.domain.search.pepxml.xtandem.PepXmlXtandemSearchScanIn;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResultIn;

/**
 * 
 */
public class PepXmlXtandemSearchScan extends PepXmlSearchScan<XtandemPeptideProphetResultIn, XtandemSearchResultIn>
    implements PepXmlXtandemSearchScanIn {

    private List<XtandemPeptideProphetResultIn> results = new ArrayList<XtandemPeptideProphetResultIn>();
    
    @Override
    public List<XtandemPeptideProphetResultIn> getScanResults() {
        return results;
    }

    public void addSearchResult(XtandemPeptideProphetResultIn result) {
        this.results.add(result);
    }
}
