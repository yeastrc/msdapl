/* SearchProjectsForm.java
 * Created on Apr 6, 2004
 */
package org.yeastrc.www.project;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

/**
 * Action form for searching projects.
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Apr 6, 2004
 *
 */
public class SearchProjectsForm extends ActionForm {

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

	/** The groups for this search */
	private String[] groups = new String[0];
	
	/** The types for this search */
	private String[] types = new String[0];
	
	/** The search string entered */
	private String searchString = "";

	/**
	 * @return
	 */
	public String[] getGroups() {
		return groups;
	}

	/**
	 * @return
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 * @return
	 */
	public String[] getTypes() {
		return types;
	}

	/**
	 * @param strings
	 */
	public void setGroups(String[] strings) {
		groups = strings;
	}

	/**
	 * @param string
	 */
	public void setSearchString(String string) {
		searchString = string;
	}

	/**
	 * @param strings
	 */
	public void setTypes(String[] strings) {
		types = strings;
	}

}
