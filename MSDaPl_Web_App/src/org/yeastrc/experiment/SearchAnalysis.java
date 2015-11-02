/**
 * SearchAnalysis.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.jobqueue.MsAnalysisUploadJob;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.www.project.experiment.QCPlot;
import org.yeastrc.yates.YatesRun;


/**
 * 
 */
public class SearchAnalysis implements MsSearchAnalysis {

    
    private final MsSearchAnalysis analysis;
    private String analysisName;
    private List<AnalysisFile> files;
    private MsAnalysisUploadJob job;
    
    private List<QCPlot> qcPlots;
    private List<String> qcSummaryStringList;
    
    private YatesRun dtaSelect;
    private List<ExperimentProteinProphetRun> prophetRuns;
    private List<ExperimentProteinferRun> protInferRuns;
    
    private static final Pattern tppVersionPattern = Pattern.compile("TPP\\s+(v\\d+\\.\\d+)");
    
    public SearchAnalysis(MsSearchAnalysis analysis) {
        this.analysis = analysis;
        qcSummaryStringList = new ArrayList<String>();
    }
    
    public void setJob(MsAnalysisUploadJob job) {
    	this.job = job;
    }
    
    public MsAnalysisUploadJob getJob() {
    	return job;
    }
    
    public boolean hasJob() {
    	return job != null;
    }
    
    public void setFiles(List<AnalysisFile> files) {
        this.files = files;
    }
    
    public List<AnalysisFile> getFiles() {
        return files;
    }
    
    @Override
    public Program getAnalysisProgram() {
        return analysis.getAnalysisProgram();
    }

    @Override
    public String getAnalysisProgramVersion() {
        return analysis.getAnalysisProgramVersion();
    }
    
    public String getAnalysisProgramVersionShort() {
    	String version = getAnalysisProgramVersion();
    	
    	if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
    		Matcher m = tppVersionPattern.matcher(version);
    		if(m.find()) {
    			version = m.group(1);
    		}
    	}
    	return version;
    }

    @Override
    public int getId() {
        return analysis.getId();
    }

    @Override
    public Date getUploadDate() {
        return analysis.getUploadDate();
    }

    public String getFilename() {
        return analysis.getFilename();
    }

    @Override
    public void setId(int analysisId) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isComplete() {
        return this.job == null || this.job.isComplete();
    }
    
    public List<ExperimentProteinferRun> getProtInferRuns() {
    	if(protInferRuns == null)
    		return new ArrayList<ExperimentProteinferRun>();
    	else
    		return protInferRuns;
    }

    public void setProtInferRuns(List<ExperimentProteinferRun> protInferRuns) {
        this.protInferRuns = protInferRuns;
    }
    
    public boolean getHasProtInferResults() {
        return dtaSelect != null || 
        (protInferRuns != null && protInferRuns.size() > 0) ||
        (prophetRuns != null && prophetRuns.size() > 0);
    }
    
    public YatesRun getDtaSelect() {
        return dtaSelect;
    }

    public void setDtaSelect(YatesRun dtaSelect) {
        this.dtaSelect = dtaSelect;
    }

    public List<ExperimentProteinProphetRun> getProteinProphetRuns() {
    	if(prophetRuns == null)
    		return new ArrayList<ExperimentProteinProphetRun>();
    	else
    		return prophetRuns;
    }
    
    public void setProteinProphetRun(List<ExperimentProteinProphetRun> runs) {
        this.prophetRuns = runs;
    }
    
    public List<QCPlot> getQcPlots() {
    	if(this.qcPlots == null)
    		return new ArrayList<QCPlot>(0);
    	return this.qcPlots;
    }
    
    public void setQcPlots(List<QCPlot> plots) {
    	this.qcPlots = plots;
    }
    
	public List<String> getQcSummaryStrings() {
		return qcSummaryStringList;
	}

	public void addQcSummaryString(String qcSummaryString) {
		this.qcSummaryStringList.add(qcSummaryString);
	}

	@Override
	public String getComments() {
		return this.analysis.getComments();
	}

	@Override
	public void setComments(String comments) {
		throw new UnsupportedOperationException();
	}

}
