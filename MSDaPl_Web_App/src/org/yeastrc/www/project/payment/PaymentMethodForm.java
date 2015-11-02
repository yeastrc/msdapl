/*
 * RegisterForm.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project.payment;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.www.costcenter.PaymentMethodChecker;

public class PaymentMethodForm extends ActionForm {

	private int id;
	private int projectId;
	private int paymentMethodId; // only available when we are editing an existing payment method
	private String uwBudgetNumber;
	private String poNumber;
	private String contactFirstName;
	private String contactLastName;
	private String contactEmail;
	private String contactPhone;
	private String organization;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String zip;
	private String country;
	private boolean isCurrent;
	
	private boolean isEditable; // If false only the status field is editable
	private boolean federalFunding = false; 
	
	private boolean ponumberAllowed = true; // Only Non-UW affiliated projects are allowed a PO number.
	private boolean uwbudgetAllowed = false; // Only UW affiliated are allowed a UW Budget number.
	

	@Override
	public void	reset(ActionMapping mapping, HttpServletRequest request)  {
		
		this.federalFunding = false; // this is a checkbox; set it to false
	}
	
	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();
		
		
		// We need atleast a UW budget number OR a PO number
		if (StringUtils.isBlank(uwBudgetNumber) && StringUtils.isBlank(poNumber)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "At least UW budget number or a PO number is required"));
		}
		
		if (StringUtils.isNotBlank(uwBudgetNumber) && StringUtils.isNotBlank(poNumber)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Only ONE of UW budget number or PO number should be entered"));
		}
		
		if(!StringUtils.isBlank(uwBudgetNumber)) {
			if(!PaymentMethodChecker.getInstance().checkUwbudgetNumber(uwBudgetNumber)) {
				errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Invalid UW budget number."));
			}
		}
		if(!StringUtils.isBlank(poNumber)) {
			// remove any spaces
			poNumber = poNumber.trim().replaceAll("\\s", "");
			if(!PaymentMethodChecker.getInstance().checkPonumber(poNumber)) {
				errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Invalid PO number."));
			}
		}
		
		/*
		
		if(StringUtils.isBlank(contactFirstName)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "First name"));
		}
		
		if(StringUtils.isBlank(contactLastName)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Last name"));
		}
		
		// email
		if(StringUtils.isBlank(contactEmail)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "E-mail address"));
		}
		else if(!contactEmail.contains("@") || contactEmail.length() < 3) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "E-mail address appears invalid"));
		}
		
		// phone number
		if(StringUtils.isBlank(contactPhone)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Phone number"));
		}
		else {
			// need at least 10 digits in the phone number
			String myStr = contactPhone.replaceAll( "[^\\d]", "" );
			if(myStr.length() < 10) {
				errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Phone number appears invalid"));
			}
		}
		
		
		if(StringUtils.isBlank(organization)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Organization"));
		}
		
		if(StringUtils.isBlank(addressLine1)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Address"));
		}
		
		if(StringUtils.isBlank(city)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "City"));
		}
		
		if(StringUtils.isBlank(state)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "State"));
		}
		
		if(StringUtils.isBlank(zip)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Zip"));
		}
		else if(zip.length() < 5) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Zip code appears invalid"));
		}
		
		if(StringUtils.isBlank(country)) {
			errors.add("payment", new ActionMessage("error.payment.infoincomplete", "Country"));
		}
		*/
		return errors;
	}


	public String getUwBudgetNumber() {
		return uwBudgetNumber;
	}

	public void setUwBudgetNumber(String uwBudgetNumber) {
		this.uwBudgetNumber = uwBudgetNumber;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getContactFirstName() {
		return contactFirstName;
	}

	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}

	public String getContactLastName() {
		return contactLastName;
	}

	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getOrganization() {
		return organization;
	}
	
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(int paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public boolean isFederalFunding() {
		return federalFunding;
	}

	public void setFederalFunding(boolean federalFunding) {
		this.federalFunding = federalFunding;
	}
	
	public boolean isPonumberAllowed() {
		return ponumberAllowed;
	}

	public void setPonumberAllowed(boolean ponumberAllowed) {
		this.ponumberAllowed = ponumberAllowed;
	}

	public boolean isUwbudgetAllowed() {
		return uwbudgetAllowed;
	}

	public void setUwbudgetAllowed(boolean uwbudgetAllowed) {
		this.uwbudgetAllowed = uwbudgetAllowed;
	}
}