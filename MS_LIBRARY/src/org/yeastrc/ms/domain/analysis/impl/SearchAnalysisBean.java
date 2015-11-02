package org.yeastrc.ms.domain.analysis.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;

public class SearchAnalysisBean implements MsSearchAnalysis {

    private int id;
//    private int searchId;
    private Date uploadDate;
    private Program analysisProgram;
    private String analysisProgramVersion;
    private String comments;
    private String filename;
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String getComments() {
		return comments;
	}
    
    @Override
	public void setComments(String comments) {
		this.comments = comments;
	}
    
	public Program getAnalysisProgram() {
        return analysisProgram;
    }
    
    public void setAnalysisProgram(Program program) {
        this.analysisProgram = program;
    }
    
    public String getAnalysisProgramVersion() {
        return analysisProgramVersion;
    }
    
    public void setAnalysisProgramVersion(String programVersion) {
        this.analysisProgramVersion = programVersion;
    }

    public void setUploadDate(Date date) {
        this.uploadDate = date;
    }
    
    @Override
    public Date getUploadDate() {
        return uploadDate;
    }
    
	@Override
	public String getFilename() {
		return this.filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
    
}
