/**
 * MS2RunDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file;

import org.yeastrc.ms.dao.run.GenericRunDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;

/**
 * 
 */
public interface MS2RunDAO extends GenericRunDAO<MS2RunIn, MS2Run> {

    public abstract boolean isGeneratedByBullseye(int runId);
}
