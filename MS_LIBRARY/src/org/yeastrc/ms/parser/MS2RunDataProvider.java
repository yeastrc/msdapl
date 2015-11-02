/**
 * MS2RunDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;

/**
 * 
 */
public interface MS2RunDataProvider  {

    public abstract void open(String filePath, String sha1Sum) throws DataProviderException;
    
    public abstract String getFileName();
    
    public abstract MS2RunIn getRunHeader() throws DataProviderException;
    
//    public abstract boolean hasNextScan();
    
    public abstract MS2ScanIn getNextScan() throws DataProviderException;
    
    public abstract void close();
}
