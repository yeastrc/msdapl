package org.yeastrc.ms.dao.protinfer;

import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;

public interface GenericProteinferPeptideDAO<T extends GenericProteinferPeptide<?,?>> {

    public abstract int save(GenericProteinferPeptide<?,?> peptide);

    public abstract List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId);
    
    public abstract List<Integer> getUniquePeptideIdsForProteinferProtein(int pinferProteinId);

    public abstract List<T> loadPeptidesForProteinferProtein(int pinferProteinId);

    public abstract List<Integer> getPeptideIdsForProteinferRun(int proteinferId);
    
    public abstract int getUniquePeptideSequenceCountForRun(int proteinferId);
    
    public abstract int getUniqueIonCountForRun(int proteinferId);
    
    public abstract T load(int pinferPeptideId);
    
    public abstract ProteinferPeptide loadPeptide(int pinferId, String peptideSequence);

    public abstract int update(GenericProteinferPeptide<?,?> peptide);
    
    public abstract void delete(int id);

}