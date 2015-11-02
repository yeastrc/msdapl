/**
 * MsRunDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;

/**
 * 
 */
public interface MsRun extends MsRunBase {

    /**
     * @return database id of the run
     */
    public abstract int getId();
    
    /**
     * @ return the list of enzymes for this run.
     */
    public abstract List<MsEnzyme> getEnzymeList();
}
