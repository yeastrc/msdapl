package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.protinfer.GenericProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.idpicker.GenericIdPickerProtein;


public interface GenericIdPickerProteinDAO <P extends GenericIdPickerProtein<?>> extends GenericProteinferProteinDAO<P> {

    public abstract int saveIdPickerProtein(GenericIdPickerProtein<?> protein);
    
    public abstract int updateIdPickerProtein(GenericIdPickerProtein<?> protein);
    
    public abstract int updateIdPickerProteinOnly(GenericIdPickerProtein<?> protein);
    
    public abstract int updateProteinSubsetValue(int proteinferId, boolean isSubset);
    
    public abstract boolean proteinPeptideGrpAssociationExists(int pinferId, int proteinGrpLabel, int peptideGrpLabel);
    
    public abstract void saveProteinPeptideGroupAssociation(int pinferId, int proteinGrpLabel, int peptideGrpLabel);
    
    public abstract void saveSubsetProtein(int subsetProteinId, int superProteinId);
    
    public abstract List<P> loadIdPickerClusterProteins(int pinferId,int clusterLabel);
    
    public abstract List<P> loadIdPickerGroupProteins(int pinferId,int groupLabel);
    
    public abstract List<Integer> getIdPickerGroupProteinIds(int pinferId, int groupLabel);
    
    public abstract List<Integer> getGroupLabelsForCluster(int pinferId, int clusterLabel);
    
    public abstract List<Integer> getClusterLabels(int pinferId);
    
    public abstract int getFilteredParsimoniousProteinCount(int proteinferId);
    
    public abstract int getIdPickerGroupCount(int pinferId);
    
    public abstract int getIdPickerParsimoniousGroupCount(int pinferId);
    
    public abstract int getIdPickerNonSubsetGroupCount(int pinferId);
    
    public abstract List<Integer> getFilteredSortedProteinIds(int pinferId, ProteinFilterCriteria filterCriteria);
    
    public abstract List<Integer> getFilteredNrseqIds(int pinferId, ProteinFilterCriteria filterCriteria);

    public abstract List<Integer> sortProteinIdsByCoverage(int pinferId, boolean groupProteins, SORT_ORDER sortOrder);
    
    public abstract List<Integer> sortProteinsByNSAF(int pinferId, boolean groupProteins, SORT_ORDER sortOrder);
    
    public abstract List<Integer> sortProteinIdsByValidationStatus(int pinferId);
    
    public abstract List<Integer> sortProteinIdsBySpectrumCount(int pinferId, boolean groupProteins);
    
    public abstract List<Integer> sortProteinIdsByPeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins);
    
    public abstract List<Integer> sortProteinIdsByUniquePeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins);

    public abstract List<Integer> sortProteinIdsByCluster(int pinferId);
    
    public abstract List<Integer> sortProteinIdsByGroup(int pinferId);
    
    public abstract List<Integer> getIdPickerParsimoniousProteinIds(int pinferId);
    
    public abstract List<Integer> getIdPickerNonSubsetProteinIds(int pinferId);
    
    public  abstract List<Integer> getParsimoniousNrseqProteinIds(int pinferId);
    
    public  abstract List<Integer> getNonParsimoniousNrseqProteinIds(int pinferId);
    
    public  abstract List<Integer> getNonSubsetNrseqProteinIds(int pinferId);
    
    public  abstract List<Integer> getSubsetNrseqProteinIds(int pinferId);
    
    public abstract Map<Integer, Integer> getProteinGroupLabels(int pinferId);
    
    public abstract boolean isNrseqProteinGrouped(int pinferId, int nrseqId);
    
    public abstract boolean isProteinGrouped(int pinferProteinId);
    
}
