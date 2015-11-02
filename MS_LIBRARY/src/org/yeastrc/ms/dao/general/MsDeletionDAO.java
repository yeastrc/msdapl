/**
 * MsDeletionDAO.java
 * @author Vagisha Sharma
 * Jul 23, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.general;

/**
 * 
 */
public interface MsDeletionDAO {

    public abstract void deleteExperiment(int experimentId);
    
    public abstract void deleteSearch(int searchId);
}
