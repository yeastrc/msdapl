/**
 * SQTSearchScanDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile;



/**
 * 
 */
public interface SQTSearchScan extends SQTSearchScanBase {

    /**
     * @return the database id of the search to which this scan result belongs
     */
    public abstract int getRunSearchId();
    
    /**
     * @return the database id of the scan
     */
    public abstract int getScanId();
   
}
