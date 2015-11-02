/**
 * SearchParamsDataProvider.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public interface SearchParamsDataProvider {

    public abstract MsSearchDatabaseIn getSearchDatabase();

    public abstract MsEnzymeIn getSearchEnzyme();

    public abstract List<MsResidueModificationIn> getDynamicResidueMods();

    public abstract List<MsResidueModificationIn> getStaticResidueMods();

    public abstract List<MsTerminalModificationIn> getStaticTerminalMods();

    public abstract List<MsTerminalModificationIn> getDynamicTerminalMods();
    
    public abstract Program getSearchProgram();
    
    public abstract void parseParams(String remoteServer, String paramFileDir) throws DataProviderException;
    
    public abstract String paramsFileName(); 
}
