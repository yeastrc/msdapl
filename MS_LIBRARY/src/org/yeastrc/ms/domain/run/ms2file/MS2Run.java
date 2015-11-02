/**
 * MS2RunDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.MsRun;

/**
 * 
 */
public interface MS2Run extends MsRun {

    /**
     * @return the list of headers for the MS2 run.
     */
    public abstract List<MS2NameValuePair> getHeaderList();
    
    public abstract boolean isGeneratedByBullseye();
}
