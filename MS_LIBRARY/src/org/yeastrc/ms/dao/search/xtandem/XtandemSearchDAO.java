/**
 * XtandemSearchDAO.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.xtandem;

import org.yeastrc.ms.dao.search.GenericSearchDAO;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearch;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchIn;

/**
 * 
 */
public interface XtandemSearchDAO extends GenericSearchDAO <XtandemSearchIn, XtandemSearch>{

    public abstract String getSearchParamValue(int searchId, String paramName);
    
    /**
     * Returns the parent mass type (Average or Monoisotopic) used for the search
     * @param searchId
     * @return
     */
    public abstract MassType getParentMassType(int searchId);
    
    /**
     * Returns the fragment mass type (Average or Monoisotopic) used for the search
     * @param searchId
     * @return
     */
    public abstract MassType getFragmentMassType(int searchId);
    
    /**
     * Returns the enzymatic termini contraint used for this search
     * @param searchId
     * @return
     */
    public abstract int getNumEnzymaticTermini(int searchId);
}
