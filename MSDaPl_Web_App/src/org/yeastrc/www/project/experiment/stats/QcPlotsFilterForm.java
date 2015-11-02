/**
 * SequestFilterResultsForm.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment.stats;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * 
 */
public class QcPlotsFilterForm extends ActionForm {

    private int experimentId;
    private int analysisId;
    private double qvalue = 0.01;
    
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        
        ActionErrors errors =  new ActionErrors();
        
        if(experimentId <= 0) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, 
                    new ActionMessage("error.general.errorMessage", "Invalid value for experimentId: "+experimentId));
        }
        
        if(analysisId <= 0) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, 
                    new ActionMessage("error.general.errorMessage", "Invalid value for analysisId: "+analysisId));
        }
        
        if(qvalue < 0 || qvalue > 1.0) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, 
                    new ActionMessage("error.general.errorMessage", 
                            "Invalid value for qvalue: "+qvalue+". Valid valies are between 0.0 and 1.0."));
        }
        
        return errors;
    }


    public int getExperimentId() {
        return experimentId;
    }


    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }


    public int getAnalysisId() {
        return analysisId;
    }


    public void setAnalysisId(int analysisId) {
        this.analysisId = analysisId;
    }


    public double getQvalue() {
        return qvalue;
    }


    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }
}
