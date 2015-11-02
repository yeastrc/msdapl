/**
 * SequestRunSearchResultDb.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import org.yeastrc.ms.domain.search.MsSearchResult;


/**
 * 
 */
public interface SequestSearchResult extends MsSearchResult {
    
    public SequestResultData getSequestResultData();
}
