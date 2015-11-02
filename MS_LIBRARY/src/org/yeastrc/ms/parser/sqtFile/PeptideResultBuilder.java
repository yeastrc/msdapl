package org.yeastrc.ms.parser.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;

public interface PeptideResultBuilder {

    public abstract MsSearchResultPeptide build(String resultSequence,
            List<? extends MsResidueModificationIn> dynaResidueMods,
            List<? extends MsTerminalModificationIn> dynaTerminalMods)
            throws SQTParseException;

}