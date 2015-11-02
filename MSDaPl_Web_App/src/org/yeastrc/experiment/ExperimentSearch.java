/**
 * ExperimentSearch.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public class ExperimentSearch implements MsSearch{

    private final MsSearch search;
    private List<SearchFile> files; 
    
    public ExperimentSearch(MsSearch search) {
        this.search = search;
    }
    
    public List<SearchFile> getFiles() {
        return files;
    }
    
    public void setFiles(List<SearchFile> files) {
        this.files = files;
    }
    
    @Override
    public int getId() {
        return search.getId();
    }
    
    @Override
    public List<MsResidueModification> getDynamicResidueMods() {
        return search.getDynamicResidueMods();
    }

    public String getDynamicResidueModifications() {
        String mods = "";
        for(MsResidueModification mod: getDynamicResidueMods()) {
            mods = mods + ", "+mod.getModifiedResidue()+" ("+round(mod.getModificationMass())+")";
        }
        if(mods.length() > 0)
            mods = mods.substring(1);
        else 
            mods = "NONE";
        return mods;
    }
    
    @Override
    public List<MsTerminalModification> getDynamicTerminalMods() {
        return search.getDynamicTerminalMods();
    }
    
    public String getDynamicTerminalModifications() {
        String mods = "";
        for(MsTerminalModification mod: getDynamicTerminalMods()) {
            mods = mods + ", "+mod.getModifiedTerminal()+" ("+round(mod.getModificationMass())+")";
        }
        if(mods.length() > 0)
            mods = mods.substring(1);
        else 
            mods = "NONE";
        return mods;
    }

    @Override
    public List<MsEnzyme> getEnzymeList() {
        return search.getEnzymeList();
    }
    
    public String getEnzymes() {
        String enzymes = "";
        for(MsEnzyme enzyme: getEnzymeList()) {
            enzymes = enzymes + ", "+enzyme.getName();
        }
        if(enzymes.length() > 0)
            enzymes = enzymes.substring(1);
        return enzymes;
    }

    @Override
    public int getExperimentId() {
        return search.getExperimentId();
    }

    @Override
    public List<MsSearchDatabase> getSearchDatabases() {
        return search.getSearchDatabases();
    }
    
    public String getSearchDatabase() {
        String dbs = "";
        for(MsSearchDatabase db: getSearchDatabases()) {
            dbs = dbs + ", "+db.getDatabaseFileName();
        }
        if(dbs.length() > 0)
            dbs = dbs.substring(1);
        return dbs;
    }

    @Override
    public List<MsResidueModification> getStaticResidueMods() {
        return search.getStaticResidueMods();
    }
    
    public String getStaticResidueModifications() {
        String mods = "";
        for(MsResidueModification mod: getStaticResidueMods()) {
            mods = mods + ", "+mod.getModifiedResidue()+" ("+round(mod.getModificationMass())+")";
        }
        if(mods.length() > 0)
            mods = mods.substring(1);
        else 
            mods = "NONE";
        return mods;
    }
    
    @Override
    public List<MsTerminalModification> getStaticTerminalMods() {
        return search.getStaticTerminalMods();
    }
    
    public String getStaticTerminalModifications() {
        String mods = "";
        for(MsTerminalModification mod: getStaticTerminalMods()) {
            mods = mods + ", "+mod.getModifiedTerminal()+" ("+round(mod.getModificationMass())+")";
        }
        if(mods.length() > 0)
            mods = mods.substring(1);
        else 
            mods = "NONE";
        return mods;
    }
    
    private static double round(BigDecimal number) {
        double num = number.doubleValue();
        return Math.round(num*100.0)/100.0;
    }

    @Override
    public Date getUploadDate() {
        return search.getUploadDate();
    }

    @Override
    public Date getSearchDate() {
        return search.getSearchDate();
    }

    @Override
    public Program getSearchProgram() {
        return search.getSearchProgram();
    }

    @Override
    public String getSearchProgramVersion() {
        return search.getSearchProgramVersion();
    }

    @Override
    public String getServerDirectory() {
        return search.getServerDirectory();
    }
}
