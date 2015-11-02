package org.yeastrc.ms.dao.protinfer;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinUserValidation;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;

public interface GenericProteinferProteinDAO  <P extends GenericProteinferProtein<?>> {

    public abstract int save(GenericProteinferProtein<?> protein);
    
    public abstract void saveProteinferProteinPeptideMatch(int pinferProteinId, int pinferPeptideId);
    
    public abstract int update(GenericProteinferProtein<?> protein);
    
    public abstract void updateUserAnnotation(int pinferProteinId, String annotation);

    public abstract void updateUserValidation(int pinferProteinId, ProteinUserValidation validation);

    public abstract P loadProtein(int pinferProteinId);
    
    public abstract ProteinferProtein loadProtein(int proteinferId, int nrseqProteinId);

    public abstract List<Integer> getProteinferProteinIds(int proteinferId);
    
    public abstract List<Integer> getNrseqIdsForRun(int proteinferId);
    
    public abstract int getPeptideCountForProtein(int nrseqId, List<Integer> pinferIds);
    
    public abstract List<String> getPeptidesForProtein(int nrseqId, List<Integer> pinferIds);
    
    public abstract List<Integer> getProteinsForPeptide(int pinferId, String peptide, boolean exactMatch);

    public abstract List<P> loadProteins(int proteinferId);
    
//    public abstract List<ProteinferProtein> loadProteinsN(int proteinferId);
    
    public abstract List<Integer> getProteinIdsForNrseqIds(int proteinferId, ArrayList<Integer> nrseqIds);

    public abstract int getProteinCount(int proteinferId);

    public abstract void delete(int pinferProteinId);

}