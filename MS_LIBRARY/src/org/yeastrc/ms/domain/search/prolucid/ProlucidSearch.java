/**
 * ProlucidSearchDb.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearch;

/**
 * 
 */
public interface ProlucidSearch extends MsSearch {

    public abstract List<ProlucidParam> getProlucidParams();
}
