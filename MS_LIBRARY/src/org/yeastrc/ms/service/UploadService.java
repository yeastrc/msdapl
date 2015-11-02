/**
 * UploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

/**
 * 
 */
public interface UploadService {

    public void setDirectory(String directory);
    
    public void setRemoteServer(String remoteServer);
    
    public void setRemoteDirectory(String remoteDirectory);
    
    public boolean preUploadCheckPassed();
    
    public String getPreUploadCheckMsg();
    
    public String getUploadSummary();
    
    public void upload() throws UploadException;
    
}
