package org.yeastrc.www.project;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.grant.FundingSourceType;

public class EditGrantForm extends ActionForm {

	private int grantID;
	private String fundingType;
	private String fedFundingAgencyName; // federal
	private String fundingAgencyName; // non-federal
	private int PI;
	private String grantNumber;
	private String grantAmount;
	private String grantTitle;
	
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		// we should have a PI for this grant
		if (PI == 0) {
			errors.add("grant", new ActionMessage("error.grant.nopi"));
		}
		// we should have a title for this grant
		if (grantTitle == null || grantTitle.length() == 0) {
			errors.add("grant", new ActionMessage("error.grant.notitle"));
		}
		// we should know the type of funding
		if (fundingType == null) {
			errors.add("grant", new ActionMessage("error.grant.nofundingtype"));
		}
		// if we know the funding type make sure we have the name of the funding source and
		// any other required information
		else {
			if (FundingSourceType.isFederal(fundingType)) {
				// for a Federal source we need the name of the funding agency
				if (!FundingSourceType.isValidSourceName(fundingType, fedFundingAgencyName)) 
					errors.add("grant", new ActionMessage("error.grant.nofundingsource.federal"));
				// for a Federal source we need a grant number
//				if (grantNumber == null || grantNumber.length() == 0) 
//					errors.add("grant", new ActionMessage("error.grant.nograntnum"));
			}
			else {
				// for a non-federal funding source we need a name
				if (fundingAgencyName == null || fundingAgencyName.length() == 0) 
					errors.add("grant", new ActionMessage("error.grant.nofundingsource.nonfederal"));
			}
		}
		return errors;
	}
	
	/**
	 * @return the fundingType
	 */
	public String getFundingType() {
		return fundingType;
	}
	/**
	 * @param fundingType the fundingType to set
	 */
	public void setFundingType(String fundingType) {
		this.fundingType = fundingType;
	}
	/**
	 * @return the fundingAgencyName
	 */
	public String getFundingAgencyName() {
		return fundingAgencyName;
	}
	/**
	 * @param fundingAgencyName the fundingAgencyName to set
	 */
	public void setFundingAgencyName(String fundingAgencyName) {
		this.fundingAgencyName = fundingAgencyName;
	}
	/**
	 * @return the PI
	 */
	public int getPI() {
		return PI;
	}
	/**
	 * @param PI the PI to set
	 */
	public void setPI(int PI) {
		this.PI = PI;
	}
	/**
	 * @return the grantNumber
	 */
	public String getGrantNumber() {
		return grantNumber;
	}
	/**
	 * @param grantNumber the grantNumber to set
	 */
	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}
	/**
	 * @return the grantAmount
	 */
	public String getGrantAmount() {
		return grantAmount;
	}
	/**
	 * @param grantAmount the grantAmount to set
	 */
	public void setGrantAmount(String grantAmount) {
		this.grantAmount = grantAmount;
	}
	/**
	 * @return the grantTitle
	 */
	public String getGrantTitle() {
		return grantTitle;
	}
	/**
	 * @param grantTitle the grantTitle to set
	 */
	public void setGrantTitle(String grantTitle) {
		this.grantTitle = grantTitle;
	}

	/**
	 * @return the fedFundingAgencyName
	 */
	public String getFedFundingAgencyName() {
		return fedFundingAgencyName;
	}

	/**
	 * @param fedFundingAgencyName the fedFundingAgencyName to set
	 */
	public void setFedFundingAgencyName(String fedFundingAgencyName) {
		this.fedFundingAgencyName = fedFundingAgencyName;
	}
	
	public String getFundingSourceName() {
		if (FundingSourceType.isFederal(fundingType))
			return fedFundingAgencyName;
		else
			return fundingAgencyName;
	}

	/**
	 * @return the grantID
	 */
	public int getGrantID() {
		return grantID;
	}

	/**
	 * @param grantID the grantID to set
	 */
	public void setGrantID(int grantID) {
		this.grantID = grantID;
	}
}
