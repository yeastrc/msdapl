/**
 * MxXmlDataProvider.java
 * @author Vagisha Sharma
 * Jun 22, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.MsScanIn;

/**
 * 
 */
public interface MzXmlDataProvider {

    public abstract void open(String filePath, String sha1Sum) throws DataProviderException;
    
    public abstract String getFileName();
    
    public abstract MsRunIn getRunHeader() throws DataProviderException;
    
    public abstract MsScanIn getNextScan() throws DataProviderException;
    
    public abstract void close();
}
