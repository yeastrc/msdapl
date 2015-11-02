/**
 * MascotSearchDAO.java
 * @author Vagisha Sharma
 * Oct 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.mascot;

import org.yeastrc.ms.dao.search.GenericSearchDAO;
import org.yeastrc.ms.domain.search.mascot.MascotSearch;
import org.yeastrc.ms.domain.search.mascot.MascotSearchIn;

/**
 * 
 */
public interface MascotSearchDAO extends GenericSearchDAO <MascotSearchIn, MascotSearch>{

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
