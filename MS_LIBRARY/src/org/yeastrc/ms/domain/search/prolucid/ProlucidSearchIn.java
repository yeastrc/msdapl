/**
 * ProlucidSearch.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchIn;

/**
 * 
 */
public interface ProlucidSearchIn extends MsSearchIn {

    public abstract List<ProlucidParamIn> getProlucidParams();
}
