/**
 * DoProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
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
import org.yeastrc.ms.domain.protinfer.ProgramParam;
import org.yeastrc.ms.domain.protinfer.ProgramParam.ParamMaker;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.proteinfer.job.ProgramParameters.Param;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DoProteinInferenceAction extends Action {

    private static final Logger log = Logger.getLogger(DoProteinInferenceAction.class);
    
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

        // Restrict access to members who are part of a group
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isInAGroup(user.getResearcher().getID())) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
        }
        
        ProteinInferenceForm prinferForm = (ProteinInferenceForm) form;
        ProteinInferInputSummary inputSummary = prinferForm.getInputSummary();
        ProgramParameters params = prinferForm.getProgramParams();
        
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
        
        // If "Remove Asterisks (*)" was unchecked it may not be in the parameters list
        // or its value will be empty. Set it to false.
        boolean foundRemoveAsterisksParam = false;
        ProgramParam removeAsterisksParam = ParamMaker.makeRemoveAsterisksParam();
        for(Param param: params.getParamList()) {
            if(param.getName().equals(removeAsterisksParam.getName())) {
                if(param.getValue() == null || param.getValue().trim().length() == 0)
                    param.setValue("false");
                foundRemoveAsterisksParam = true;
                break;
            }
        }
        if(!foundRemoveAsterisksParam) {
            Param myParam = new Param(removeAsterisksParam);
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
        
        if(prinferForm.isIndividualRuns()) {
            ProteinferJobSaver.instance().saveMultiJobToDatabase(user.getID(), inputSummary, params, 
                    prinferForm.getInputType(), prinferForm.getComments());
        }
        else {
            ProteinferJobSaver.instance().saveJobToDatabase(user.getID(), inputSummary, params, 
                prinferForm.getInputType(), prinferForm.getComments());
        }
        
        // Go!
        ActionForward success = mapping.findForward( "Success" ) ;
        success = new ActionForward( success.getPath() + "?ID="+prinferForm.getProjectId(), success.getRedirect() ) ;
        return success;

    }
    
}
