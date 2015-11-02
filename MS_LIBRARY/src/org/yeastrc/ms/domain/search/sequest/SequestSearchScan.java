/**
 * SequestSearchScan.java
 * @author Vagisha Sharma
 * Aug 21, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;

/**
 * 
 */
public interface SequestSearchScan extends SQTSearchScanIn<SequestSearchResultIn> {

    public abstract List<SequestSearchResultIn> getScanResults();
}
