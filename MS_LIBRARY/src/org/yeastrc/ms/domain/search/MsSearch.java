/**
 * MsSearchDb.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;

/**
 * 
 */
public interface MsSearch extends MsSearchBase {

    /**
     * @return the database id for this search
     */
    public abstract int getId();
    
    /**
     * @return database id of the experiment to which this search belongs.
     */
    public abstract int getExperimentId();
    
    /**
     * @return the date this search was uploaded
     */
    public abstract Date getUploadDate();
    
    /**
     * @return the searchDatabases
     */
    public abstract List<MsSearchDatabase> getSearchDatabases();

    /**
     * @return the staticModifications
     */
    public abstract List<MsResidueModification> getStaticResidueMods();

    /**
     * @return the dynamicModifications
     */
    public abstract List<MsResidueModification> getDynamicResidueMods();
    
    /**
     * @return the terminal static modifications
     */
    public abstract List<MsTerminalModification> getStaticTerminalMods();
    
    /**
     * @return the terminal dynamic modifications
     */
    public abstract List<MsTerminalModification> getDynamicTerminalMods();
    
    /**
     * @return the enzymes used for this search
     */
    public abstract List<MsEnzyme> getEnzymeList();
}
