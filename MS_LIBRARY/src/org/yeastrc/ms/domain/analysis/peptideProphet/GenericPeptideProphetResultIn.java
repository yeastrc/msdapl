/**
 * GenericPeptideProphetResultIn.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

import org.yeastrc.ms.domain.search.MsSearchResultIn;

/**
 * 
 */
public interface GenericPeptideProphetResultIn <T extends MsSearchResultIn>
    extends PeptideProphetResultDataIn {

    public void setSearchResult(T searchResult);

    public T getSearchResult();

    public void setPeptideProphetResult(PeptideProphetResultDataIn ppRes);

}
