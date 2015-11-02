/**
 * MsSearchResultDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.util.List;


/**
 * 
 */
public interface MsSearchResult extends MsRunSearchResultBase {

    /**
     * @return database id of the run search this result belongs to.
     */
    public abstract int getRunSearchId();
    
    /**
     * @return database id of the scan for which this result was returned. 
     */
    public abstract int getScanId();
    
    /**
     * @return database id of the result.
     */
    public abstract int getId();
    
    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProtein> getProteinMatchList();
    
}
