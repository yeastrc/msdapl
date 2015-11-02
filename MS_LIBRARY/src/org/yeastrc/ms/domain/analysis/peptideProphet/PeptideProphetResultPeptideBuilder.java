/**
 * PeptideProphetResultPeptideBuilder.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.proteinProphet.Modification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;

/**
 * 
 */
public class PeptideProphetResultPeptideBuilder {

    private static PeptideProphetResultPeptideBuilder instance;
    
    private PeptideProphetResultPeptideBuilder() {}
    
    public static PeptideProphetResultPeptideBuilder getInstance() {
        if(instance == null) {
            instance = new PeptideProphetResultPeptideBuilder();
        }
        return instance;
    }
    
    public MsSearchResultPeptide buildResultPeptide(String strippedSequence, char preResidue,
            char postResidue, List<Modification> modificationList) {
        
        SearchResultPeptideBean peptide = new SearchResultPeptideBean();
        peptide.setPeptideSequence(strippedSequence);
        peptide.setPreResidue(preResidue);
        peptide.setPostResidue(postResidue);
        List<MsResultResidueMod> mods = new ArrayList<MsResultResidueMod>(modificationList.size());
        List<MsResultTerminalMod> termMods = new ArrayList<MsResultTerminalMod>();
        for(Modification mod: modificationList) {
            if(!mod.isTerminalModification()) {
                ResultResidueModBean mb = new ResultResidueModBean();
                char modRes = strippedSequence.charAt(mod.getPosition());
                mb.setModificationMass(mod.getMass());
                mb.setModifiedPosition(mod.getPosition());
                mb.setModifiedResidue(modRes);
                mods.add(mb);
            }
            else {
                ResultTerminalModBean mb = new ResultTerminalModBean();
                mb.setModificationMass(mod.getMass());
                mb.setModifiedTerminal(mod.getTerminus());
                termMods.add(mb);
            }
        }
        peptide.setDynamicResidueModifications(mods);
        peptide.setDynamicTerminalModifications(termMods);
        return peptide;
    }
}
