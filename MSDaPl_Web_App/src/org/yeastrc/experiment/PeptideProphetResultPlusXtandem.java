/**
 * PeptideProphetResultPlusXtandem.java
 * @author Vagisha Sharma
 * Oct 27, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultData;

/**
 * 
 */
public class PeptideProphetResultPlusXtandem extends PeptideProphetResultPlus {

    private XtandemResultData xtandemData;
    
    public PeptideProphetResultPlusXtandem(PeptideProphetResult result, MsScan scan) {
       super(result, scan);
    }
    
    public XtandemResultData getXtandemData() {
        return xtandemData;
    }

    public void setXtandemData(XtandemResultData xtandemData) {
        this.xtandemData = xtandemData;
    }
}
