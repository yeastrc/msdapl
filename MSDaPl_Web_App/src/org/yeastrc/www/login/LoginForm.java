/*
 * RegisterForm.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.login;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class LoginForm extends ActionForm {

	// The form variables we'll be tracking
	private String username = null;
	private String password = null;

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (this.getUsername() == null || this.getUsername().length() < 1) {
			errors.add("username", new ActionMessage("error.login.nousername"));
		}

		if (this.getPassword() == null || this.getPassword().length() < 1) {
			errors.add("password", new ActionMessage("error.login.nopassword"));
		}

		return errors;

	}
	
	/**
	 * Set the username.
	 * @param arg The username.
	 */
	public void setUsername(String arg) { this.username = arg; }

	/**
	 * Set the password.
	 * @param arg The password.
	 */
	public void setPassword(String arg) { this.password = arg; }


	/**
	 * Get the username inputted into the form.
	 * @return the username
	 */
	public String getUsername() { return this.username; }

	/**
	 * Get the password inputted into the form.
	 * @return the password
	 */
	public String getPassword() { return this.password; }

}