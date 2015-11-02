package org.yeastrc.ms.dao.protinfer;

import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferInput;
import org.yeastrc.ms.domain.search.Program;

public interface GenericProteinferRunDAO <S extends ProteinferInput, T extends GenericProteinferRun<S>>{

    public abstract int save(GenericProteinferRun<?> run);
    
    public abstract void update(GenericProteinferRun<?> run);

    public abstract T loadProteinferRun(int proteinferId);
    
    public abstract int getMaxProteinHitCount(int proteinferId);
    
    public abstract List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds);
    
    public abstract List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds, Program inputGenerator);

    public abstract List<Integer> loadSearchIdsForProteinferRun(int pinferId);
    
    public abstract List<Integer> loadProteinferIdsForExperiment(int experimentId);
    
    public abstract void delete(int pinferId);

}