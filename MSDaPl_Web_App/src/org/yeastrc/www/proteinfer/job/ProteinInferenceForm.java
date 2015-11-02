/**
 * ProteinInferenceForm.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;
import org.yeastrc.www.proteinfer.job.ProgramParameters.Param;

/**
 * 
 */
public class ProteinInferenceForm extends ActionForm {

    private int projectId;
    private ProgramParameters programParams;
    private ProteinInferInputSummary inputSummary;
    private InputType inputType;
    private String comments;
    private boolean individualRuns = false;
    
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public ProteinInferenceForm () {
        inputSummary = new ProteinInferInputSummary();
        programParams = new ProgramParameters();
    }
    /**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        // FORM VALIDATION WILL BE DONE VIA JAVASCRIPT.
        
//        boolean hasErrors = false;
//        // Make sure at least one file was selected
//        boolean selected = false;
//        for(ProteinInferIputFile input: inputSummary.getInputFiles()) {
//            if(input.getIsSelected()) {
//                selected = true;
//                break;
//            }
//        }
//        if(!selected) {
//            errors.add("proteinfer", new ActionMessage("error.proteinfer.noinput"));
//            hasErrors = true;
//        }
//        
//        updateProgramParameterDefaults(programParams);
//        // Validate the parameter values
//        StringBuilder errorMessage = new StringBuilder();
//        if(!ProgramParameters.validateParams(this.programParams, errorMessage)) {
//            String[] err = errorMessage.toString().split("\\n");
//            for(String e: err)
//                errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.param", e));
//            hasErrors = true;
//        }
//        
//        if(hasErrors) {
//            String programName = programParams.getProgramName();
//            ProteinInferenceProgram piProgram = ProteinInferenceProgram.getProgramForName(programName);
//            // TODO Add a method in ProteinInferenceProgram -- something like ProteinInferenceProgram.usesSearchInput().
//            if(piProgram == ProteinInferenceProgram.PROTINFER_PERC)
//                request.setAttribute("useSearchInput", false);
//            else
//                request.setAttribute("useSearchInput", true);
//        }
        
        return errors;
    }
    
    public InputType getInputType() {
        return this.inputType;
    }
    
    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }
    
    public char getInputTypeChar() {
        return inputType.getShortName();
    }
    
    public void setInputTypeChar(char shortName) {
        this.inputType = InputType.getInputTypeForChar(shortName);
    }
    
    public void setInputSummary(ProteinInferInputSummary inputSummary) {
        this.inputSummary = inputSummary;
    }

    public ProteinInferInputSummary getInputSummary() {
        return inputSummary;
    }
    
    public ProteinInferIputFile getInputFile(int index) {
        return inputSummary.getInputFile(index);
    }
    
    public ProgramParameters getProgramParams() {
        return programParams;
    }
    
    public void setProgramParams(ProgramParameters programParams) {
        this.programParams = programParams;
    }
    public Param getParam(int index) {
        return programParams.getParam(index);
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    public boolean isIndividualRuns() {
        return individualRuns;
    }
    
    public void setIndividualRuns(boolean individual) {
        this.individualRuns = individual;
    }
}
