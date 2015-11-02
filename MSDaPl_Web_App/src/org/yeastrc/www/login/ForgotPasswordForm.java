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
 * @version 2004-02-03
 */
public class ForgotPasswordForm extends ActionForm {

	// The form variables we'll be tracking
	private String username = null;
	private String email = null;

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (( this.getUsername() == null || this.getUsername().length() < 1) &&
		    (this.getEmail() == null || this.getEmail().length() < 1)) {
			errors.add("username", new ActionMessage("error.forgotpassword.noinfo"));
		}

		return errors;

	}
	
	/**
	 * Set the username.
	 * @param arg The username.
	 */
	public void setUsername(String arg) { this.username = arg; }

	/**
	 * Set the email.
	 * @param arg The email.
	 */
	public void setEmail(String arg) { this.email = arg; }


	/**
	 * Get the username inputted into the form.
	 * @return the username
	 */
	public String getUsername() { return this.username; }

	/**
	 * Get the email inputted into the form.
	 * @return the email
	 */
	public String getEmail() { return this.email; }

}