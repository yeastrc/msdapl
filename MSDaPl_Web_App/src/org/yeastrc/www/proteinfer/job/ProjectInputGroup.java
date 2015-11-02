/**
 * ProjectSearch.java
 * @author Vagisha Sharma
 * Jan 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import java.util.Date;
import java.util.List;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;

/**
 * 
 */
public class ProjectInputGroup {

    private int projectId;
    private int inputGroupId;
    private Date uploadDate;
    private List<ProteinInferIputFile> files;
    
    public ProjectInputGroup(int projectId, MsSearch search, List<ProteinInferIputFile> runSearchFiles) {
        this.projectId = projectId;
        this.inputGroupId = search.getId();
        this.uploadDate = search.getUploadDate();
        this.files = runSearchFiles;
    }
    
    public ProjectInputGroup(int projectId, MsSearchAnalysis analysis, List<ProteinInferIputFile> runSearchAnalysisFiles) {
        this.projectId = projectId;
        this.inputGroupId = analysis.getId();
        this.uploadDate = analysis.getUploadDate();
        this.files = runSearchAnalysisFiles;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public int getInputGroupId() {
        return inputGroupId;
    }
    
    public Date getUploadDate() {
        return uploadDate;
    }
    
    public List<ProteinInferIputFile> getFiles() {
        return files;
    }
}
