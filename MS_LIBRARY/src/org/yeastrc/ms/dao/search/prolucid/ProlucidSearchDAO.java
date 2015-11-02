/**
 * ProlucidSearchDAO.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid;

import org.yeastrc.ms.dao.search.GenericSearchDAO;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearch;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchIn;

/**
 * 
 */
public interface ProlucidSearchDAO extends GenericSearchDAO<ProlucidSearchIn, ProlucidSearch> {

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
     * Returns the specificity contraint used for this search
     * @param searchId
     * @return
     */
    public abstract int getSpecificity(int searchId);

}
