/**
 * ProlucidSearchResultDb.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import org.yeastrc.ms.domain.search.MsSearchResult;

/**
 * 
 */
public interface ProlucidSearchResult extends MsSearchResult {
    
    public abstract ProlucidResultData getProlucidResultData();
}
