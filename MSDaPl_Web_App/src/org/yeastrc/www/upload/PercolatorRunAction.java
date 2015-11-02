/**
 * PercolatorRunAction.java
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
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.PercolatorJob;
import org.yeastrc.ms.domain.protinfer.ProgramParam;
import org.yeastrc.ms.domain.protinfer.ProgramParam.ParamMaker;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.proteinfer.job.ProgramParameters;
import org.yeastrc.www.proteinfer.job.ProgramParameters.Param;
import org.yeastrc.www.upload.PercolatorRunForm.PercolatorInputFile;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PercolatorRunAction extends Action {

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

        PercolatorRunForm myForm = (PercolatorRunForm) form;
        
        // get the project Id
        int projectId = myForm.getProjectId();
        // User making the request to run protein inference should be affiliated with the project
        Project project = ProjectDAO.instance().load(projectId);
        if(!project.checkAccess(user.getResearcher())) {
             ActionErrors errors = new ActionErrors();
             errors.add("username", new ActionMessage("error.general.errorMessage", 
                     "You may run percolator only for your projects."));
             saveErrors( request, errors );
             return mapping.findForward( "Failure" );
        }
        
        
        checkDefaultProteinInferOptions(myForm);
        
        List<PercolatorJob> percJobs = createPercolatorJobs(user, myForm);
        
        
        PercolatorResultDirectoryCreator percDirCreator = PercolatorResultDirectoryCreator.getInstance();
        for(PercolatorJob percJob: percJobs) {
        	
        	// First create the directory where results will be saved and the input files will be written
        	try {
        		percDirCreator.initForJob(percJob);
        	}
        	catch(PercolatorExecutorException e) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("general", new ActionMessage("error.general.errorMessage", "Error running percolator: "+e.getMessage()));
        		saveErrors( request, errors );
        		log.error("Error creating directory for Percolator", e);
        		ActionForward oldFwd = mapping.findForward("Failure");
        		String path = oldFwd.getPath()+"?ID="+projectId;
        		boolean redirect = oldFwd.getRedirect();
        		return new ActionForward(path, redirect);
        	}

        	// Now create an entry in the jobQueue database
        	try {
        		PercolatorJobSaver.getInstance().save(percJob);
        	}
        	catch(Exception e) {
        		log.error("Error saving job details to database", e);
        		ActionErrors errors = new ActionErrors();
        		errors.add("general", new ActionMessage("error.general.errorMessage", "Error saving job details to database: "+e.getMessage()));
        		saveErrors( request, errors );
        		ActionForward oldFwd = mapping.findForward("Failure");
        		String path = oldFwd.getPath()+"?ID="+projectId;
        		boolean redirect = oldFwd.getRedirect();
        		return new ActionForward(path, redirect);
        	}
        }
        
        // Go!
        StringBuilder buf = new StringBuilder();
        for(PercolatorJob job: percJobs)
        	buf.append(job.getId()+",");
        buf.deleteCharAt(buf.length() - 1);
        ActionForward fwd = mapping.findForward( "Success" );
		return new ActionForward(fwd.getPath()+"?queued="+buf.toString(), fwd.getRedirect());

    }

	private void checkDefaultProteinInferOptions(PercolatorRunForm myForm) {
		
		ProgramParameters params = myForm.getProgramParams();
        // If "remove ambiguous spectrum" was unchecked it may not be in the parameters list
        // or its value will be empty. Set it to false.
        boolean foundAmbig = false;
        ProgramParam ambigSpecParam = ParamMaker.makeRemoveAmbigSpectraParam();
        for(Param param: params.getParamList()) {
            if(param.getName().equals(ambigSpecParam.getName())) {
                if(param.getValue() == null || param.getValue().trim().length() == 0)
                    param.setValue("false");
                foundAmbig = true;
                break;
            }
        }
        if(!foundAmbig) {
            Param myParam = new Param(ambigSpecParam);
            myParam.setValue("false");
            params.addParam(myParam);
        }
        
        // If "refresh peptide protein mathces" was unchecked it may not be in the parameters list
        // or its value will be empty. Set it to false.
        boolean refreshProteinMatches = false;
        ProgramParam refreshProteinMatchesParam = ParamMaker.makeRefreshPeptideProteinMatchParam();
        for(Param param: params.getParamList()) {
            if(param.getName().equals(refreshProteinMatchesParam.getName())) {
                if(param.getValue() == null || param.getValue().trim().length() == 0)
                    param.setValue("false");
                refreshProteinMatches = true;
                break;
            }
        }
        if(!refreshProteinMatches) {
            Param myParam = new Param(refreshProteinMatchesParam);
            myParam.setValue("false");
            params.addParam(myParam);
        }
        
        // If "Allow I/L substitution" was unchecked it may not be in the parameters list
        // or its value will be empty. Set it to false.
        boolean foundItoLSubstParam = false;
        ProgramParam doItoLSubstitutionParam = ParamMaker.makeDoItoLSubstitutionParam();
        for(Param param: params.getParamList()) {
            if(param.getName().equals(doItoLSubstitutionParam.getName())) {
                if(param.getValue() == null || param.getValue().trim().length() == 0)
                    param.setValue("false");
                foundItoLSubstParam = true;
                break;
            }
        }
        if(!foundItoLSubstParam) {
            Param myParam = new Param(doItoLSubstitutionParam);
            myParam.setValue("false");
            params.addParam(myParam);
        }
        
        // If "Calculate NSAF for all proteins" was unchecked it may not be in the parameters list
        // or its value will be empty. Set it to false.
        boolean foundCalcNsafForAll = false;
        ProgramParam calcNsafForAllParam = ParamMaker.makeCalculateAllNsafParam();
        for(Param param: params.getParamList()) {
            if(param.getName().equals(calcNsafForAllParam.getName())) {
                if(param.getValue() == null || param.getValue().trim().length() == 0)
                    param.setValue("false");
                foundCalcNsafForAll = true;
                break;
            }
        }
        if(!foundCalcNsafForAll) {
            Param myParam = new Param(calcNsafForAllParam);
            myParam.setValue("false");
            params.addParam(myParam);
        }
	}

	private List<PercolatorJob> createPercolatorJobs(User user,PercolatorRunForm myForm) {
		
		List<PercolatorJob> jobs = new ArrayList<PercolatorJob>();
		
		if(myForm.isIndividualRuns()) {
			
			for(PercolatorInputFile iFile: myForm.getSelectedInputFiles()) {
				
				PercolatorJob percJob = new PercolatorJob();
				percJob.setSubmitter(user.getID());
				percJob.setType(JobUtils.TYPE_PERC_EXE);
				percJob.setProjectID(myForm.getProjectId());
				percJob.setExperimentID(myForm.getExperimentId());
				percJob.setSearchId(myForm.getSearchId());
				List<PercolatorInputFile> percInputList = new ArrayList<PercolatorInputFile>(1);
				percInputList.add(iFile);
				percJob.setPercolatorInputFiles(percInputList);
				percJob.setComments(iFile.getRunName()+" "+myForm.getComments());
				percJob.setRunProteinInference(myForm.isRunProteinInference());
				percJob.setProgramParams(myForm.getProgramParams());
				jobs.add(percJob);
			}
		}
		else {
			PercolatorJob percJob = new PercolatorJob();
			percJob.setSubmitter(user.getID());
			percJob.setType(JobUtils.TYPE_PERC_EXE);
			percJob.setProjectID(myForm.getProjectId());
			percJob.setExperimentID(myForm.getExperimentId());
			percJob.setSearchId(myForm.getSearchId());
			percJob.setPercolatorInputFiles(myForm.getSelectedInputFiles());
			percJob.setProgramParams(myForm.getProgramParams());
			percJob.setComments(myForm.getComments());
			percJob.setRunProteinInference(myForm.isRunProteinInference());
			jobs.add(percJob);
		}
		return jobs;
	}
}
