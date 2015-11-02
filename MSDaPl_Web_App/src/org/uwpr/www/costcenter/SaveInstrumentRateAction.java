/**
 * ViewTimeBlocksAction.java
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
public class SaveInstrumentRateAction extends Action {

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
        	
        	InstrumentRate rate = new InstrumentRate();
        	rate.setId(rateForm.getInstrumentRateId());
        	rate.setCurrent(rateForm.isCurrent());
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

        	// load the exiting information for this instrumentRateId
        	InstrumentRate rateFromDb = InstrumentRateDAO.getInstance().getInstrumentRate(rate.getId());
        	
        	// if any of the fields other than "isCurrent" have been changed make sure
    		// this instrument rate is not already associated with a usage block
        	boolean changed = false;
        	if(!rate.getRate().equals(rateFromDb.getRate())) 
        		changed = true;
        	else if(rate.getInstrument().getID() != rateFromDb.getInstrument().getID())
        		changed = true;
        	else if(rate.getTimeBlock().getId() != rateFromDb.getTimeBlock().getId())
        		changed = true;
        	else if(rate.getRateType().getId() != rateFromDb.getRateType().getId())
        		changed = true;
        	
        	if(changed) {
        		
        		if(InstrumentUsageDAO.getInstance().hasInstrumentUsageForInstrumentRate(rate.getId())) {
                	
                	ActionErrors errors = new ActionErrors();
                	errors.add("costcenter", new ActionMessage("error.costcenter.save", "Cannot update instrument rate ID: "+rate.getId()+
                			". It is associated with usage block(s)"));
                    saveErrors( request, errors );
                    return mapping.findForward("Failure");
                }
        	}
        	

        	// If there is already a rate for this instrumentID+timeBlockID+rateTypeID that is current
        	// then this one cannot be marked current
        	InstrumentRate rateForSameCombo = InstrumentRateDAO.getInstance().getInstrumentCurrentRate(rate.getInstrument().getID(), 
        			rate.getTimeBlock().getId(), rate.getRateType().getId());
        	
        	if(rateForSameCombo != null && 
        	   rateForSameCombo.getId() != rate.getId() && 
        	   rateForSameCombo.isCurrent() && rate.isCurrent()) {
        		ActionErrors errors = new ActionErrors();
            	errors.add("costcenter", new ActionMessage("error.costcenter.save", "Cannot mark rate current."+
            			". A current rate already exists for this instrument, time block and rate type."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
        	}
        	
        	// update
        	InstrumentRateDAO.getInstance().updateInstrumentRate(rate);
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
