package org.uwpr.www.instrumentlog;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 * AllInstrumentUsageJSONAction.java
 * @author Vagisha Sharma
 * Jul 04, 2011
 * 
 * Returns the instrument usage for all instruments as a JSON object.  
 * For use with fullcalendar.
 *
 */
public class AllInstrumentUsageJSONAction extends Action {

	private static final Logger log = Logger.getLogger(AllInstrumentUsageJSONAction.class);
	
	/** 
	 * Method execute
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 * @throws Exception 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		boolean isAdmin = false;
        Groups groupMan = Groups.getInstance();
        if (groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            isAdmin = true;
        }
        
		
		java.util.Date startDate = null;
		java.util.Date endDate = null;
		
		if (request.getParameter("start") != null) {
			try {
				long start = Long.parseLong(request.getParameter("start"));
				startDate = new Date(start * 1000);
			}
			catch(NumberFormatException e) {
				return sendError(response, "Invalid start date: "+request.getParameter("start"));
			}
		}
		
		if (request.getParameter("end") != null) {
			try {
				long start = Long.parseLong(request.getParameter("end"));
				endDate = new Date(start * 1000);
			}
			catch(NumberFormatException e) {
				return sendError(response, "Invalid start date: "+request.getParameter("end"));
			}
		}
		
		String requestedInstruments = request.getParameter("instruments");
        Set<Integer> requestedInstrumentList = null;
        if(requestedInstruments != null) {
        	requestedInstrumentList = new HashSet<Integer>();
        	String[] tokens = requestedInstruments.split(",");
        	for(String token: tokens) {
        		if(token.trim().length() > 0)
        			requestedInstrumentList.add(Integer.parseInt(token));
        	}
        }
        
        
		JSONArray eventArray = null;
		try {
			if(isAdmin) {
				eventArray = JSONInstrumentUsageGetter.getInstance().getForAdmin(startDate, endDate, requestedInstrumentList);
			}
			else {
				eventArray = JSONInstrumentUsageGetter.getInstance().get(startDate, endDate, requestedInstrumentList);
			}
		}
		catch(SQLException e) {
			log.error("Error getting instrument usage.", e);
			return sendError(response, "There was a database error. Error message was: "+e.getMessage());
		}
		
		response.setContentType("application/json");
		//System.out.println(eventArray.toJSONString());
		PrintWriter out = response.getWriter();
		out.write(eventArray.toJSONString());
		out.flush();
        return null;
	}
	
	private ActionForward sendError(HttpServletResponse response, String errorMessage) throws IOException {
		response.setContentType("application/json");
		PrintWriter responseWriter = response.getWriter();
		responseWriter.write(getJSONError(errorMessage));
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		responseWriter.flush();
		return null;
	}
	
	private String getJSONError(String errorMessage) {
		
		JSONObject json = new JSONObject();
		json.put("message", errorMessage);
		json.put("response_type", "ERROR");
		return json.toJSONString();
	}
	
}
