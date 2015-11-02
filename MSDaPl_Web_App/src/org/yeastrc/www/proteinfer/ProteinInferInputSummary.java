package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.List;

public class ProteinInferInputSummary {

    private int inputGroupId; // can be a searchId or searchAnalysisId
    private String program;
    private String programVersion;
    
//    private int searchAnalysisId;
//    private String analysisProgram;
//    private String analysisProgramVersion;
    
    private String searchDatabase;
    private List<ProteinInferIputFile> files;
    
    public ProteinInferInputSummary() {
        files = new ArrayList<ProteinInferIputFile>();
    }
    
    public List<ProteinInferIputFile> getInputFiles() {
        return files;
    }
    
    /**
     * Either a searchId or a searchAnalysisId
     * @return
     */
    public int getInputGroupId() {
        return inputGroupId;
    }

    /**
     * Either a searchId or a searchAnalysisId
     * @param inputGroupId
     */
    public void setInputGroupId(int inputGroupId) {
        this.inputGroupId = inputGroupId;
    }

    public void setInputFiles(List<ProteinInferIputFile> files) {
        this.files = files;
    }
    
    // to be used by struts indexed properties
    public ProteinInferIputFile getInputFile(int index) {
        while(index >= files.size())
            files.add(new ProteinInferIputFile());
        return files.get(index);
    }
    
    public void addInputFile(ProteinInferIputFile runSearch) {
        files.add(runSearch);
    }
    
    public String getProgramName() {
        return program;
    }

    public void setProgramName(String program) {
        this.program = program;
    }
    
    public String getProgramVersion() {
        return programVersion;
    }

    public void setProgramVersion(String programVersion) {
        this.programVersion = programVersion;
    }

    
//    public int getSearchAnalysisId() {
//        return searchAnalysisId;
//    }
//
//    public void setSearchAnalysisId(int searchAnalysisId) {
//        this.searchAnalysisId = searchAnalysisId;
//    }
//    
//    public String getAnalysisProgram() {
//        return analysisProgram;
//    }
//
//    public void setAnalysisProgram(String analysisProgram) {
//        this.analysisProgram = analysisProgram;
//    }
//
//    public String getAnalysisProgramVersion() {
//        return analysisProgramVersion;
//    }
//
//    public void setAnalysisProgramVersion(String analysisProgramVersion) {
//        this.analysisProgramVersion = analysisProgramVersion;
//    }

    public String getSearchDatabase() {
        return searchDatabase;
    }

    public void setSearchDatabase(String searchDatabase) {
        this.searchDatabase = searchDatabase;
    }
    
    
    public static final class ProteinInferIputFile {
        private int inputId; // could be runSearchID or runSearchAnalysisID
        private String runName;
        private boolean selected = false;
        
        public ProteinInferIputFile() {}
        
        public ProteinInferIputFile(int inputId, String runName) {
            this.inputId = inputId;
            this.runName = runName;
        }
        
        public void setInputId(int inputId) {
            this.inputId = inputId;
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

        public int getInputId() {
            return inputId;
        }

        public String getRunName() {
            return runName;
        }
    }
}
