/**
 * RequestProjectInstrumentTimeAjaxAction.java
 * @author Vagisha Sharma
 * May 28, 2011
 */
package org.uwpr.www.scheduler;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.uwpr.costcenter.InstrumentRate;
import org.uwpr.costcenter.InstrumentRateDAO;
import org.uwpr.costcenter.RateType;
import org.uwpr.costcenter.RateTypeDAO;
import org.uwpr.costcenter.TimeBlock;
import org.uwpr.costcenter.TimeBlockDAO;
import org.uwpr.instrumentlog.DateUtils;
import org.uwpr.instrumentlog.InstrumentUsageDAO;
import org.uwpr.instrumentlog.InstrumentUsagePayment;
import org.uwpr.instrumentlog.InstrumentUsagePaymentDAO;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.uwpr.scheduler.InstrumentAvailabilityChecker;
import org.uwpr.scheduler.PatternToDateConverter;
import org.uwpr.scheduler.SchedulerException;
import org.uwpr.scheduler.UsageBlockBaseWithRate;
import org.uwpr.scheduler.UsageBlockPaymentInformation;
import org.uwpr.scheduler.UsageBlockRepeatBuilder;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.payment.PaymentMethod;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class RequestProjectInstrumentTimeAjaxAction extends Action{

	private static final Logger log = Logger.getLogger(RequestProjectInstrumentTimeAjaxAction.class);
	
	private static final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		
		response.setContentType("application/json");
		
        // we need a projectID
        int projectId = 0;
        try {
        	projectId = Integer.parseInt(request.getParameter("projectId"));
        }
        catch(NumberFormatException e) {
        	projectId = 0;
        }
        if(projectId == 0) {
        	return sendError(response,"Invalid projectID: "+projectId+" in request");
        }
        
        // Make sure the user has access to the project
        Project project = null;
        try {
        	project = ProjectFactory.getProject(projectId);
        	if(!project.checkAccess(user.getResearcher())) {
        		return sendError(response,"User does not have access to schedule instrument time for project");
        	}
        }
        catch(Exception e) {
        	log.error("Error checking access to project ID: "+projectId, e);
        	return sendError(response,"Error loading project to check access. "+e.getMessage());
        }
        
        
        // we need an instrumentID
        int instrumentId = 0;
        try {
        	instrumentId = Integer.parseInt(request.getParameter("instrumentId"));
        }
        catch(NumberFormatException e) {
        	instrumentId = 0;
        }
        
        
        // get a list of ms instruments
        List <MsInstrument> instruments = MsInstrumentUtils.instance().getMsInstruments();
        Collections.sort(instruments, new Comparator<MsInstrument>() {
            public int compare(MsInstrument o1, MsInstrument o2) {
                return o1.getID() > o2.getID() ? 1 : (o1.getID() == o2.getID() ? 0 : -1);
        }});
        
        boolean found = false;
        for(MsInstrument instrument: instruments) {
        	if(instrument.getID() == instrumentId) {
        		found = true;
        		break;
        	}
        }
        if(!found) {
        	return sendError(response,"Invalid instrumentID: "+instrumentId+" in request");
        }
        
        
        // Get the rate type -- UW, non-profit, commercial etc.
        RateType rateType = RateTypeDAO.getInstance().getRateTypeForAffiliation(project.getAffiliation());
        
        
        // Get the start and end date and time
        String startDate = request.getParameter("startDate");
        String startTime = request.getParameter("startTime");
        String endDate = request.getParameter("endDate");
        String endTime = request.getParameter("endTime");
        
        
        Date rangeStartDate = null;
        try {
        	rangeStartDate = PatternToDateConverter.convert(startDate, startTime);
        }
        catch(SchedulerException e) {
        	return sendError(response,"Error reading start date. Error was: "+e.getMessage());
        }
        
        Date rangeEndDate = null;
        try {
        	rangeEndDate = PatternToDateConverter.convert(endDate, endTime);
        }
        catch(SchedulerException e) {
        	return sendError(response,"Error reading end date. Error was: "+e.getMessage());
        }
        
        if(!rangeEndDate.after(rangeStartDate))
        {
        	return sendError(response,"Start date has to be before the end date.");
        }
        
        double hoursInCurrentRange = DateUtils.getNumHours(rangeStartDate, rangeEndDate);
        
        
        // Split the given range into time blocks
        List<TimeBlock> timeBlocks = TimeBlockDAO.getInstance().getAllTimeBlocks();
        if(timeBlocks.size() != 1)
        {
        	String msg = "Expected 1 time block.  Found "+timeBlocks.size();
    		return sendError(response,msg);
        }
        
        
        // Has the user checked the "Repeat Daily" checkbox?
    	boolean repeatDaily = Boolean.parseBoolean(request.getParameter("repeatdaily"));
    	if(repeatDaily && hoursInCurrentRange > 24) {
    		return sendError(response,"Selected time range exceeds 24 hours and cannot be repeated daily.");
    	}
        
    	
    	// get the instrumentRateID
    	TimeBlock timeBlock = timeBlocks.get(0);
    	InstrumentRate rate = InstrumentRateDAO.getInstance().getInstrumentCurrentRate(instrumentId, timeBlock.getId(), rateType.getId());
    	if(rate == null) {
    		return sendError(response,"No rate information found for instrumentId: "+instrumentId+
    				" and timeBlockId: "+timeBlock.getId()+" and rateTypeId: "+rateType.getId()+" in request");
    	}

    	List<UsageBlockBaseWithRate> allBlocks = new ArrayList<UsageBlockBaseWithRate>();
    	
    	UsageBlockBaseWithRate usageBlock = new UsageBlockBaseWithRate();
    	usageBlock.setProjectID(projectId);
    	usageBlock.setInstrumentID(instrumentId);
    	usageBlock.setInstrumentRateID(rate.getId());
    	usageBlock.setResearcherID(user.getResearcher().getID());
    	usageBlock.setStartDate(rangeStartDate);
    	usageBlock.setEndDate(rangeEndDate);
    	usageBlock.setRate(rate);

    	allBlocks.add(usageBlock);
            	
        
        // Is this block being repeated on a daily basis?
    	if(repeatDaily) {
        	
        	String repeatEndDateString = request.getParameter("repeatenddate");
    		if(repeatEndDateString == null || repeatEndDateString.trim().length() == 0) {
    			return sendError(response, "Repeat was checked but no end date was specified");
    		}
    		Date repeatEndDate = null;
    		try {
    			repeatEndDate = PatternToDateConverter.parseDate(repeatEndDateString);
    		}
    		catch(ParseException e) {
    			return sendError(response, "Error parsing repeat end date: "+repeatEndDate);
    		}
    		
    		UsageBlockBaseWithRate block = allBlocks.get(0);
    		try {
    			allBlocks = UsageBlockRepeatBuilder.getInstance().repeatDaily(block, repeatEndDate);
    		}
    		catch(SchedulerException e) {
    			return sendError(response, "Could not create repeating events. Error was:: "+e.getMessage());
    		}
        }
    	
    	
		// Check if the instrument is available
    	for(UsageBlockBase block: allBlocks) {
    		if(!InstrumentAvailabilityChecker.getInstance().isInstrumentAvailable(instrumentId, block.getStartDate(), block.getEndDate())) {

    			return sendError(response, "Instrument is busy at the requested time between "
    					+block.getStartDateFormated() + " and "+block.getEndDateFormated());
    		}
    	}
		
        
    	// Save the blocks
		String errorMessage = saveUsageBlocksForProject(request, response, allBlocks);
		if(errorMessage != null)
			return sendError(response, errorMessage);
		
		
    	// Write the response
        PrintWriter writer = response.getWriter();
        writer.write(getJSONSuccess(allBlocks));
        return null;
	}

	private static String saveUsageBlocksForProject(HttpServletRequest request, HttpServletResponse response, 
			List<? extends UsageBlockBase> usageBlocks) throws Exception {
		
		
		// Get the payment method(s)
		UsageBlockPaymentInformation paymentInfo = new UsageBlockPaymentInformation(usageBlocks.get(0).getProjectID());
		
		String method1IdString = request.getParameter("paymentMethodId1");
		if(method1IdString == null) {
			return "No payment method found in request";
		}
		String method1Perc = request.getParameter("paymentMethod1Percent");
		if(method1Perc == null) {
			return "Percent to be billed to payment method 1 not found in request";
		}
		try {
			paymentInfo.add(method1IdString, method1Perc);
		}
		catch(SchedulerException e) {
			return e.getMessage();
		}
		
        if(request.getParameter("paymentMethodId2") != null && !(request.getParameter("paymentMethodId2").equals("0"))) {
        	
        	if(request.getParameter("paymentMethod2Percent") == null) {
        		return "Percent to be billed to payment method 2 not found in request";
        	}
        	try {
        		paymentInfo.add(request.getParameter("paymentMethodId2"), request.getParameter("paymentMethod2Percent"));
        	}
    		catch(SchedulerException e) {
    			return e.getMessage();
    		}
        }
        
        
        return saveUsageBlocksForProject(usageBlocks, paymentInfo);
	}



	public static String saveUsageBlocksForProject(
			List<? extends UsageBlockBase> usageBlocks,
			UsageBlockPaymentInformation paymentInfo) {
		
		List<Integer> savedBlockIds = new ArrayList<Integer>();
        
        InstrumentUsagePaymentDAO iupDao = InstrumentUsagePaymentDAO.getInstance();
        
        for(UsageBlockBase block: usageBlocks) {

        	log.info("Saving usage block: "+block.toString());
        	
        	try {

        		// save to the instrumentUsage table
            	InstrumentUsageDAO.getInstance().save(block);
            	savedBlockIds.add(block.getID());
            	
        		for(int i = 0; i < paymentInfo.getCount(); i++) {

        			PaymentMethod pm = paymentInfo.getPaymentMethod(i);
        			BigDecimal perc = paymentInfo.getPercent(i);

        			InstrumentUsagePayment usagePayment = new InstrumentUsagePayment();
        			usagePayment.setInstrumentUsageId(block.getID());
        			usagePayment.setPaymentMethod(pm);
        			usagePayment.setPercent(perc);
        			iupDao.savePayment(usagePayment);
        		}
        	}
        	catch(Exception e) {
        		
        		// delete the usage blocks saved thus far and throw an error
        		for(Integer blockId: savedBlockIds) {
        			try {
        				InstrumentUsageDAO.getInstance().delete(blockId);
        			}
        			catch(Exception ex) {
            			log.error("There was an error deleting block ID: "+blockId);
            		}
        		}
        		
        		log.error("Error saving payment information for usage", e);
        		return "There was an error saving payment information for the scheduled time. Error was: "+e.getMessage();
        	}
        }
		return null;
	}
	
	private ActionForward sendError(HttpServletResponse response, String errorMessage) throws IOException {
		PrintWriter responseWriter = response.getWriter();
		responseWriter.write(getJSONError(errorMessage));
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return null;
	}
	
	private String getJSONError(String errorMessage) {
		
		JSONObject json = new JSONObject();
		json.put("message", errorMessage);
		json.put("response_type", "ERROR");
		return json.toJSONString();
	}
	
	private String getJSONSuccess(List<UsageBlockBaseWithRate> usageBlocks) {
		
		JSONObject json = new JSONObject();
		json.put("response_type", "SUCCESS");
		
		JSONArray array = new JSONArray();
		json.put("blocks", array);
		for(UsageBlockBaseWithRate block: usageBlocks) {
			JSONObject obj = new JSONObject();
			obj.put("id", String.valueOf(block.getID()));
			obj.put("fee", String.valueOf(block.getFee()));
			obj.put("start_date", block.getStartDateFormated());
			obj.put("end_date", block.getEndDateFormated());
			array.add(obj);
		}
		// log.info(json.toJSONString());
		return json.toJSONString();
	}
}
