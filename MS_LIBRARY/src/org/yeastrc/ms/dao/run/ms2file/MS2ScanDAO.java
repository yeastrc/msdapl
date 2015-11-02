/**
 * MS2ScanDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file;

import org.yeastrc.ms.dao.run.GenericScanDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;

/**
 * 
 */
public interface MS2ScanDAO extends GenericScanDAO <MS2ScanIn, MS2Scan> {

    public abstract boolean isGeneratedByBullseye(int scanId);
}
