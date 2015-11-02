/**
 * DatasetSelectionFormAction.java
 * @author Vagisha Sharma
 * Jun 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
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
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.proteinfer.ProteinInferToProjectMapper;
import org.yeastrc.www.proteinfer.job.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.job.ProteinferJob;
import org.yeastrc.www.taglib.HistoryTag;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DatasetSelectionFormAction extends Action {

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

        
        // If a protein inference run id was sent with the request get it now
        List<Integer> inputPiRunIds = new ArrayList<Integer>();
        try {
            String idStr = request.getParameter("piRunIds");
            if(idStr != null) {
                String[] tokens = idStr.split(",");
                for(String tok: tokens)
                    inputPiRunIds.add(Integer.parseInt(tok.trim()));
            }
        }
        catch(Exception e) {}
        
        // Get a list of selected projectIds
        List<Integer> projectIds = new ArrayList<Integer>();
        try {
            String idStr = request.getParameter("projectIds");
            if(idStr != null) {
                String[] tokens = idStr.split(",");
                for(String tok: tokens)
                	projectIds.add(Integer.parseInt(tok.trim()));
            }
        }
        catch(Exception e) {}
        
        for(Integer pinferId: inputPiRunIds) {
        	List<Integer> mappedIds = ProteinInferToProjectMapper.map(pinferId);
        	for(Integer projectId: mappedIds) {
        		if(projectIds.contains(projectId))
        			continue;
        		else
        			projectIds.add(projectId);
        	}
        }
        
        // For each experiment in the project get a list of the protein inference id
        List<ProteinferRunFormBean> piRuns = new ArrayList<ProteinferRunFormBean>();
        
        ProjectExperimentDAO prExpDao = ProjectExperimentDAO.instance();
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        
        Collections.sort(projectIds);
        
        for(Integer projectId: projectIds) {
            
        	Project project = ProjectDAO.instance().load(projectId);
        	
            // Get the protein inference runs for this project
            List<Integer> experimentIds = prExpDao.getExperimentIdsForProject(project.getID());
            
            for(int experimentId: experimentIds) {
                
                List<Integer> exptPiRunIds = ProteinInferJobSearcher.getInstance().getProteinferIdsForMsExperiment(experimentId);
                Collections.sort(exptPiRunIds);
                for(int piRunId: exptPiRunIds) {
                    
                    ProteinferRun run = runDao.loadProteinferRun(piRunId);
                    ProteinferRunFormBean bean = new ProteinferRunFormBean(run, project.getID());
                    
                    if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {
                        ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(piRunId);
                        if(job.getStatus() != JobUtils.STATUS_COMPLETE)
                            continue;
                    }
                    if(inputPiRunIds.contains(piRunId))
                        bean.setSelected(true);
                    piRuns.add(bean);
                }
                
            }
           
        }
        
        boolean groupProteins = Boolean.parseBoolean(request.getParameter("groupProteins"));
        request.setAttribute("datasetList", piRuns);
        request.setAttribute("groupProteins", groupProteins);
//        DatasetSelectionForm myForm = (DatasetSelectionForm)form;
//        myForm.setProteinferRunList(piRuns);
//        myForm.setGroupProteins(groupProteins);
        
        // Don't add to history
        request.setAttribute(HistoryTag.NO_HISTORY_ATTRIB, true); // We don't want this added to history.
        return mapping.findForward("Success");
    }
}
