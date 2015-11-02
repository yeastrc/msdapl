package org.yeastrc.www.proteinfer.idpicker;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

public class WIdPickerSpectrumMatch {
    
    private final int scanNumber;
    private final double retentionTime;
    private double precursorArea = -1.0;
    private MsSearchResult spectrumMatch;
    private ProteinferSpectrumMatch idpPsm;
    
    public WIdPickerSpectrumMatch(ProteinferSpectrumMatch idpPsm, MsSearchResult psm, MsScan scan) {
        this.idpPsm = idpPsm;
        this.spectrumMatch = psm;
        this.scanNumber = scan.getStartScanNum();
        this.retentionTime = round(scan.getRetentionTime());
        if(scan instanceof MS2Scan) {
            precursorArea = round(((MS2Scan)scan).getBullsEyeArea());
        }
    }

    public int getScanNumber() {
        return scanNumber;
    }

    public double getRetentionTime() {
        return retentionTime;
    }

    public double getPrecursorArea() {
        return precursorArea;
    }
    
    public boolean hasPrecursorArea() {
        return (this.precursorArea != -1.0);
    }
    
    public int getScanId() {
        return spectrumMatch.getScanId();
    }
    
    public int getResultId() {
        return idpPsm.getResultId();
    }
    
    public ProteinferSpectrumMatch getProteinferSpectrumMatch() {
        return idpPsm;
    }

    public MsSearchResult getSpectrumMatch() {
        return spectrumMatch;
    }

    public String getModifiedSequence() {
        try {
            return removeTerminalResidues(spectrumMatch.getResultPeptide().getModifiedPeptide());
        }
        catch (ModifiedSequenceBuilderException e) {
            return null;
        }
    }
    
    private static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }
    
    private static double round(BigDecimal number) {
        return round(number.doubleValue());
    }
    private static double round(double num) {
        return Math.round(num*100.0)/100.0;
    }
}
