/**
 * ViewTimeBlocksAction.java
 * @author Vagisha Sharma
 * May 1, 2011
 */
package org.uwpr.www.costcenter;

import java.util.Collections;
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
import org.uwpr.costcenter.InstrumentRateComparator;
import org.uwpr.costcenter.InstrumentRateDAO;
import org.uwpr.costcenter.RateType;
import org.uwpr.costcenter.RateTypeDAO;
import org.uwpr.costcenter.TimeBlock;
import org.uwpr.costcenter.TimeBlockDAO;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewInstrumentRatesAction extends Action {

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
        
        ViewInstrumentRatesForm filterForm = (ViewInstrumentRatesForm) form;
        
        List<InstrumentRate> instrumentRates = InstrumentRateDAO.getInstance().getRates(
												        		filterForm.getInstrumentId(),
												        		filterForm.getRateTypeId(),
												        		filterForm.getTimeBlockId(),
												        		filterForm.getCurrent());
        
        // sort by instrumentID, time block ID and then rate type ID
        Collections.sort(instrumentRates, new InstrumentRateComparator());
        
        
        List<MsInstrument> instrumentList = MsInstrumentUtils.instance().getMsInstruments();
        List<RateType> rateTypeList = RateTypeDAO.getInstance().getAllRateTypes();
        List<TimeBlock> timeBlockList = TimeBlockDAO.getInstance().getAllTimeBlocks();
        
        
        request.setAttribute("instrumentRates", instrumentRates);
        request.setAttribute("instrumentList", instrumentList);
        request.setAttribute("rateTypeList", rateTypeList);
        request.setAttribute("timeBlockList", timeBlockList);
        return mapping.findForward("Success");
	}
}
