/**
 * MsExperiment.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 
 */
public interface MsExperiment {

    public abstract int getId();
    
    public abstract void setId(int id);
    
    public abstract String getServerAddress();
    
    public abstract void setServerAddress(String serverAddress);
    
    public abstract String getServerDirectory();
    
    public abstract void setServerDirectory(String serverDirectory);
    
    public abstract Date getUploadDate();
    
    public abstract void setUploadDate(Date uploadDate);
    
    public abstract Timestamp getLastUpdateDate();
    
    public abstract String getComments();
    
    public abstract void setComments(String comments);
    
    public abstract int getInstrumentId();
    
    public abstract void setInstrumentId(int instrumentId);
}
