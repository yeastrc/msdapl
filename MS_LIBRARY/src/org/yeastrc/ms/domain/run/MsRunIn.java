/**
 * MsRun.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeIn;

public interface MsRunIn extends MsRunBase {

    public abstract List<MsEnzymeIn> getEnzymeList();

}

interface MsRunBase {
    
    public abstract RunFileFormat getRunFileFormat();

    public abstract String getFileName();
    
    public abstract String getCreationDate();

    public abstract String getConversionSW();

    public abstract String getConversionSWVersion();

    public abstract String getConversionSWOptions();

    public abstract String getInstrumentVendor();

    public abstract String getInstrumentModel();

    public abstract String getInstrumentSN();

    public abstract String getComment();

    public abstract String getSha1Sum();

    public abstract String getAcquisitionMethod();
}