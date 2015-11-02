/**
 * ProteinProphetSpectrumMatch.java
 * @author Vagisha Sharma
 * Mar 1, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * 
 */
public class ProteinProphetSpectrumMatch {

	private final int scanNumber;
    private final double retentionTime;
    private MsSearchResult spectrumMatch;
    private ProteinferSpectrumMatch idpPsm;
    
    public ProteinProphetSpectrumMatch(ProteinferSpectrumMatch idpPsm, MsSearchResult psm, MsScan scan) {
        this.idpPsm = idpPsm;
        this.spectrumMatch = psm;
        this.scanNumber = scan.getStartScanNum();
        this.retentionTime = round(scan.getRetentionTime());
    }

    public int getScanNumber() {
        return scanNumber;
    }

    public double getRetentionTime() {
        return retentionTime;
    }

    public boolean hasPrecursorArea() {
        return false;
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
