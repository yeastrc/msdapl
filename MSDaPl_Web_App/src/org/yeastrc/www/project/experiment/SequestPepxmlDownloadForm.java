/**
 * SequestPepxmlDownloadForm.java
 * @author Vagisha Sharma
 * Apr 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ExperimentFile;


/**
 * 
 */
public class SequestPepxmlDownloadForm extends SequestFilterResultsForm {

    private List<ExperimentFile> files = new ArrayList<ExperimentFile>();
    
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        if(getSelectedFileIds().size() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 1 or more files to export."));
        }
        return errors;
    }
    
    
    public ExperimentFile getFile(int index) {
        while(index >= files.size())
            files.add(new ExperimentFile());
        return files.get(index);
    }
    
    public void setFileList(List <ExperimentFile> files) {
        this.files = files;
    }
    
    public List <ExperimentFile> getFileList() {
        return files;
    }
    
    public List<Integer> getSelectedFileIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for(ExperimentFile file: files) {
            if (file != null && file.isSelected())
                ids.add(file.getId());
        }
        return ids;
    }
}
