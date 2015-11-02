/**
 * SequestSearchDb.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Param;

/**
 * 
 */
public interface SequestSearch extends MsSearch {

    public abstract List<Param> getSequestParams();
}
