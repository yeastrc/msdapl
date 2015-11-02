package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.protinfer.GenericProteinferPeptideDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.GenericIdPickerPeptide;

public interface GenericIdPickerPeptideDAO <T extends GenericIdPickerPeptide<?,?>> extends GenericProteinferPeptideDAO<T> {

    public abstract int saveIdPickerPeptide(GenericIdPickerPeptide<?,?> peptide);
    
    public abstract int updateIdPickerPeptide(GenericIdPickerPeptide<?,?> peptide);
    
    public abstract List<T> loadIdPickerGroupPeptides(int pinferId, int groupId);
    
    public abstract List<Integer> getMatchingPeptGroupLabels(int pinferId, int proteinGroupId); 
}
