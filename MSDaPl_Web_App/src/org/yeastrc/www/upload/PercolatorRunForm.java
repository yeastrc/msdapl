/**
 * PercolatorRunForm.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.upload;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.proteinfer.job.ProgramParameters;
import org.yeastrc.www.proteinfer.job.ProgramParameters.Param;

/**
 * 
 */
public class PercolatorRunForm extends ActionForm {

    private int projectId;
    private int experimentId;
    private int searchId;
    private List<PercolatorInputFile> inputFiles;
    private String comments;
    private boolean individualRuns = false;
    private boolean runProteinInference = false;
    private ProgramParameters programParams;
    
    
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public PercolatorRunForm () {
    	inputFiles = new ArrayList<PercolatorInputFile>();
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
        
        return errors;
    }
    
    public int getProjectId() {
        return projectId;
    }
    
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    public int getExperimentId() {
		return experimentId;
	}
    
	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}
	
	public int getSearchId() {
		return searchId;
	}
	
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	
	public List<PercolatorInputFile> getInputFiles() {
		return inputFiles;
	}
	
	public List<PercolatorInputFile> getSelectedInputFiles() {
		List<PercolatorInputFile> selectedFiles = new ArrayList<PercolatorInputFile>();
		for(PercolatorInputFile ifile: this.inputFiles)
			if(ifile.getIsSelected())
				selectedFiles.add(ifile);
		
		return selectedFiles;
	}
	
	public void setInputFiles(List<PercolatorInputFile> inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	// to be used by struts indexed properties
    public PercolatorInputFile getInputFile(int index) {
        while(index >= inputFiles.size())
        	inputFiles.add(new PercolatorInputFile());
        return inputFiles.get(index);
    }
    
    public void addInputFile(PercolatorInputFile runSearch) {
    	inputFiles.add(runSearch);
    }
    
	public boolean isIndividualRuns() {
        return individualRuns;
    }
    
    public void setIndividualRuns(boolean individual) {
        this.individualRuns = individual;
    }
    
    public boolean isRunProteinInference() {
		return runProteinInference;
	}
	public void setRunProteinInference(boolean runProteinInference) {
		this.runProteinInference = runProteinInference;
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
    
	public static final class PercolatorInputFile {
        private int runSearchId;
        private String runName;
        private boolean selected = false;
        
        public PercolatorInputFile() {}
        
        public PercolatorInputFile(int runSearchId, String runName) {
            this.runSearchId = runSearchId;
            this.runName = runName;
        }
        
        public void setRunSearchId(int runSearchId) {
            this.runSearchId = runSearchId;
        }

        public void setRunName(String runName) {
            this.runName = runName;
        }

        public boolean getIsSelected() {
            return selected;
        }

        public void setIsSelected(boolean selected) {
            this.selected = selected;
        }

        public int getRunSearchId() {
            return runSearchId;
        }

        public String getRunName() {
            return runName;
        }
    }
}
