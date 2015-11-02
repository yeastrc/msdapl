/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run;

import java.math.BigDecimal;
import java.util.List;



public interface MsScanIn extends MsScanBase {

    /**
     * String[0] = m/z; String[1] = RT
     * @return
     */
    public abstract List<String[]> getPeaksString();
    
    public abstract List<Peak> getPeaks();

}

interface MsScanBase {

    public abstract int getStartScanNum();

    public abstract int getEndScanNum();

    public abstract int getMsLevel();

    public abstract BigDecimal getPrecursorMz();

    public abstract int getPrecursorScanNum();

    public abstract BigDecimal getRetentionTime();

    public abstract String getFragmentationType();
    
    public abstract DataConversionType getDataConversionType();
    
    public abstract int getPeakCount();

}