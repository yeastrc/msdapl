/**
 * ProlucidSearchResult.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import org.yeastrc.ms.domain.search.MsSearchResultIn;

/**
 * 
 */
public interface ProlucidSearchResultIn extends MsSearchResultIn {

    public abstract ProlucidResultData getProlucidResultData();
}
