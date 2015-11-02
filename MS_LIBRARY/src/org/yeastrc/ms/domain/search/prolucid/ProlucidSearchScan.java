/**
 * ProlucidSearchScan.java
 * @author Vagisha Sharma
 * Aug 31, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;

/**
 * 
 */
public interface ProlucidSearchScan extends SQTSearchScanIn<ProlucidSearchResultIn> {
    
    public abstract List<ProlucidSearchResultIn> getScanResults();
}
