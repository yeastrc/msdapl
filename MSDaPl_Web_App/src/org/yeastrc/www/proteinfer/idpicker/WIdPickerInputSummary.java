package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;

public class WIdPickerInputSummary {

    private IdPickerInput idpInput;
    private String fileName;
    
    public WIdPickerInputSummary(IdPickerInput idpInput) {
        this.idpInput = idpInput;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public IdPickerInput getInput() {
        return idpInput;
    }
    
    public int getNumHits() {
        return idpInput.getNumTargetHits();
    }
    
    public int getNumFilteredHits() {
        return idpInput.getNumFilteredTargetHits();
    }
    
    public double getPercentFilteredHits() {
        return round(getNumFilteredHits()*100.0 / (double)getNumHits());
    }
    
    private static double round(double num) {
        return Math.round(num*100.0)/100.0;
    }
}
