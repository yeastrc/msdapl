/**
 * EditProjectInstrumentTimeFormAction.java
 * @author Vagisha Sharma
 * Jan 6, 2012
 */
package org.uwpr.www.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.costcenter.InstrumentRate;
import org.uwpr.costcenter.InstrumentRateDAO;
import org.uwpr.costcenter.RateType;
import org.uwpr.costcenter.RateTypeDAO;
import org.uwpr.costcenter.TimeBlock;
import org.uwpr.costcenter.TimeBlockDAO;
import org.uwpr.instrumentlog.InstrumentUsageDAO;
import org.uwpr.instrumentlog.InstrumentUsagePayment;
import org.uwpr.instrumentlog.InstrumentUsagePaymentDAO;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.uwpr.scheduler.InstrumentAvailabilityChecker;
import org.uwpr.scheduler.UsageBlockBaseWithRate;
import org.uwpr.scheduler.UsageBlockDeletableDecider;
import org.uwpr.scheduler.UsageBlockPaymentInformation;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class EditProjectInstrumentTimeAction extends Action {

	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			return returnError(mapping, request, "username", 
					new ActionMessage("error.login.notloggedin"), "authenticate");
		}
		
		// Get the form
		EditProjectInstrumentTimeForm editForm = (EditProjectInstrumentTimeForm) form;
		
		// we need a projectID
        int projectId = editForm.getProjectId();
        
		
        // Make sure the user has access to the project
        Project project = null;
        try {
        	project = ProjectFactory.getProject(projectId);
        	
        	if(project == null) {
        		return returnError(mapping, request, "scheduler", 
        				new ActionMessage("error.scheduler.invalidid", "Project with ID: "+projectId+" not found in the database."),
        				"standardHome");
        	}
        }
        catch(Exception e) {
        	return returnError(mapping, request, "scheduler", 
        			new ActionMessage("error.costcenter.load", e.getMessage()),
    				"standardHome");
        }
        try {
        	
        	if(!project.checkAccess(user.getResearcher())) {
        		return returnError(mapping, request, "scheduler", 
        				new ActionMessage("error.costcenter.invalidaccess", 
                        		"User does not have access to edit instrument time for project "+projectId+"."),
        				"viewProject", "?ID="+projectId);
        	}
        }
        catch(Exception e) {
        	return returnError(mapping, request, "scheduler", 
        			new ActionMessage("error.costcenter.load", 
                    		"Project ID: "+projectId+". ERROR: "+e.getMessage()),
    				"viewProject", "?ID="+projectId);
        }
        
        // we need an instrumentID
        int instrumentId = editForm.getInstrumentId();
        MsInstrument instrument = null;
        try {
        	instrument = MsInstrumentUtils.instance().getMsInstrument(instrumentId);
        	if(instrument == null) {
        		return returnError(mapping, request, "scheduler", 
        				new ActionMessage("error.scheduler.invalidid", "Instrument with ID "+instrumentId+" not found in the database."),
        				"viewProject", "?ID="+projectId);
        	}
        }
        catch(Exception e) {
        	return returnError(mapping, request, "scheduler", 
        			new ActionMessage("error.costcenter.load", 
                    		"Instrument ID: "+projectId+". ERROR: "+e.getMessage()),
    				"viewProject", "?ID="+projectId);
        }
		
		// get the usageBlockIds from the request
		// get the usage block ID(s)
        List<Integer> usageBlockIds = new ArrayList<Integer>();
        String usageBlockIdString = editForm.getUsageBlockIdsToEdit();
        if(usageBlockIdString != null) {
        	String[] tokens = usageBlockIdString.split(",");
            for(String token: tokens) {

            	try {
            		usageBlockIds.add(Integer.parseInt(token));
            	}
            	catch(NumberFormatException e) {
            		return returnError(mapping, request, "scheduler", 
            				new ActionMessage("error.costcenter.invaliddata", 
                            		"Invalid usage block ID found in request: "+token+"."),
            				"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
            	}
            }
        }
        if(usageBlockIds.size() == 0) {
        	return returnError(mapping, request, "scheduler", 
        			new ActionMessage("error.costcenter.invaliddata", 
            		"No usage block IDs found in request."),
    				"standardHome");
        }
        
        
        // load the usage blocks
        List<UsageBlockBase> blocksToDelete = new ArrayList<UsageBlockBase>(usageBlockIds.size());
        for(int usageBlockId: usageBlockIds) {
        	
        	UsageBlockBase usageBlock = MsInstrumentUtils.instance().getUsageBlockBase(usageBlockId);
        	if(usageBlock == null) {
        		return returnError(mapping, request, "scheduler", 
        				new ActionMessage("error.costcenter.invaliddata", 
        						"No usage block found for usageBlockId: "+usageBlockId),
        						"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
        	}
        	blocksToDelete.add(usageBlock);
        }
        
        
        // Make sure the old blocks can be deleted.
        for(UsageBlockBase block: blocksToDelete) {
        	StringBuilder errorMessage = new StringBuilder();
        	if(!UsageBlockDeletableDecider.getInstance().isBlockDeletable(block, user, errorMessage)) {
        		return returnError(mapping, request, "scheduler", 
        				new ActionMessage("error.costcenter.delete", 
        						"Block ID "+block.getID()+": "+block.getStartDateFormated()+" - "+block.getEndDateFormated()+
        						". "+errorMessage.toString()),
        						"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
        	}
        }
        
        
        // Make sure we can create new blocks in the given time range
        Date rangeStartDate = editForm.getStartDateDate();
        Date rangeEndDate = editForm.getEndDateDate();
        // Split the given range into time blocks
        List<TimeBlock> timeBlocks = TimeBlockDAO.getInstance().getAllTimeBlocks();
        if(timeBlocks.size() != 1)
        {
        	return returnError(mapping, request, "scheduler", 
        			new ActionMessage("error.costcenter.invaliddata", 
        					"Expected 1 time block.  Found "+timeBlocks.size()),
        					"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
        }
        
        
        // Get the rate type -- internal or external.
        RateType rateType = RateTypeDAO.getInstance().getRateTypeForAffiliation(project.getAffiliation());
        if(rateType == null) {
        		return returnError(mapping, request, "scheduler", 
            			new ActionMessage("error.costcenter.invaliddata", "Could not find rate type for affiliation: "+project.getAffiliation().name()),
    							"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
        }
        
        
        List<UsageBlockBaseWithRate> allBlocks = new ArrayList<UsageBlockBaseWithRate>();
    	
    	UsageBlockBase oldBlock = blocksToDelete.get(0);
    	TimeBlock timeBlock = timeBlocks.get(0);
    	
    		
		// get the instrumentRateID
        InstrumentRate rate = InstrumentRateDAO.getInstance().getInstrumentCurrentRate(instrumentId, timeBlock.getId(), rateType.getId());
        if(rate == null) {
        	return returnError(mapping, request, "scheduler", 
        			new ActionMessage("error.costcenter.invaliddata", 
        					"No rate information found for instrumentId: "+instrumentId+
                			" and timeBlockId: "+timeBlock.getId()+" and rateTypeId: "+rateType.getId()+" in request"),
							"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
        }
        
        UsageBlockBaseWithRate usageBlock = new UsageBlockBaseWithRate();
        usageBlock.setProjectID(projectId);
        usageBlock.setInstrumentID(instrumentId);
        usageBlock.setInstrumentRateID(rate.getId());
        usageBlock.setResearcherID(oldBlock.getResearcherID());
        usageBlock.setUpdaterResearcherID(user.getResearcher().getID());
        usageBlock.setDateCreated(oldBlock.getDateCreated());
        usageBlock.setStartDate(rangeStartDate);
        usageBlock.setEndDate(rangeEndDate);
        usageBlock.setRate(rate);
            
            
        allBlocks.add(usageBlock);
        
        
        // Check if the instrument is available
    	for(UsageBlockBase block: allBlocks) {
    		if(!InstrumentAvailabilityChecker.getInstance().isInstrumentAvailable(instrumentId, 
    														block.getStartDate(), block.getEndDate(),
    														usageBlockIds)) {
    			return returnError(mapping, request, "scheduler", 
            			new ActionMessage("error.costcenter.invaliddata", 
            					"Instrument is busy at the requested time between "
            					+block.getStartDateFormated() + " and "+block.getEndDateFormated()),
    							"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
    		}
    	}
    	

        // Save the new blocks
    		
    	// get the payment method(s) for the old blocks.
    	// get the payment method(s) used for the first block.  
    	InstrumentUsagePaymentDAO paymentDao = InstrumentUsagePaymentDAO.getInstance();
    	List<InstrumentUsagePayment> paymentMethods = paymentDao.getPaymentsForUsage(blocksToDelete.get(0).getID());
    	Map<Integer, String> paymentInfoMap = new HashMap<Integer, String>();
    	for(InstrumentUsagePayment paymentMethod: paymentMethods) {
    		paymentInfoMap.put(paymentMethod.getPaymentMethod().getId(), paymentMethod.getPercent().toString());
    	}

    	// Other blocks should also have the same payment method(s).
    	for(int i = 1; i < blocksToDelete.size(); i++) {
    		List<InstrumentUsagePayment> blkPaymentMethods = paymentDao.getPaymentsForUsage(blocksToDelete.get(i).getID());

    		if(blkPaymentMethods.size() != paymentMethods.size()) {
    			return returnError(mapping, request, "scheduler", 
    					new ActionMessage("error.costcenter.invaliddata", 
    							"Number of payment method(s) for all the blocks selected are not the same. "+
    					" Blocks with different payment methods cannot be edited together."),
    					"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
    		}

    		for(InstrumentUsagePayment payment: blkPaymentMethods) {

    			int paymentMethodId = payment.getPaymentMethod().getId();
    			String percent = payment.getPercent().toString();

    			if(!paymentInfoMap.containsKey(paymentMethodId) || !paymentInfoMap.get(paymentMethodId).equals(percent)) {

    				return returnError(mapping, request, "scheduler", 
    						new ActionMessage("error.costcenter.invaliddata", 
    								"Payment method(s) for all the blocks selected are not the same. "+
    						" Blocks with different payment methods cannot be edited together."),
    						"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);

    			}
    		}
    	}

    	UsageBlockPaymentInformation paymentInfo = new UsageBlockPaymentInformation(projectId);
    	// add the payment methods to the request
    	for(Integer paymentMethodId: paymentInfoMap.keySet()) {
    		paymentInfo.add(String.valueOf(paymentMethodId), paymentInfoMap.get(paymentMethodId).toString());
    	}

    	String errorMessage = RequestProjectInstrumentTimeAjaxAction.saveUsageBlocksForProject(allBlocks, paymentInfo);
    	if(errorMessage != null)
    		return returnError(mapping, request, "scheduler", 
    				new ActionMessage("error.costcenter.invaliddata", errorMessage),
    				"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
		
    	
    	// Delete the old blocks
        for(UsageBlockBase blockToDelete: blocksToDelete) {
        	
        	try {
        		InstrumentUsageDAO.getInstance().delete(blockToDelete.getID());
        	}
        	catch(Exception e) {
        		return returnError(mapping, request, "scheduler", 
						new ActionMessage("error.costcenter.delete", e.getMessage()),
						"viewScheduler", "?projectId="+projectId+"&instrumentId="+instrumentId);
        	}
        }
        
        // set the year and month so that the scheduler initializes at the correct year and month.
        Calendar startCal = Calendar.getInstance();
    	startCal.setTime(rangeStartDate);
        request.getSession().setAttribute("scheduler_year", startCal.get(Calendar.YEAR));
        request.getSession().setAttribute("scheduler_month", startCal.get(Calendar.MONTH));
        ActionForward fwd = mapping.findForward("viewScheduler");
        return new ActionForward(fwd.getPath()+"?projectId="+projectId+"&instrumentId="+instrumentId, true);
	}

	private ActionForward returnError(ActionMapping mapping,
			HttpServletRequest request, String errProperty, ActionMessage message, String forward) {
		ActionErrors errors = new ActionErrors();
		errors.add(errProperty, message);
		saveErrors( request, errors );
		return mapping.findForward(forward);
	}
	
	private ActionForward returnError(ActionMapping mapping,
			HttpServletRequest request, String errProperty, ActionMessage message, 
			String forward, String appendToFwdPath) {
		ActionErrors errors = new ActionErrors();
		errors.add(errProperty, message);
		saveErrors( request, errors );
		ActionForward fwd = mapping.findForward(forward);
        return new ActionForward(fwd.getPath()+appendToFwdPath, fwd.getRedirect());
	}
}
