/**
 * SaveChangeInstrumentRateAction.java
 * @author Vagisha Sharma
 * May 1, 2011
 */
package org.uwpr.www.costcenter;

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
public class SaveChangeInstrumentRateAction extends Action {

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
        
        InstrumentRateForm rateForm = (InstrumentRateForm) form;
        
        try {
        	
        	int parentInstrumentRateId = rateForm.getInstrumentRateId();
        	// mark the parent instrument rate as not current
        	InstrumentRate parentRate = InstrumentRateDAO.getInstance().getInstrumentRate(parentInstrumentRateId);
        	if(parentRate == null) {
        		ActionErrors errors = new ActionErrors();
            	errors.add("costcenter", new ActionMessage("error.costcenter.save", 
            			"Cannot mark old rate as obsolete. No instrument rate found for ID: "+parentInstrumentRateId));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
        	}
        	parentRate.setCurrent(false);
        	InstrumentRateDAO.getInstance().updateInstrumentRate(parentRate);
        	
        	// now save a new rate
        	InstrumentRate rate = new InstrumentRate();
        	rate.setCurrent(true);
        	rate.setRate(rateForm.getRate());
        	
        	// instrument
        	MsInstrument instrument = MsInstrumentUtils.instance().getMsInstrument(rateForm.getInstrumentId());
        	rate.setInstrument(instrument);
        	// rime block
        	TimeBlock timeBlock = TimeBlockDAO.getInstance().getTimeBlock(rateForm.getTimeBlockId());
        	rate.setTimeBlock(timeBlock);
        	// rate type
        	RateType rateType = RateTypeDAO.getInstance().getRateType(rateForm.getRateTypeId());
        	rate.setRateType(rateType);

        	
        	// save
        	InstrumentRateDAO.getInstance().saveInstrumentRate(rate);
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("costcenter", new ActionMessage("error.costcenter.save", e.getMessage()));
        	saveErrors( request, errors );
        	return mapping.findForward("Failure");
        }
        
        return mapping.findForward("Success");
	}
}
