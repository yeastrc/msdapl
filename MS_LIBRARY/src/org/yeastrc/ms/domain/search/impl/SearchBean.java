/**
 * MsSearchBean.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.Program;


public class SearchBean implements MsSearch {

    private int id;
    private int experimentId;
    private Date uploadDate;
    private Date searchDate;
    private String serverAddress;
    private String serverDirectory;
    private Program analysisProgram;
    private String analysisProgramVersion;
    
    private List<MsSearchDatabase> searchDatabases;
    
    private List<MsResidueModification> staticResidueMods;
    
    private List<MsResidueModification> dynamicResidueMods;
    
    private List<MsTerminalModification> staticTerminalMods;
    
    private List<MsTerminalModification> dynamicTerminalMods;
    
    private List<MsEnzyme> enzymes;
    
    public SearchBean() {
        searchDatabases = new ArrayList<MsSearchDatabase>();
        staticResidueMods = new ArrayList<MsResidueModification>();
        dynamicResidueMods = new ArrayList<MsResidueModification>();
        staticTerminalMods = new ArrayList<MsTerminalModification>();
        dynamicTerminalMods = new ArrayList<MsTerminalModification>();
    }
    
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
    
    public int getExperimentId() {
        return this.experimentId;
    }
    
    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }
    
    /**
     * @return the serverAddress
     */
    public String getServerAddress() {
        return serverAddress;
    }
    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    /**
     * @return the serverDirectory
     */
    public String getServerDirectory() {
        return serverDirectory;
    }
    /**
     * @param directory the serverDirectory to set
     */
    public void setServerDirectory(String directory) {
        this.serverDirectory = directory;
    }

    public Program getSearchProgram() {
        return analysisProgram;
    }
    
    public void setSearchProgram(Program program) {
        this.analysisProgram = program;
    }
    
    public String getSearchProgramVersion() {
        return analysisProgramVersion;
    }
    
    public void setSearchProgramVersion(String programVersion) {
        this.analysisProgramVersion = programVersion;
    }

    public void setUploadDate(Date date) {
        this.uploadDate = date;
    }
    
    @Override
    public Date getUploadDate() {
        return uploadDate;
    }
    
    public void setSearchDate(Date date) {
        this.searchDate = date;
    }
    
    @Override
    public Date getSearchDate() {
        return searchDate;
    }
    
    //------------------------------------------------------------------------------------------------------
    // database(s) used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsSearchDatabase> getSearchDatabases() {
        return searchDatabases;
    }

    /**
     * @param searchDatabases the searchDatabases to set
     */
    public void setSearchDatabases(List<MsSearchDatabase> searchDatabases) {
        this.searchDatabases = searchDatabases;
    }

    //------------------------------------------------------------------------------------------------------
    // static residue modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsResidueModification> getStaticResidueMods() {
        return staticResidueMods;
    }

    /**
     * @param staticResidueMods the staticModifications to set
     */
    public void setStaticResidueMods(List<MsResidueModification> staticModifications) {
        this.staticResidueMods = staticModifications;
    }
    
    //------------------------------------------------------------------------------------------------------
    // dynamic residue modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsResidueModification> getDynamicResidueMods() {
        return dynamicResidueMods;
    }

    /**
     * @param dynamicResidueMods the dynamicModifications to set
     */
    public void setDynamicResidueMods(List<MsResidueModification> dynamicModifications) {
        this.dynamicResidueMods = dynamicModifications;
    }
    
    //------------------------------------------------------------------------------------------------------
    // static terminal modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsTerminalModification> getStaticTerminalMods() {
        return staticTerminalMods;
    }

    /**
     * @param staticResidueMods the staticModifications to set
     */
    public void setStaticTerminalMods(List<MsTerminalModification> termStaticMods) {
        this.staticTerminalMods = termStaticMods;
    }
    
    //------------------------------------------------------------------------------------------------------
    // dynamic terminal modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsTerminalModification> getDynamicTerminalMods() {
        return dynamicTerminalMods;
    }

    /**
     * @param dynamicResidueMods the dynamicModifications to set
     */
    public void setDynamicTerminalMods(List<MsTerminalModification> termDynaMods) {
        this.dynamicTerminalMods = termDynaMods;
    }

    //------------------------------------------------------------------------------------------------------
    // enzymes used for the search
    //------------------------------------------------------------------------------------------------------
    @Override
    public List<MsEnzyme> getEnzymeList() {
        return enzymes;
    }
    
    public void setEnzymeList(List<MsEnzyme> enzymes) {
        this.enzymes = enzymes;
    }
}
