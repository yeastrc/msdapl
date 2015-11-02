/**
 * ViewTimeBlocksAction.java
 * @author Vagisha Sharma
 * May 1, 2011
 */
package org.uwpr.www.costcenter;

import java.sql.SQLException;
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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewCurrentInstrumentRatesAction extends Action {

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
		
        
        List<InstrumentRate> instrumentRates = null;
		try {
			instrumentRates = InstrumentRateDAO.getInstance().getCurrentRates();
		} catch (SQLException e) {
			ActionErrors errors = new ActionErrors();
            errors.add("costcenter", new ActionMessage("error.costcenter.load", e.getMessage()));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
		}
       
        // sort by instrumentID, time block ID and then rate type ID
        Collections.sort(instrumentRates, new InstrumentRateComparator());
        
       
        
        request.setAttribute("instrumentRates", instrumentRates);
        return mapping.findForward("Success");
	}
}
