/*
 * RegisterForm.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.register;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

/**
 * <P>This ActionForm is used by the YRC registration form.  It simply
 * allows a potential collaborator to register with the site, so that
 * they can log in and request collaborations, plasmids, or what have you.
 * <P>This is designed to be scoped to the request, NOT the session.
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class RegisterForm extends ActionForm {

	// The form variables we'll be tracking
	private String username = null;
	private String password = null;
	private String password2 = null;

	// Registrant's info
	private String firstName = null;
	private String lastName = null;
	private String email = null;
	private String degree = null;
	private String organization = null;
	private String department = null;
	private String state = null;
	private String zip = null;
	private String country = null;

	public RegisterForm() { super(); }

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (this.getUsername() == null || this.getUsername().length() < 1) {
			errors.add("username", new ActionMessage("error.register.nousername"));
		}

		if (this.getPassword() == null || this.getPassword().length() < 1) {
			errors.add("password", new ActionMessage("error.register.nopassword"));
		}

		if (this.getPassword2() == null || this.getPassword2().length() < 1) {
			errors.add("password", new ActionMessage("error.register.nopassword2"));
		}

		if (this.getPassword() != null && !( this.getPassword().equals( this.getPassword2() ) ) )   {
			errors.add("password", new ActionMessage("error.register.verification"));
		}

		if (this.getFirstName() == null || this.getFirstName().length() < 1) {
			errors.add("firstName", new ActionMessage("error.register.firstname"));
		}

		if (this.getLastName() == null || this.getLastName().length() < 1) {
			errors.add("lastName", new ActionMessage("error.register.lastname"));
		}

		if (this.getEmail() == null || this.getEmail().length() < 1) {
			errors.add("email", new ActionMessage("error.register.email"));
		}

		if (this.getDegree() == null || this.getDegree().length() < 1) {
			errors.add("degree", new ActionMessage("error.register.degree"));
		}

		if (this.getOrganization() == null || this.getOrganization().length() < 1) {
			errors.add("organization", new ActionMessage("error.register.organization"));
		}

		if (this.getDepartment() == null || this.getDepartment().length() < 1) {
			errors.add("department", new ActionMessage("error.register.department"));
		}

		if (this.getState() == null || this.getState().length() != 2) {
			errors.add("state", new ActionMessage("error.register.state"));
		}

		if (this.getCountry() == null || this.getCountry().length() < 1) {
			errors.add("country", new ActionMessage("error.register.country"));
		}

		if (this.getZip() == null || this.getZip().length() < 1) {
			if (this.getCountry() != null && this.getCountry().equals("us"))
				errors.add("zip", new ActionMessage("error.register.zip"));
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
	 * Set the password verifier.
	 * @param arg The password verifier.
	 */
	public void setPassword2(String arg) { this.password2 = arg; }

	/**
	 * Set the first name.
	 * @param arg The first name.
	 */
	public void setFirstName(String arg) { this.firstName = arg; }

	/**
	 * Set the last name.
	 * @param arg The last name.
	 */
	public void setLastName(String arg) { this.lastName = arg; }

	/**
	 * Set the email.
	 * @param arg The email.
	 */
	public void setEmail(String arg) { this.email = arg; }

	/**
	 * Set the degree.
	 * @param arg The degree.
	 */
	public void setDegree(String arg) { this.degree = arg; }

	/**
	 * Set the organization.
	 * @param arg The organization.
	 */
	public void setOrganization(String arg) { this.organization = arg; }

	/**
	 * Set the department.
	 * @param arg The department.
	 */
	public void setDepartment(String arg) { this.department = arg; }

	/**
	 * Set the state.
	 * @param arg The state.
	 */
	public void setState(String arg) { this.state = arg; }

	/**
	 * Set the country.
	 * @param arg The country.
	 */
	public void setCountry(String arg) { this.country = arg; }

	/**
	 * Set the zip.
	 * @param arg The zip.
	 */
	public void setZip(String arg) { this.zip = arg; }


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

	/**
	 * Get the password verification inputted into the form.
	 * @return the password verification
	 */
	public String getPassword2() { return this.password2; }

	/**
	 * Get the first name inputted into the form.
	 * @return the first name
	 */
	public String getFirstName() { return this.firstName; }

	/**
	 * Get the last name inputted into the form.
	 * @return the last name
	 */
	public String getLastName() { return this.lastName; }

	/**
	 * Get the email inputted into the form.
	 * @return the email
	 */
	public String getEmail() { return this.email; }

	/**
	 * Get the degree inputted into the form.
	 * @return the degree
	 */
	public String getDegree() { return this.degree; }

	/**
	 * Get the organization inputted into the form.
	 * @return the organization
	 */
	public String getOrganization() { return this.organization; }

	/**
	 * Get the department inputted into the form.
	 * @return the department
	 */
	public String getDepartment() { return this.department; }

	/**
	 * Get the state inputted into the form.
	 * @return the state
	 */
	public String getState() { return this.state; }

	/**
	 * Get the zip inputted into the form.
	 * @return the zip
	 */
	public String getZip() { return this.zip; }

	/**
	 * Get the country inputted into the form.
	 * @return the country
	 */
	public String getCountry() { return this.country; }

}