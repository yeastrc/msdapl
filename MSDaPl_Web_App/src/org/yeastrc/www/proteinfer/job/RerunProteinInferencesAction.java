/**
 * 
 */
package org.yeastrc.www.proteinfer.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.www.proteinfer.ProteinInferToProjectMapper;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * RerunProteinInferencesAction.java
 * @author Vagisha Sharma
 * Jun 7, 2010
 * 
 */
public class RerunProteinInferencesAction extends Action {

	private static final Logger log = Logger.getLogger("protinferRerunLog");
    
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

        log.info("Got request to re-run protein inferences");
       
        // Restrict access to admins
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
        }
        
        FormFile file = ((RerunProteinInferenceForm)form).getInputFile();
        boolean deleteOld = ((RerunProteinInferenceForm)form).isDeleteOriginal();
        
        
        if(file == null || file.getFileName().trim().length() == 0) {
        	ActionErrors errors = new ActionErrors();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", "No input file with protein inference IDs to re-run"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        List<Integer> piRunIds = null;
        try {piRunIds = getPiRunIds(file.getInputStream());}
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", e.getMessage()));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Make sure these exist and are IDPicker runs
        for(Integer piRunId: piRunIds) {
        	
        	ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(piRunId);
            if(job == null) {
            	log.error("No job found for piRunID: "+piRunId);
            	ActionErrors errors = new ActionErrors();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", "No job found for piRunID: "+piRunId));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            IdPickerRun run = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(piRunId);
    		if(run == null) {
    			log.error("Could not find protein inference run for piRunID: "+piRunId);
    			ActionErrors errors = new ActionErrors();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", "Not a IDPicker run;  piRunID: "+piRunId));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
    		}
            
        }
        
        List<RerunEntry> rerunEntries = new ArrayList<RerunEntry>(piRunIds.size());
        for(Integer piRunId: piRunIds) {
        	
        	RerunEntry entry = new RerunEntry();
        	entry.setOldPiRunId(piRunId);
        	rerunEntries.add(entry);
        	
        	// get the project ID
        	List<Integer> projectIds = ProteinInferToProjectMapper.map(piRunId);
        	// Is this still associated with a project? 
        	if(projectIds == null || projectIds.size() == 0) {
        		continue; // no need to re-run this one.
        	}
        	
        	entry.setProjectId(projectIds.get(0)); // just set the first one
        	
        	// copy parameters for the old run and queue a new job
        	int jobId = ProteinInferenceRerunner.reRun(piRunId, deleteOld);
        	
        	// load the new job and get the new protein inference ID
        	ProteinferJob job = ProteinInferJobSearcher.getInstance().getJob(jobId);
        	entry.setNewPiRunId(job.getPinferId());
        	
        }
        
        request.setAttribute("rerunEntries", rerunEntries);
        // Go!
        return mapping.findForward( "Success" ) ;
    }

	private List<Integer> getPiRunIds(InputStream inputStream) throws Exception {
		
		BufferedReader reader = null;
		List<Integer> piRunIds = new ArrayList<Integer>();
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while((line = reader.readLine()) != null) {
				
				try {
					int piRunId = Integer.parseInt(line.trim());
					piRunIds.add(piRunId);
				}
				catch(NumberFormatException e) {
					throw new Exception("Error parsing piRunId in line: "+line, e);
				}
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
		}
		
		return piRunIds;
	}
	
	public static final class RerunEntry {
		
		private int oldPiRunId;
		private int newPiRunId;
		private int projectId;
		
		public int getOldPiRunId() {
			return oldPiRunId;
		}
		public void setOldPiRunId(int oldPiRunId) {
			this.oldPiRunId = oldPiRunId;
		}
		public int getNewPiRunId() {
			return newPiRunId;
		}
		public void setNewPiRunId(int newJobId) {
			this.newPiRunId = newJobId;
		}
		public int getProjectId() {
			return projectId;
		}
		public void setProjectId(int projectId) {
			this.projectId = projectId;
		}
	}
}
