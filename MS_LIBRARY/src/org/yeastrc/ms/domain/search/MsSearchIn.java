/**
 * MsSearch.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeIn;

/**
 * 
 */
public interface MsSearchIn extends MsSearchBase {

    /**
     * @return the searchDatabases
     */
    public abstract List<MsSearchDatabaseIn> getSearchDatabases();
    
    /**
     * @return the static residue modifications
     */
    public abstract List<MsResidueModificationIn> getStaticResidueMods();

    /**
     * @return the dynamic residue modifications
     */
    public abstract List<MsResidueModificationIn> getDynamicResidueMods();
    
    /**
     * @return the static terminal modifications
     */
    public abstract List<MsTerminalModificationIn> getStaticTerminalMods();

    /**
     * @return the dynamic terminal modifications
     */
    public abstract List<MsTerminalModificationIn> getDynamicTerminalMods();
    
    
    
    /**
     * @return the enzymes used for this search
     */
    public abstract List<MsEnzymeIn> getEnzymeList();
   
}

