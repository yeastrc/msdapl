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
import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * DeleteProteinInferencesAction.java
 * @author Vagisha Sharma
 * Jun 7, 2010
 * 
 */
public class DeleteProteinInferencesAction extends Action {

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

        log.info("Got request to delete protein inferences");
       
        // Restrict access to admins
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
        }
        
        FormFile file = ((DeleteProteinInferenceForm)form).getInputFile();
        
        if(file == null || file.getFileName().trim().length() == 0) {
        	ActionErrors errors = new ActionErrors();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", "No input file with protein inference IDs to delete."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        List<Integer> piRunIds = null;
        try {piRunIds = getPiRunIds(file.getInputStream());}
        catch(Exception e) {
        	
        }
        
        // Make sure these exist 
        for(Integer piRunId: piRunIds) {
        	
        	ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(piRunId);
            if(job == null) {
            	ActionErrors errors = new ActionErrors();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", "No job found for piRunID: "+piRunId));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            ProteinferRun run = ProteinferDAOFactory.instance().getProteinferRunDao().loadProteinferRun(piRunId);
    		if(run == null) {
    			log.error("Could not find protein inference run for piRunID: "+piRunId);
    			ActionErrors errors = new ActionErrors();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", 
                		"Could not find run with ID: "+piRunId));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
    		}
            
        }
        
        List<Integer> deleted = new ArrayList<Integer>(piRunIds.size());
        for(Integer piRunId: piRunIds) {
        	// RERUN
        	try {
        		deleteRun(piRunId);
        		deleted.add(piRunId);
        	}
        	catch(Exception e){
        		
        	}
        }
        
        request.setAttribute("deleted", deleted);
        // Go!
        return mapping.findForward( "Success" ) ;
    }
    
    
    private void deleteRun(Integer piRunId) throws Exception {
    	
    	ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(piRunId);
		if(job == null) {
			log.error("Could not find protein inference job for piRunID: "+piRunId);
			return;
		}
		
		log.info("DELETE OLD JOB: "+job.getId());
		
        boolean deleted = JobDeleter.getInstance().deleteJob(job);
        
        // If the entry in jobQueue was not deleted stop here.
        if(deleted) {
        	
        	// now delete the protein inference run from the mass spec database.
	        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
	        try {
	        	log.info("DELETE OLD PI RUN: "+piRunId);
	            fact.getProteinferRunDao().delete(piRunId);
	        }
	        catch(Exception e) {
	        	log.error("IdPicker run could not be deleted; piRunID: "+piRunId, e);
	        }
        }
        else {
        	log.error("Protein inference job could not be deleted; jobID: "+job.getId());
        }
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
}
