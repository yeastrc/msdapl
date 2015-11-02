/**
 * ViewAllInstrumentsAvailabilityAction.java
 * @author Vagisha Sharma
 * May 21, 2009
 * @version 1.0
 */
package org.uwpr.www.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.instrumentlog.InstrumentColors;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectIDComparator;
import org.yeastrc.project.ProjectsSearcher;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewAllInstrumentsCalendarAction extends Action {

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
            HttpServletRequest request, HttpServletResponse response) throws Exception{
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }
        
        // get a list of ms instruments
        List <MsInstrument> instruments = MsInstrumentUtils.instance().getMsInstruments(true); // active only
        Collections.sort(instruments, new Comparator<MsInstrument>() {
            public int compare(MsInstrument o1, MsInstrument o2) {
                return o1.getID() > o2.getID() ? 1 : (o1.getID() == o2.getID() ? 0 : -1);
            }});
        
        List<InstrumentNameColor> instrumentNameColors = new ArrayList<InstrumentNameColor>(instruments.size());
        
        for (MsInstrument instrument: instruments) {
        	
        	InstrumentNameColor item = new InstrumentNameColor();
        	item.setInstrumentId(instrument.getID());
        	item.setName(instrument.getName());
        	
        	item.setActive(instrument.isActive());
        	item.setColor(InstrumentColors.getColor(instrument.getID()));
        	instrumentNameColors.add(item);
        }
        request.setAttribute("instruments", instrumentNameColors);
        
        // If the user making the request is an admin put a list of projects 
        // to be displayed in a drop down list
        Groups groupMan = Groups.getInstance();
        if (groupMan.isMember(user.getResearcher().getID(), "administrators")) {
        	
        	ProjectsSearcher projSearcher = new ProjectsSearcher();
        	List <Project> projects = projSearcher.search();
        	Collections.sort(projects, new ReverseComparator(new ProjectIDComparator()));
        	request.setAttribute("projects", projects);
        }
        else
        {
        	ProjectsSearcher projSearcher = new ProjectsSearcher();
        	projSearcher.setResearcher(user.getResearcher());
        	projSearcher.setCheckWriteAccess(true); // only want projects where user is listed as a researcher.
        	List <Project> projects = projSearcher.search();
        	Collections.sort(projects, new ReverseComparator(new ProjectIDComparator()));
        	request.setAttribute("projects", projects);
        }
        
        return mapping.findForward("Success");
    }
    
    public static final class InstrumentNameColor {
    	
    	private int instrumentId;
    	private String name;
    	private boolean active;
    	private String color;
    	
    	
		public int getInstrumentId() {
			return instrumentId;
		}
		public void setInstrumentId(int instrumentId) {
			this.instrumentId = instrumentId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
    }
}
