/**
 * ProteinferRunPlus.java
 * @author Vagisha Sharma
 * Apr 10, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;



import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;

/**
 * 
 */
public class ProteinferRunFormBean {

    private List<Integer> projectIds;
    private int runId;
    private Date runDate;
    private String comments;
    private ProteinInferenceProgram program;
    private boolean isSelected = false;
    

    public ProteinferRunFormBean() {
        projectIds = new ArrayList<Integer>();
    }
    
    public ProteinferRunFormBean(ProteinferRun run, int projectId) {
        this(projectId, run.getId(), run.getDate(), run.getComments(), run.getProgram());
    }
    
    protected ProteinferRunFormBean(int projectId, int runId, Date runDate, String comments, ProteinInferenceProgram program) {
        this(projectId, runId, (java.util.Date)runDate, comments, program);
    }
    
    protected ProteinferRunFormBean(int projectId, int runId, java.util.Date runDate, String comments, ProteinInferenceProgram program) {
        this();
        this.projectIds.add(projectId);
        this.runId = runId;
        if(runDate != null)
            this.runDate = new Date(runDate.getTime());
        this.comments = comments;
        this.program = program;
    }
    
    public ProteinferRunFormBean(ProteinferRun run, List<Integer> projectIds) {
        this(projectIds, run.getId(), run.getDate(), run.getComments(), run.getProgram());
    }
    
    protected ProteinferRunFormBean(List<Integer> projectIds, int runId, Date runDate, String comments, ProteinInferenceProgram program) {
        this(projectIds, runId, (java.util.Date)runDate, comments, program);
    }
    
    protected ProteinferRunFormBean(List<Integer> projectIds, int runId, java.util.Date runDate, String comments, ProteinInferenceProgram program) {
        this();
        this.projectIds = projectIds;
        this.runId = runId;
        if(runDate != null)
            this.runDate = new Date(runDate.getTime());
        this.comments = comments;
        this.program = program;
    }
    
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public void setProjectIdString(String projectIdString) {
        String[] tokens = projectIdString.split(",");
        for(String tok: tokens)
            projectIds.add(Integer.parseInt(tok));
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setProgramName(String program) {
        this.program = ProteinInferenceProgram.getProgramForName(program);
    }

    public int getRunId() {
        return runId;
    }

    public List<Integer> getProjectIdList() {
        return this.projectIds;
    }
    
    public String getProjectIdString() {
        StringBuilder buf = new StringBuilder();
        for(int id: projectIds) {
            buf.append(","+id);
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
    }
    
    public Date getRunDate() {
        return runDate;
    }

    public String getProgramDisplayName() {
        return program.getDisplayName();
    }
    
    public String getProgramName() {
        return program.name();
    }
    
    public String getComments() {
        return comments;
    }
    
}
