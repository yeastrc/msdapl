package org.yeastrc.ms.parser.sqtFile;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.impl.SearchResult;

/**
 * Represents a 'M' line in the SQT file
 */
public abstract class SQTSearchResult extends SearchResult {

    private MsSearchResultPeptide resultPeptide = null;

    public SQTSearchResult() {
        super();
    }

    public MsSearchResultPeptide getResultPeptide() {
        if (resultPeptide != null)
            return resultPeptide;
        try {
            resultPeptide = buildPeptideResult();
        }
        catch (SQTParseException e) {
           throw new RuntimeException("Error building result peptide",e);
        }
        return resultPeptide;
    }
   
   

    public abstract MsSearchResultPeptide buildPeptideResult() throws SQTParseException;
    
   
}