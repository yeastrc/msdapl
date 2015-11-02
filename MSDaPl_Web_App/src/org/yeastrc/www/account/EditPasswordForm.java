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
public class EditPasswordForm extends ActionForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (this.getPassword() == null || this.getPassword().length() < 1) {
			errors.add("researcher", new ActionMessage("error.register.nopassword"));
		}
		if (this.getPassword2() == null || this.getPassword2().length() < 1) {
			errors.add("researcher", new ActionMessage("error.register.nopassword2"));
		}
		if (this.getPassword() != null && this.getPassword2() != null &&
		 (!(this.getPassword().equals(this.getPassword2())))) {
			errors.add("researcher", new ActionMessage("error.register.verification"));
		}
		
		return errors;
	}


	/** Get the password */
	public String getPassword() { return this.password; }
	
	/** Get the password verification */
	public String getPassword2() { return this.password2; }

	
	
	
	/** Set the password */
	public void setPassword(String arg) { this.password = arg; }
	
	/** Set the password verification */
	public void setPassword2(String arg) { this.password2 = arg; }
	


	
	private String password = null;
	private String password2 = null;
	

}