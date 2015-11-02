/**
 * EditProjectInstrumentTimeForm.java
 * @author Vagisha Sharma
 * Jan 6, 2012
 */
package org.uwpr.www.scheduler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.scheduler.PatternToDateConverter;
import org.uwpr.scheduler.SchedulerException;

/**
 * 
 */
public class EditProjectInstrumentTimeForm extends ActionForm{

	private int projectId;
	private int instrumentId;
	private String instrumentName;
	private String usageBlockIdsToEdit;
	private int creatorId;
	private String createDate;
	private int updaterId;
	private String updateDate;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private Date startDateDate;
	private Date endDateDate;
	
	/** 
	 * Method validate
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();
		
		try {
			startDateDate = PatternToDateConverter.convert(startDate, startTime);
		}
		catch(SchedulerException e) {
			errors.add("costcenter", new ActionMessage("error.costcenter.invaliddata", "Invalid start date: "+startDate));
		}
		
		try {
			endDateDate = PatternToDateConverter.convert(endDate, endTime);
		}
		catch(SchedulerException e) {
			errors.add("costcenter", new ActionMessage("error.costcenter.invaliddata", "Invalid end date: "+endDate));
		}
		
		if(startDateDate != null && endDateDate != null) {
			if(!endDateDate.after(startDateDate)) {
				errors.add("costcenter", new ActionMessage("error.costcenter.invaliddata", "End date has to be after start date."));
			}
		}
		
		return errors;
	}

	public Date getStartDateDate() {
		return this.startDateDate;
	}
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Date getEndDateDate() {
		return this.endDateDate;
	}
	
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getUsageBlockIdsToEdit() {
		return usageBlockIdsToEdit;
	}

	public void setUsageBlockIdsToEdit(String usageBlockIdsToEdit) {
		this.usageBlockIdsToEdit = usageBlockIdsToEdit;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(int instrumentId) {
		this.instrumentId = instrumentId;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getUpdaterId() {
		return updaterId;
	}

	public void setUpdaterId(int updaterId) {
		this.updaterId = updaterId;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
}
