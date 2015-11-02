/**
 * PeptideProphetResultPlusMascot.java
 * @author Vagisha Sharma
 * Oct 9, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.mascot.MascotResultData;

/**
 * 
 */
public class PeptideProphetResultPlusMascot extends PeptideProphetResultPlus {

    private MascotResultData mascotData;
   
    public PeptideProphetResultPlusMascot(PeptideProphetResult result, MsScan scan) {
       super(result, scan);
    }
    
    public MascotResultData getMascotData() {
        return mascotData;
    }

    public void setMascotData(MascotResultData mascotData) {
        this.mascotData = mascotData;
    }
}
