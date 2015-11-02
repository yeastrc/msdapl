package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

public class WIdPickerIonForProtein extends WIdPickerIon {

    private List<Character> ntermResidues = new ArrayList<Character>();
    private List<Character> cTermResidues = new ArrayList<Character>();
    
    public WIdPickerIonForProtein(GenericProteinferIon<? extends ProteinferSpectrumMatch> ion, 
            MsSearchResult psm, MsScan bestScan) {
        super(ion, psm, bestScan);
    }
    
    public void addTerminalResidues(char nterm, char cterm) {
        this.ntermResidues.add(nterm);
        this.cTermResidues.add(cterm);
    }
    
    public String getIonSequence() {
        
        if(getBestSpectrumMatch() == null)
            return null;
        String seq;
        try {
        	// get modified peptide of the form: PEP[+80]TIDE
            seq = removeTerminalResidues(getBestSpectrumMatch().getResultPeptide().getModifiedPeptide(true));
        }
        catch (ModifiedSequenceBuilderException e) {
            return null;
        }
        seq = "."+seq+".";
        for(int i = 0; i < ntermResidues.size(); i++) {
            seq = "("+ntermResidues.get(i)+")"+seq+"("+cTermResidues.get(i)+")";
        }
        return seq;
    }
}
