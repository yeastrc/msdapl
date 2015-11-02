package org.yeastrc.ms.domain.protinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.ProteinferInput;

public class IdPickerInput extends ProteinferInput {

    private int numTargetHits = 0;
    private int numDecoyHits = 0;
    private int numFilteredTargetHits = 0;
    private int numFilteredDecoyHits = 0;
    
    public int getNumTargetHits() {
        return numTargetHits;
    }
    public void setNumTargetHits(int numTargetHits) {
        this.numTargetHits = numTargetHits;
    }
    
    public int getNumDecoyHits() {
        return numDecoyHits;
    }
    public void setNumDecoyHits(int numDecoyHits) {
        this.numDecoyHits = numDecoyHits;
    }
    
    public int getNumFilteredTargetHits() {
        return numFilteredTargetHits;
    }
    public void setNumFilteredTargetHits(int numFilteredTargetHits) {
        this.numFilteredTargetHits = numFilteredTargetHits;
    }
    
    public int getNumFilteredDecoyHits() {
        return numFilteredDecoyHits;
    }
    public void setNumFilteredDecoyHits(int numFilteredDecoyHits) {
        this.numFilteredDecoyHits = numFilteredDecoyHits;
    }
}
