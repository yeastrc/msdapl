package org.uwpr.www.scheduler;

import org.apache.struts.action.*;
import org.uwpr.instrumentlog.*;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ShiftTimeScheduledForInstrument.java
 * @author Vagisha Sharma
 * April 10, 2015
 *
 */
public class ShiftTimeScheduledForInstrument extends Action
{
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


        // We need an instrumentID
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
        if(startDateString == null)
        {
            ActionErrors errors = new ActionErrors();
            errors.add("scheduler", new ActionMessage("error.scheduler.missingDate", "start date"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        if(endDateString == null)
        {
            ActionErrors errors = new ActionErrors();
            errors.add("scheduler", new ActionMessage("error.scheduler.missingDate", "end date"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }

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
                ActionErrors errors = new ActionErrors();
                errors.add("scheduler", new ActionMessage("error.scheduler.invalidDate", "start date " + startDateString));
                saveErrors(request, errors);
                return mapping.findForward("standardHome");
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
                ActionErrors errors = new ActionErrors();
                errors.add("scheduler", new ActionMessage("error.scheduler.invalidDate", "end date " + endDateString));
                saveErrors(request, errors);
                return mapping.findForward("standardHome");
            }
        }

        if(endDate.before(startDate))
        {
            ActionErrors errors = new ActionErrors();
            errors.add("scheduler", new ActionMessage("error.scheduler.invalidDate",
                    "Start date cannot be after end date. Selected start date: " + startDateString + ". End date: " + endDateString));
            saveErrors(request, errors);
            return mapping.findForward("standardHome");
        }

        int shiftByDays;
        String shiftByDaysStr = request.getParameter("shiftByDays");
        try {
            shiftByDays = Integer.parseInt(shiftByDaysStr);
        }
        catch(NumberFormatException e) {
            shiftByDays = 0;
        }
        if(shiftByDays == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("scheduler", new ActionMessage("error.scheduler.general", "Invalid value for shiftByDays parameter - " + shiftByDaysStr));
            saveErrors(request, errors);
            return mapping.findForward("Failure"); // TODO: go to ViewTimeScheduledForInstrument.
        }

        java.util.List<UsageBlock> usageBlocks = MsInstrumentUtils.instance().getUsageBlocksForInstrument(instrumentId, startDate, endDate, false);
        List<UsageBlock> usageBlocksToShift = new ArrayList<UsageBlock>();

        Calendar calendar = Calendar.getInstance();
        for(UsageBlock block: usageBlocks)
        {
            if(block.getStartDate().before(startDate))
            {
                // Skip if this block has a start date before the selected start date.
                continue;
            }
            calendar.setTime(block.getStartDate());
            calendar.add(Calendar.DATE, shiftByDays);
            block.setStartDate(calendar.getTime());

            calendar.setTime(block.getEndDate());
            calendar.add(Calendar.DATE, shiftByDays);
            block.setEndDate(calendar.getTime());

            block.setUpdaterResearcherID(user.getResearcher().getID());

            usageBlocksToShift.add(block);
        }

        InstrumentUsageDAO.getInstance().updateBlocksDates(usageBlocksToShift);

        // Get the updated usage blocks
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, shiftByDays);
        startDate = calendar.getTime();
        calendar.setTime(endDate);
        calendar.add(Calendar.DATE, shiftByDays);
        endDate = calendar.getTime();
        usageBlocks = MsInstrumentUtils.instance().getUsageBlocksForInstrument(instrumentId, startDate, endDate, false);

        request.setAttribute("usageBlocks", usageBlocks);

        java.util.List<MsInstrument> instrumentList = MsInstrumentUtils.instance().getMsInstruments();
        request.setAttribute("instruments", instrumentList);


        if(usageBlocks.size() == 0
                && instrumentId == 0
                && startDate == null
                && endDate == null)
        {
            request.setAttribute("noInstrumentTimeScheduled", true);
        }

        ActionForward forward = new ActionForward();
        forward.setPath(mapping.findForward("Success").getPath() + "?instrumentId=" + instrumentId); // ViewScheduledTimeForInstrument
        forward.setRedirect(true);
        return forward;
    }
}
