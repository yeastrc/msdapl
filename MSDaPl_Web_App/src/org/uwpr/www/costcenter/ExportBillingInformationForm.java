/**
 * ExportBillingInformationForm.java
 * @author Vagisha Sharma
 * Jun 18, 2011
 */
package org.uwpr.www.costcenter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * 
 */
public class ExportBillingInformationForm extends ActionForm {

	
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	private String startDateString;
	private String endDateString;
	
	private String projectIdString;
	
	private boolean exportInvoice = false;
	private boolean summarize = false; 
	
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();
		
		java.util.Date sdate = null;
		try {
			sdate = getStartDate();
		}
		catch(ParseException e) {
			errors.add("costcenter", new ActionMessage("error.costcenter.invaliddata", "Invalid start date: "+startDateString));
		}
		
		java.util.Date edate = null;
		try {
			edate = getEndDate();
		}
		catch(ParseException e) {
			errors.add("costcenter", new ActionMessage("error.costcenter.invaliddata", "Invalid end date: "+endDateString));
		}
		
		if(edate.before(sdate)) {
			errors.add("costcenter", new ActionMessage("error.costcenter.invaliddata", "Start date cannot be after end date. Selected start date: "+startDateString));
		}
		return errors;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		exportInvoice = false;
		summarize = false;
	}
	
	public java.util.Date getStartDate() throws ParseException {
		return dateFormat.parse(startDateString);
	}

	public void setStartDate(java.util.Date startDate) {
		this.startDateString = dateFormat.format(startDate);
	}

	public String getStartDateString() {
		return startDateString;
	}

	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
	}

	public java.util.Date getEndDate() throws ParseException {
		return dateFormat.parse(endDateString);
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDateString = dateFormat.format(endDate);
	}

	public String getEndDateString() {
		return endDateString;
	}

	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
	}

	public String getProjectIdString() {
		return projectIdString;
	}

	public void setProjectIdString(String projectIdString) {
		this.projectIdString = projectIdString;
	}

	public int getProjectId() {
		try {
			return Integer.parseInt(projectIdString);
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}

	public void setProjectId(int projectId) {
		this.projectIdString = String.valueOf(projectId);
	}

	public boolean isExportInvoice() {
		return exportInvoice;
	}

	public void setExportInvoice(boolean exportInvoice) {
		this.exportInvoice = exportInvoice;
	}

	public boolean isSummarize() {
		return summarize;
	}

	public void setSummarize(boolean summarize) {
		this.summarize = summarize;
	}
}
