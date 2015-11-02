package org.yeastrc.ms.domain.protinfer;

import java.io.Serializable;

public class PeptideDefinition implements Serializable {

    private boolean useMods;
    private boolean useCharge;
    
    public PeptideDefinition() {}
    
    public PeptideDefinition(boolean useMods, boolean useCharge) {
        this.useMods = useMods;
        this.useCharge = useCharge;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Sequence");
        if(useMods)
            buf.append(" + Modifications");
        if(useCharge)
            buf.append(" + Charge");
        return buf.toString();
    }
    
    
    public boolean isUseMods() {
        return useMods;
    }
    public void setUseMods(boolean useMods) {
        this.useMods = useMods;
    }
    public boolean isUseCharge() {
        return useCharge;
    }
    public void setUseCharge(boolean useCharge) {
        this.useCharge = useCharge;
    }
    
    public boolean equals(Object o) {
        if(this == o)
            return true;
        
        if(!(o instanceof PeptideDefinition))
            return false;
        
        PeptideDefinition that = (PeptideDefinition) o;
        return (this.useCharge == that.useCharge && this.useMods == that.useMods);
    }
}
