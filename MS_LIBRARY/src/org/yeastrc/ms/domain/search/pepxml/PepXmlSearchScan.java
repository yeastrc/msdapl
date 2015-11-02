/**
 * PepXmlSearchScan.java
 * @author Vagisha Sharma
 * Jul 14, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml;

/**
 * 
 */
public interface PepXmlSearchScan extends PepXmlSearchScanBase {

    /**
     * @return the database id of the search to which this scan result belongs
     */
    public abstract int getRunSearchId();
    
    /**
     * @return the database id of the scan
     */
    public abstract int getScanId();
}
