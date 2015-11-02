/**
 * NewPercolatorProteinInference.java
 * @author Vagisha Sharma
 * Apr 8, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class NewPercolatorProteinInferenceAction extends Action {

    private static final Logger log = Logger.getLogger(NewPercolatorProteinInferenceAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
//    	ActionErrors errors = new ActionErrors();
//    	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", 
//    			"Protein inference jobs have been disabled. A bug was found that is currently being fixed."));
//    	saveErrors( request, errors );
//    	return mapping.findForward("standardHome");
    	
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        // Restrict access to members
//        Groups groupMan = Groups.getInstance();
//        if (!groupMan.isMember(user.getResearcher().getID(), Projects.MACCOSS) &&
//          !groupMan.isMember( user.getResearcher().getID(), Projects.YATES) &&
//          !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
//            ActionErrors errors = new ActionErrors();
//            errors.add("access", new ActionMessage("error.access.invalidgroup"));
//            saveErrors( request, errors );
//            return mapping.findForward( "Failure" );
//        }
        
        int searchAnalysisId = -1;
        if (request.getParameter("searchAnalysisId") != null) {
            try {searchAnalysisId = Integer.parseInt(request.getParameter("searchAnalysisId"));}
            catch(NumberFormatException e) {searchAnalysisId = -1;}
        }
        
        if (searchAnalysisId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.analysisId", searchAnalysisId, ""));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        // make sure a search analysis with the given Id exists AND it is a Percolator analysis
        MsSearchAnalysis searchAnalysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(searchAnalysisId);
        if(searchAnalysis == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.analysisId", searchAnalysisId, ""));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        // make sure this is a Percolator analysis
        if(searchAnalysis.getAnalysisProgram() != Program.PERCOLATOR) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.analysisId", 
                    searchAnalysisId, "Not a Percolator analysis."));
            saveErrors( request, errors );
        }
        
        
        // We need the projectID so we can check this user's access rights and 
        // redirect back to the project page after
        // the protein inference job has been submitted.
        int projectId = -1;
        if (request.getParameter("projectId") != null) {
            try {projectId = Integer.parseInt(request.getParameter("projectId"));}
            catch(NumberFormatException e) {projectId = -1;}
        }
        
        if (projectId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.projectId", projectId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        request.setAttribute("projectId", projectId);
        
        // User making the request to run protein inference should be affiliated with the project
        Project project = ProjectDAO.instance().load(projectId);
        if(!project.checkAccess(user.getResearcher())) {
             ActionErrors errors = new ActionErrors();
             errors.add("username", new ActionMessage("error.general.errorMessage", 
                     "You may run protein inference only for your projects."));
             saveErrors( request, errors );
             return mapping.findForward( "Failure" );
        }
        
        
        
        // Create our ActionForm
        ProteinInferenceForm formForAnalysis = createFormForAnalysisInput(searchAnalysis, projectId);

        if(formForAnalysis != null)
            request.setAttribute("proteinInferenceFormAnalysis", formForAnalysis);
            
        
        // Go!
        return mapping.findForward("Success");

    }

    private ProteinInferenceForm createFormForAnalysisInput(MsSearchAnalysis analysis, int projectId) {
        
        ProteinInferInputGetter inputGetter = ProteinInferInputGetter.instance();

        ProteinInferenceForm formForAnalysis = new ProteinInferenceForm();
        formForAnalysis.setInputType(InputType.ANALYSIS);
        formForAnalysis.setProjectId(projectId);
        ProteinInferInputSummary inputSummary = inputGetter.getInputAnalysisSummary(analysis);
        formForAnalysis.setInputSummary(inputSummary);
        
        // DO we have peptide-level scores for this analysis
        boolean havePeptideScores = false;
        PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
        if(peptResDao.peptideCountForAnalysis(analysis.getId()) > 0)
        	havePeptideScores = true;
        
        // get the percolator version
        String version = analysis.getAnalysisProgramVersion();
        ProgramParameters progParams = new ProgramParameters(ProteinInferenceProgram.PROTINFER_PERC);
        
        // If we don't have peptide-level scores we will not display the peptide related scores options.
        if(havePeptideScores) {
        	progParams = new ProgramParameters(ProteinInferenceProgram.PROTINFER_PERC_PEPT);
//        	progParams.removeParam("peptide_qval_percolator");
//        	progParams.removeParam("peptide_pep_percolator");
        }
        
        try {
            float fv = Float.parseFloat(version);
            if(fv < 1.06)
                progParams = new ProgramParameters(ProteinInferenceProgram.PROTINFER_PERC_OLD);
                
        }
        catch(NumberFormatException ex) {
            log.error("Cannot determine version of Percolator. Version: "+version);
        }
        // set the Protein Inference parameters
        formForAnalysis.setProgramParams(progParams);
        return formForAnalysis;
    }
}
