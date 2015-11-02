/**
 * PeptideProphetResultPlut.java
 * @author Vagisha Sharma
 * Aug 4, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;


import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;

/**
 * 
 */
public class PeptideProphetResultPlusSequest extends PeptideProphetResultPlus {

    private SequestResultData sequestData;
    
    public PeptideProphetResultPlusSequest(PeptideProphetResult result, MsScan scan) {
        super(result,scan);
    }
    
    public SequestResultData getSequestData() {
        return sequestData;
    }

    public void setSequestData(SequestResultData sequestData) {
        this.sequestData = sequestData;
    }
}
