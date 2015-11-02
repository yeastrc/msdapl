/**
 * ViewProteinInferenceJob.java
 * @author Vagisha Sharma
 * Feb 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;


import java.util.List;

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
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.www.proteinfer.ProteinInferToProjectMapper;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewProteinInferenceJob extends Action {

    private static final Logger log = Logger.getLogger(ViewProteinInferenceJob.class);
    
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

        // Restrict access to users that are members of a group
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isInAGroup(user.getResearcher().getID())) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
        }
        
        // get the protein inference id
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // get the project id
        int projectId = 0;
        try {projectId = Integer.parseInt(request.getParameter("projectId"));}
        catch(NumberFormatException e){};
        
        // If there is not project ID in the request parameters get it from the database
        if(projectId == 0) {
        	List<Integer> projectIds = ProteinInferToProjectMapper.map(pinferId);
        	if(projectIds != null && projectIds.size() > 0)
        		projectId = projectIds.get(0); // get the first one
        }
        
        // Error if we still don't have a project ID
        if(projectId == 0) {
            log.error("Invalid project id: "+projectId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.project.noprojectid"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        request.setAttribute("projectId", projectId);
        
        
        // load the job
        ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(pinferId);
        if(job == null) {
            log.error("No Protein Inference Job found with protein inference ID: "+pinferId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        request.setAttribute("pinferJob", job);
        
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        ProteinferRun pinferRun = fact.getProteinferRunDao().loadProteinferRun(pinferId);
        if(pinferRun == null) {
            log.error("No Protein Inference RUN found with ID: "+pinferId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        else {
            request.setAttribute("program", pinferRun.getProgram());
            IdPickerRun idpRun = fact.getIdPickerRunDao().loadProteinferRun(pinferId);
            request.setAttribute("params", idpRun.getParams());
            
            List<WIdPickerInputSummary> inputList = IdPickerResultsLoader.getIDPickerInputSummary(pinferId);
            request.setAttribute("inputList", inputList);
        }
        
        // Go!
        return mapping.findForward("Success");
    }
}
