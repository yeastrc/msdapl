/**
 * 
 */
package org.yeastrc.jobqueue;

import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;


/**
 * MsAnalysisUploadJob.java
 * @author Vagisha Sharma
 * Oct 27, 2010
 * 
 */
public class MsAnalysisUploadJob extends Job {

	public MsAnalysisUploadJob() {
		super();
	}
	
	private int projectID;
	private int experimentID;
	private int searchAnalysisId;
	private String serverDirectory;
	private String comments;
	
	private Project project;
	
	public Project getProject() {
		if (this.project == null) {
			
			try {
				this.project = ProjectFactory.getProject( this.projectID );
			} catch (Exception e) { ; }
		}
		
		return this.project;
	}
	
	public boolean isRunning() {
        return this.getStatus() == JobUtils.STATUS_QUEUED || this.getStatus() == JobUtils.STATUS_OUT_FOR_WORK;
    }
    public boolean isFailed() {
        return this.getStatus() == JobUtils.STATUS_SOFT_ERROR || this.getStatus() == JobUtils.STATUS_HARD_ERROR;
    }
    public boolean isComplete() {
        return this.getStatus() == JobUtils.STATUS_COMPLETE;
    }
	
	public int getSearchAnalysisId() {
		return searchAnalysisId;
	}
	public void setSearchAnalysisId(int searchAnalysisId) {
		this.searchAnalysisId = searchAnalysisId;
	}
	
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return the projectID
	 */
	public int getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID the projectID to set
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	
	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}

	/**
	 * @return the serverDirectory
	 */
	public String getServerDirectory() {
		return serverDirectory;
	}
	/**
	 * @param serverDirectory the serverDirectory to set
	 */
	public void setServerDirectory(String serverDirectory) {
		this.serverDirectory = serverDirectory;
	}
	
}
