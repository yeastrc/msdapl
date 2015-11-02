/**
 * MascotSearch.java
 * @author Vagisha Sharma
 * Oct 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Param;

/**
 * 
 */
public interface MascotSearch extends MsSearch {

    public abstract List<Param> getMascotParams();
}
