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
public class EditInformationForm extends ActionForm {

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (this.getEmail() == null || this.getEmail().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.noemail"));
		}
		if (this.getFirstName() == null || this.getFirstName().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.nofirstname"));
		}
		if (this.getLastName() == null || this.getLastName().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.nolastname"));
		}
		if (this.getDegree() == null || this.getDegree().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.nodegree"));
		}
		if (this.getDepartment() == null || this.getDepartment().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.nodepartment"));
		}
		if (this.getOrganization() == null || this.getOrganization().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.noorganization"));
		}
		if (this.getState() == null || this.getState().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.nostate"));
		}
		if (this.getCountry() == null || this.getCountry().length() < 1) {
			errors.add("researcher", new ActionMessage("error.researcher.nocountry"));
		}

		return errors;
	}


	/** Get the first name */
	public String getFirstName() { return this.firstName; }
	
	/** Get the last name */
	public String getLastName() { return this.lastName; }
	
	/** Get the email addy */
	public String getEmail() { return this.email; }
	
	/** Get the degree */
	public String getDegree() { return this.degree; }
	
	/** Get the department */
	public String getDepartment() { return this.department; }
	
	/** Get the organization */
	public String getOrganization() { return this.organization; }
	
	/** Get the state */
	public String getState() { return this.state; }
	
	/** Get the zip code/postal code */
	public String getZipCode() { return this.zip; }
	
	/** Get the country */
	public String getCountry() { return this.country; }
	
	
	
	/** Set the first name */
	public void setFirstName(String arg) { this.firstName = arg; }
	
	/** Set the last name */
	public void setLastName(String arg) { this.lastName = arg; }
	
	/** Set the email addy */
	public void setEmail(String arg) { this.email = arg; }
	
	/** Set the degree */
	public void setDegree(String arg) { this.degree = arg; }
	
	/** Set the department */
	public void setDepartment(String arg) { this.department = arg; }
	
	/** Set the organization */
	public void setOrganization(String arg) { this.organization = arg; }
	
	/** Set the state */
	public void setState(String arg) { this.state = arg; }
	
	/** Set the zip/postal code */
	public void setZipCode(String arg) { this.zip = arg; }
	
	/** Set the country */
	public void setCountry(String arg) { this.country = arg; }

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

	
	private String firstName = null;
	private String lastName = null;
	private String email = null;
	private String degree = null;
	private String department = null;
	private String organization = null;
	private String state = null;
	private String zip = null;
	private String country = null;
	private int id;
	

}