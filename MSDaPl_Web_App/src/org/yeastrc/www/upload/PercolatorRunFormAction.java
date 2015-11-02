/**
 * PercolatorRunFormAction.java
 * @author Vagisha Sharma
 * Dec 10, 2010
 */
package org.yeastrc.www.upload;

import java.util.ArrayList;
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
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.proteinfer.job.ProgramParameters;
import org.yeastrc.www.upload.PercolatorRunForm.PercolatorInputFile;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PercolatorRunFormAction extends Action {

	private static final Logger log = Logger.getLogger(PercolatorRunAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        // We need a valid search ID
        int searchId = -1;
        if (request.getParameter("searchId") != null) {
            try {searchId = Integer.parseInt(request.getParameter("searchId"));}
            catch(NumberFormatException e) {searchId = -1;}
        }
        
        if (searchId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("general", new ActionMessage("error.general.invalid.id", "searchID: "+request.getParameter("searchId")));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // get the experimentID
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        int experimentId = search.getExperimentId();
        
        // get the project for this experiment
        int projectId = ProjectExperimentDAO.instance().getProjectIdForExperiment(experimentId);
        // User making the request to run protein inference should be affiliated with the project
        Project project = ProjectDAO.instance().load(projectId);
        if(!project.checkAccess(user.getResearcher())) {
             ActionErrors errors = new ActionErrors();
             errors.add("username", new ActionMessage("error.general.errorMessage", 
                     "You may run percolator only for your projects."));
             saveErrors( request, errors );
             return mapping.findForward( "Failure" );
        }
        
        
        // get a list of runSearchIDs for this search
        MsRunSearchDAO rsDao = DAOFactory.instance().getMsRunSearchDAO();
        List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchId);
        List<PercolatorInputFile> iFiles = new ArrayList<PercolatorInputFile>(runSearchIds.size());
        for(Integer runSearchId: runSearchIds) {
        	PercolatorInputFile iFile = new PercolatorInputFile();
        	iFile.setRunSearchId(runSearchId);
        	iFile.setRunName(rsDao.loadFilenameForRunSearch(runSearchId));
        	iFile.setIsSelected(true);
        	iFiles.add(iFile);
        }
        
        PercolatorRunForm myForm = new PercolatorRunForm();
        myForm.setProjectId(projectId);
        myForm.setExperimentId(experimentId);
        myForm.setSearchId(searchId);
        myForm.setInputFiles(iFiles);
        myForm.setIndividualRuns(false);
        request.setAttribute("percolatorRunForm", myForm);
        
        ProgramParameters progParams = new ProgramParameters(ProteinInferenceProgram.PROTINFER_PERC_PEPT);
        myForm.setProgramParams(progParams);
        
        // Go!
        return mapping.findForward("Success");

    }
    
}
