/**
 * 
 */
package org.yeastrc.jobqueue;

import java.util.Date;

import org.yeastrc.project.Researcher;


/**
 * @author Mike
 *
 */
public class Job {
	
	private int id;
	private int submitter;
	private int type;
	private Date submitDate;
	private Date lastUpdate;
	private int status;
	private int attempts;
	private String log;
	private Researcher researcher;
	
	/**
	 * Get a description of that status for this job
	 * @return
	 */
	public String getStatusDescription() {
		if (this.status == JobUtils.STATUS_COMPLETE)
			return "Complete";
		if (this.status == JobUtils.STATUS_HARD_ERROR)
			return "Hard Error";
		if (this.status == JobUtils.STATUS_OUT_FOR_WORK)
			return "Running";
		if (this.status == JobUtils.STATUS_QUEUED)
			return "Queued";
		if (this.status == JobUtils.STATUS_SOFT_ERROR)
			return "Soft Error";
		else
			return "Unknown Status";
	}
	
	/**
	 * Get the researcher that submitted this job
	 * Returns null if it can not be found for any reason
	 * @return
	 */
	public Researcher getResearcher() {
		if (this.researcher != null)
			return this.researcher;
		
		try {
			Researcher researcher = new Researcher();
			researcher.load( this.submitter );
			this.researcher = researcher;
		} catch (Exception e) { ; }
		
		return this.researcher;
	}
	
	/**
	 * @return the attempts
	 */
	public int getAttempts() {
		return attempts;
	}
	/**
	 * @param attempts the attempts to set
	 */
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}
	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	/**
	 * @return the log
	 */
	public String getLog() {
		return log;
	}
	/**
	 * @param log the log to set
	 */
	public void setLog(String log) {
		this.log = log;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the submitDate
	 */
	public Date getSubmitDate() {
		return submitDate;
	}
	/**
	 * @param submitDate the submitDate to set
	 */
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	/**
	 * @return the submitter
	 */
	public int getSubmitter() {
		return submitter;
	}
	/**
	 * @param submitter the submitter to set
	 */
	public void setSubmitter(int submitter) {
		this.submitter = submitter;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	public String getTypeDescriptionChar() {
		if(this.type == JobUtils.TYPE_MASS_SPEC_UPLOAD)
			return "E";
		else if(this.type == JobUtils.TYPE_ANALYSIS_UPLOAD)
			return "A";
		else if(this.type == JobUtils.TYPE_PROTEINFER_RUN)
			return "I";
		else if(this.type == JobUtils.TYPE_PERC_EXE)
			return "P";
		else
			return "U";
	}
	
	public String getTypeDescription() {
		
		if(this.type == JobUtils.TYPE_MASS_SPEC_UPLOAD)
			return "Experiment Upload (spectra, search results, analysis etc.)";
		else if(this.type == JobUtils.TYPE_ANALYSIS_UPLOAD)
			return "Analysis Results Upload (e.g. Percolator results)";
		else if(this.type == JobUtils.TYPE_PROTEINFER_RUN)
			return "Protein Inference";
		else if(this.type == JobUtils.TYPE_PERC_EXE)
			return "Percolator Run";
		else
			return "Unknown";
	}
	
}
