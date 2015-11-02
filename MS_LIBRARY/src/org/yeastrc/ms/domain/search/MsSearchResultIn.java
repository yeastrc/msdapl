package org.yeastrc.ms.domain.search;

import java.util.List;


public interface MsSearchResultIn extends MsRunSearchResultBase {

    /**
     * @return the scan number for this result
     */
    public abstract int getScanNumber();
    
    public abstract void setScanNumber(int scanNumber);
    
    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProteinIn> getProteinMatchList();
    
    
    public void addMatchingProteinMatch(MsSearchResultProteinIn match) ;

}

