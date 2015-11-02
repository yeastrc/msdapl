/**
 * XtandemSearchResult.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem;

import org.yeastrc.ms.domain.search.MsSearchResult;


/**
 * 
 */
public interface XtandemSearchResult extends MsSearchResult {
    
    public XtandemResultData getXtandemResultData();
}
