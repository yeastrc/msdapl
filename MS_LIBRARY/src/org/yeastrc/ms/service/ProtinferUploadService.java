/**
 * ProtinferUploadService.java
 * @author Vagisha Sharma
 * Aug 2, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

/**
 * 
 */
public interface ProtinferUploadService extends UploadService {

    public abstract void setSearchId(int searchId);
    
    public abstract void setAnalysisId(int analysisId);
    
}
