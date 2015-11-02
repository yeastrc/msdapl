/**
 * EditInstrumentRateAction.java
 * @author Vagisha Sharma
 * Jun 13, 2011
 */
package org.uwpr.www.costcenter;

import java.util.List;

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
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class EditInstrumentRateAction extends Action{

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
        
        int instrumentRateId = 0;
        try {
        	instrumentRateId = Integer.parseInt(request.getParameter("instrumentRateId"));
        }
        catch(NumberFormatException e) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("costcenter", new ActionMessage("error.costcenter.invalidid", " Invalid instrument rate ID: "+instrumentRateId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        
        InstrumentRate instrumentRate = InstrumentRateDAO.getInstance().getInstrumentRate(instrumentRateId);
        if(instrumentRate == null) {
        	
        	ActionErrors errors = new ActionErrors();
        	errors.add("costcenter", new ActionMessage("error.costcenter.invalidid", "No entry found for instrument rate ID: "+instrumentRateId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        // If the instrument rate has been assigned to a usage block the only field that can be edited is "isCurrent".
        if(!InstrumentUsageDAO.getInstance().hasInstrumentUsageForInstrumentRate(instrumentRateId)) {
        	request.setAttribute("fullEditable", true);
        }
        request.setAttribute("instrumentRate", instrumentRate);
        
        InstrumentRateForm instrumentRateForm = (InstrumentRateForm) form;
        request.setAttribute("instrumentRateForm", instrumentRateForm);
        
        instrumentRateForm.setInstrumentRateId(instrumentRate.getId());
        instrumentRateForm.setInstrumentId(instrumentRate.getInstrument().getID());
        instrumentRateForm.setTimeBlockId(instrumentRate.getTimeBlock().getId());
        instrumentRateForm.setRateTypeId(instrumentRate.getRateType().getId());
        instrumentRateForm.setRate(instrumentRate.getRate());
        instrumentRateForm.setCurrent(instrumentRate.isCurrent());
        
        List<TimeBlock> currentTimeBlocks = TimeBlockDAO.getInstance().getAllTimeBlocks();
        request.getSession().setAttribute("timeBlocks", currentTimeBlocks);
        
        List<RateType> rateTypes = RateTypeDAO.getInstance().getAllRateTypes();
        request.getSession().setAttribute("rateTypes", rateTypes);
        
        List<MsInstrument> instruments = MsInstrumentUtils.instance().getMsInstruments(true);
        request.getSession().setAttribute("instruments", instruments);
        
        return mapping.findForward("Success");
        
	}
}
