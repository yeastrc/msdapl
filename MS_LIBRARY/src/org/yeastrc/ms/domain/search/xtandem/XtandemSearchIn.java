/**
 * XtandemSearchIn.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.Param;

/**
 * 
 */
public interface XtandemSearchIn extends MsSearchIn {
    
    public abstract List<Param> getXtandemParams();
}
