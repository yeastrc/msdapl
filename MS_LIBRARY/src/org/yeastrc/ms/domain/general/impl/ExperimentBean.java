/**
 * MsExperimentBean.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general.impl;

import java.sql.Date;
import java.sql.Timestamp;

import org.yeastrc.ms.domain.general.MsExperiment;

/**
 * 
 */
public class ExperimentBean implements MsExperiment {

    private int id;
    private String serverAddress;
    private String serverDirectory;
    private Date uploadDate;
    private Timestamp lastUpdateDate;
    private String comments;
    private int instrumentId;
    
    
    public void setId(int id) {
        this.id = id;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public void setServerDirectory(String serverDirectory) {
        this.serverDirectory = serverDirectory;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    
    public int getInstrumentId() {
        return this.instrumentId;
    }
    
    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }
    
    @Override
    public int getId() {
        return id;
    }

    @Override
    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public String getServerDirectory() {
        return serverDirectory;
    }
    
    @Override
    public Date getUploadDate() {
        return uploadDate;
    }
}
