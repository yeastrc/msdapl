/**
 * SpectrumDataUploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.List;
import java.util.Set;

public interface SpectrumDataUploadService extends UploadService {

    public void setExperimentId(int experimentId);
    
    /**
     * Returns the filenames WITHOUT extensions
     * @return
     */
    public List<String> getFileNames();
    
    // File names without extensions.
    public void setUploadFileNames(Set<String> fileNames);
    
//    public RunFileFormat getFileFormat();
}
