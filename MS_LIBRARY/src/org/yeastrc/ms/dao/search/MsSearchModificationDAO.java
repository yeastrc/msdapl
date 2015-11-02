package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;

public interface MsSearchModificationDAO {

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModification> loadStaticResidueModsForSearch(int searchId);

    public abstract void saveStaticResidueMod(MsResidueModification mod);

    public abstract void deleteStaticResidueModsForSearch(int searchId);

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModification> loadDynamicResidueModsForSearch(int searchId);

    public abstract int loadMatchingDynamicResidueModId(MsResidueModification mod);
    
    public abstract int saveDynamicResidueMod(MsResidueModification mod);

    /**
     * This will delete all dynamic modifications for a search.
     * If any of the modifications are related to results from the search 
     * they are deleted as well (from the msDynamicModResult table).
     * @param searchId
     */
    public abstract void deleteDynamicResidueModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsTerminalModification> loadStaticTerminalModsForSearch(int searchId);

    public abstract void saveStaticTerminalMod(MsTerminalModification mod);

    public abstract void deleteStaticTerminalModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsTerminalModification> loadDynamicTerminalModsForSearch(int searchId);
    
    public abstract int loadMatchingDynamicTerminalModId(MsTerminalModification mod);

    public abstract int saveDynamicTerminalMod(MsTerminalModification mod);

    public abstract void deleteDynamicTerminalModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC RESIDUE)
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResultResidueMod> loadDynamicResidueModsForResult(int resultId);
    
    public abstract void saveDynamicResidueModForResult(int resultId, int modificationId, int modifiedPosition);
    
    public abstract void saveDynamicResidueModForResult(MsResultResidueModIds modIdentifier);

    public abstract void saveAllDynamicResidueModsForResult(List<MsResultResidueModIds> modList);
    
    public void deleteDynamicResidueModsForResult(int resultId);
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC TERMINAL)
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResultTerminalMod> loadDynamicTerminalModsForResult(int resultId);
    
    public abstract void saveDynamicTerminalModForResult(int resultId, int modificationId);
    
    public abstract void saveDynamicTerminalModForResult(MsResultTerminalModIds modIdentifier);

    public abstract void saveAllDynamicTerminalModsForResult(List<MsResultTerminalModIds> modList);
    
    public void deleteDynamicTerminalModsForResult(int resultId);
    
}