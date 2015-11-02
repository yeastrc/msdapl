/*
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.account;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-02-17
 */
public class EditUsernameForm extends ActionForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (this.getUsername() == null || this.getUsername().length() < 1) {
			errors.add("researcher", new ActionMessage("error.register.nousername"));
		}
		if (this.getUsername2() == null || this.getUsername2().length() < 1) {
			errors.add("researcher", new ActionMessage("error.register.nousername2"));
		}
		if (this.getUsername() != null && this.getUsername2() != null &&
		 (!(this.getUsername().equals(this.getUsername2())))) {
			errors.add("researcher", new ActionMessage("error.register.username_verification"));
		}
		
		return errors;
	}


	/** Get the username */
	public String getUsername() { return this.username; }
	
	/** Get the username verification */
	public String getUsername2() { return this.username2; }

	
	
	
	/** Set the username */
	public void setUsername(String arg) { this.username = arg; }
	
	/** Set the username verification */
	public void setUsername2(String arg) { this.username2 = arg; }
	


	
	private String username = null;
	private String username2 = null;
	

}