/**
 * 
 */
package org.uwpr.www.scheduler;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.uwpr.instrumentlog.DateUtils;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.UsageBlock;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * ViewTimeScheduledForInstrument.java
 * @author Vagisha Sharma
 * April 10, 2015
 * 
 */
public class ViewTimeScheduledForInstrument extends Action {

    private static final Logger log = Logger.getLogger(ViewTimeScheduledForInstrument.class);

    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

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

        // Restrict access to administrators
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
		
        
        // we need an instrumentID
        int instrumentId;
        try {
        	instrumentId = Integer.parseInt(request.getParameter("instrumentId"));
        }
        catch(NumberFormatException e) {
        	instrumentId = 0;
        }
        if(instrumentId == 0) {
        	ActionErrors errors = new ActionErrors();
            errors.add("scheduler", new ActionMessage("error.scheduler.invalidid", "Invalid instrument ID in request"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        

        String startDateString = request.getParameter("startDate");
        String endDateString = request.getParameter("endDate");
        Date startDate = null;
        Date endDate = null;
        if(startDateString != null)
        {
            try
            {
                startDate = dateFormat.parse(startDateString);
            }
            catch(ParseException e)
            {
                log.warn("Error parsing start date "+startDateString+". Setting to null.");
            }

        }
        if(endDateString != null)
        {
            try
            {
                endDate = dateFormat.parse(endDateString);
            }
            catch(ParseException e)
            {
                log.warn("Error parsing end date "+endDateString+". Setting to null.");
            }
        }
        if(endDate != null && startDate != null && endDate.before(startDate)) {
            log.warn("Start date cannot be after end date. Selected start date: "+startDateString + ". End date: "+endDateString);
        }

        if(endDate == null && startDate == null)
        {
            // If we were not given start and end dates, we will return instrument usage
            // for 90 days
            startDate = DateUtils.currentMinusDays(59);
            startDateString = dateFormat.format(startDate);
            endDate = DateUtils.currentPlusDays(30); // set time to the end of the day
            endDateString = dateFormat.format(endDate);
        }

        ViewTimeScheduledForInstrumentForm filterForm = (ViewTimeScheduledForInstrumentForm)form;
        filterForm.setInstrumentId(instrumentId);
        filterForm.setStartDateString(startDateString);
        filterForm.setEndDateString(endDateString);

        List<UsageBlock> usageBlocks = MsInstrumentUtils.instance().getUsageBlocksForInstrument(instrumentId, startDate, endDate, false);

        // sort the blocks by start date, descending
        Collections.sort(usageBlocks, new Comparator<UsageBlock>() {
			@Override
			public int compare(UsageBlock o1, UsageBlock o2) {
			    return o2.getStartDate().compareTo(o1.getStartDate());
			}
		});
        
        request.setAttribute("usageBlocks", usageBlocks);

        List<MsInstrument> instrumentList = MsInstrumentUtils.instance().getMsInstruments();
        request.setAttribute("instruments", instrumentList);

        if(usageBlocks.size() == 0
           && instrumentId == 0
           && startDate == null
           && endDate == null)
        {
            request.setAttribute("noInstrumentTimeScheduled", true);
        }
        return mapping.findForward("Success");
	}
}
