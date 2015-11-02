/**
 * SQTSearchDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;

/**
 * 
 */
public interface SQTSearchDataProvider <T extends SQTSearchScanIn<?>> {

    public abstract String getFileName();
    
    public abstract SQTRunSearchIn getSearchHeader() throws DataProviderException;
    
    public abstract boolean hasNextSearchScan();
    
    public abstract T getNextSearchScan() throws DataProviderException;
    
    public abstract void close();
}


