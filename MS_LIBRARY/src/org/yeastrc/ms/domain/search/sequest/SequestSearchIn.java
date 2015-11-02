/**
 * SequestSearch.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.Param;

/**
 * 
 */
public interface SequestSearchIn extends MsSearchIn {
    
    public abstract List<Param> getSequestParams();
}
