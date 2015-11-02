/* ManageGroupMembersForm.java
 * Created on Mar 24, 2004
 */
package org.yeastrc.www.admin;

import org.apache.struts.action.*;
import javax.servlet.http.HttpServletRequest;

/**
 * ActionForm for adding users to YRC groups
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 24, 2004
 *
 */
public class ManageGroupMembersForm extends ActionForm {

	// The form variables we'll be tracking
	private String groupName = null;
	private int researcherID = 0;

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		return errors;

	}
	

	/**
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @return
	 */
	public int getResearcherID() {
		return researcherID;
	}

	/**
	 * @param string
	 */
	public void setGroupName(String string) {
		groupName = string;
	}

	/**
	 * @param i
	 */
	public void setResearcherID(int i) {
		researcherID = i;
	}

}
