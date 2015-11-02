package org.yeastrc.www.proteinfer.job;


import java.sql.Date;

import org.yeastrc.jobqueue.Job;
import org.yeastrc.jobqueue.JobUtils;

public class ProteinferJob extends Job {

    private int pinferId;
    private String program;
    private String version;
    private String comments;
    private Date dateRun;
    
    public Date getDateRun() {
        return dateRun;
    }
    public void setDateRun(Date dateRun) {
        this.dateRun = dateRun;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public int getPinferId() {
        return pinferId;
    }
    public void setPinferRunId(int pinferId) {
        this.pinferId = pinferId;
    }
    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }
    public boolean isRunning() {
        return this.getStatus() == JobUtils.STATUS_QUEUED || this.getStatus() == JobUtils.STATUS_OUT_FOR_WORK;
    }
    public boolean isFailed() {
        return this.getStatus() == JobUtils.STATUS_SOFT_ERROR || this.getStatus() == JobUtils.STATUS_HARD_ERROR;
    }
    public boolean isComplete() {
        return this.getStatus() == JobUtils.STATUS_COMPLETE;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
}
