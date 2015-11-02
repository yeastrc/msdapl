package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptide;

public class WIdPickerPeptide {

    private int scanId;
    private IdPickerPeptide peptide;
    private boolean uniqueToProteinGrp = false;
    
    public WIdPickerPeptide(IdPickerPeptide peptide) {
        this.peptide = peptide;
    }

    public int getScanId() {
        return scanId;
    }

    public void setScanId(int scanId) {
        this.scanId = scanId;
    }

    public IdPickerPeptide getPeptide() {
        return peptide;
    }

    public void setPeptide(IdPickerPeptide peptide) {
        this.peptide = peptide;
    }
    
    public boolean getIsUniqueToProteinGroup() {
        return uniqueToProteinGrp;
    }
    
    public void setIsUniqueToProteinGroup(boolean isUnique) {
        this.uniqueToProteinGrp = isUnique;
    }
}
