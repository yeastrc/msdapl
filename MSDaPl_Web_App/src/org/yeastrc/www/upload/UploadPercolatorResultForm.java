/**
 * 
 */
package org.yeastrc.www.upload;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * UploadPercolatorResultForm.java
 * @author Vagisha Sharma
 * Oct 27, 2010
 * 
 */
public class UploadPercolatorResultForm extends ActionForm {

	private int experimentId;
	private int projectId;
	private String directory;
	private String comments;
	
	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();
		
		if (this.directory == null || directory.trim().length() == 0) {
			errors.add("upload", new ActionMessage("error.upload.nodirectoryname"));
		}
		return errors;
	}

	
	public int getExperimentId() {
		return experimentId;
	}
	
	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}
	
	public String getDirectory() {
		return directory;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
