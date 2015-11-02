package org.yeastrc.ms.dao.protinfer;

import java.util.List;

import org.yeastrc.ms.domain.protinfer.ProteinferInput;

public interface GenericProteinferInputDAO <T extends ProteinferInput>{

    public abstract List<T> loadProteinferInputList(int pinferId);
    
    public abstract int saveProteinferInput(T input);
    
    public abstract List<Integer> loadInputIdsForProteinferRun(int pinferId);
    
    public abstract void deleteProteinferInput(int pinferId);
}
