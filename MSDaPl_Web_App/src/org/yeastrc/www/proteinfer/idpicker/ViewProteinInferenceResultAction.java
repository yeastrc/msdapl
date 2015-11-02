package org.yeastrc.www.proteinfer.idpicker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;

public class ViewProteinInferenceResultAction extends Action {

    private static final Logger log = Logger.getLogger(ViewProteinInferenceResultAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        // form for filtering and display options
        IdPickerFilterForm filterForm = (IdPickerFilterForm)form;
        request.setAttribute("proteinInferFilterForm", filterForm);
        
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e){};
        
        // if we do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
       
        filterForm.setPinferId(pinferId);
        
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        
        // update the form with the parameters that were used to run protein inference
        filterForm.setMinPeptides(String.valueOf(idpParams.getMinPeptides()));
        filterForm.setMinUniquePeptides(String.valueOf(idpParams.getMinUniquePeptides()));
        filterForm.setMinCoverage(String.valueOf(idpParams.getMinCoverage()));
        
        // Some of the checkbox need to be set to true here
        filterForm.setExactPeptideMatch(true);
        
        request.setAttribute("newRequest", true);
        // Go!
        return mapping.findForward("Success");
    }
   
}
