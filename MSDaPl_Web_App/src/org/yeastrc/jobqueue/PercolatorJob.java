/**
 * PercolatorJob.java
 * @author Vagisha Sharma
 * Dec 9, 2010
 */
package org.yeastrc.jobqueue;

import java.util.List;

import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.proteinfer.job.ProgramParameters;
import org.yeastrc.www.upload.PercolatorRunForm.PercolatorInputFile;

/**
 * 
 */
public class PercolatorJob extends Job {

	private int projectID;
	private int experimentID;
	private int searchId;
	private List<PercolatorInputFile> percolatorInputFiles;
	private String resultDirectory;
	private String comments;
	private boolean runProteinInference = false;
	private Project project;
	private ProgramParameters programParams;
	
	public PercolatorJob() {
		super();
	}
	
	public Project getProject() {
		if (this.project == null) {
			
			try {
				this.project = ProjectFactory.getProject( this.projectID );
			} catch (Exception e) { ; }
		}
		
		return this.project;
	}
	
	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
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
	
	public int getExperimentID() {
		return experimentID;
	}
	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}
	
	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public List<PercolatorInputFile> getPercolatorInputFiles() {
		return percolatorInputFiles;
	}

	public void setPercolatorInputFiles(
			List<PercolatorInputFile> percolatorInputFiles) {
		this.percolatorInputFiles = percolatorInputFiles;
	}

	public String getResultDirectory() {
		return resultDirectory;
	}

	public void setResultDirectory(String resultDirectory) {
		this.resultDirectory = resultDirectory;
	}
	
	public String getServerDirectory() {
		return getResultDirectory();
	}

	public boolean isRunProteinInference() {
		return runProteinInference;
	}

	public void setRunProteinInference(boolean runProteinInference) {
		this.runProteinInference = runProteinInference;
	}

	public ProgramParameters getProgramParams() {
		return programParams;
	}

	public void setProgramParams(ProgramParameters programParams) {
		this.programParams = programParams;
	}
}
