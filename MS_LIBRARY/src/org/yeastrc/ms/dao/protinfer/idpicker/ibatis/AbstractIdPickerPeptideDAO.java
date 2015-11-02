package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.idpicker.GenericIdPickerPeptide;

import com.ibatis.sqlmap.client.SqlMapClient;

public abstract class AbstractIdPickerPeptideDAO <T extends GenericIdPickerPeptide<?,?>>
        extends BaseSqlMapDAO
        implements GenericIdPickerPeptideDAO<T> {

    private static final String sqlMapNameSpace = "IdPickerPeptide";
    
    private final ProteinferPeptideDAO peptDao;
    
    public AbstractIdPickerPeptideDAO(SqlMapClient sqlMap, ProteinferPeptideDAO peptDao) {
        super(sqlMap);
        this.peptDao = peptDao;
    }

    public int save(GenericProteinferPeptide<?,?> peptide) {
        return peptDao.save(peptide); 
     }
     
     public int saveIdPickerPeptide(GenericIdPickerPeptide<?,?> peptide) {
         int id = save(peptide);
         peptide.setId(id);
         save(sqlMapNameSpace+".insert", peptide);
         return id;
     }
     
     public List<Integer> getMatchingPeptGroupLabels(int pinferId, int proteinGroupLabel) {
         Map<String, Integer> map = new HashMap<String, Integer>(2);
         map.put("pinferId", pinferId);
         map.put("proteinGroupLabel", proteinGroupLabel);
         return super.queryForList(sqlMapNameSpace+".selectPeptGrpLabelsForProtGrp", map);
     }
     
     public void delete(int id) {
         peptDao.delete(id);
     }

     public List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId) {
         return peptDao.getPeptideIdsForProteinferProtein(pinferProteinId);
     }
     
     public List<Integer> getUniquePeptideIdsForProteinferProtein(int pinferProteinId) {
         return peptDao.getUniquePeptideIdsForProteinferProtein(pinferProteinId);
     }
     
     @Override
     public int getUniqueIonCountForRun(int proteinferId) {
         return peptDao.getUniqueIonCountForRun(proteinferId);
     }
     
     public List<Integer> getPeptideIdsForProteinferRun(int proteinferId) {
         return peptDao.getPeptideIdsForProteinferRun(proteinferId);
     }
     
     @Override
     public int getUniquePeptideSequenceCountForRun(int proteinferId) {
         return peptDao.getUniquePeptideSequenceCountForRun(proteinferId);
     }
     
     @Override
     public ProteinferPeptide loadPeptide(int pinferId, String peptideSequence) {
        return peptDao.loadPeptide(pinferId, peptideSequence);
     }
     
     @Override
     public int update(GenericProteinferPeptide<?,?> peptide) {
         return peptDao.update(peptide);
     }
     
     public int updateIdPickerPeptide(GenericIdPickerPeptide<?,?> peptide) {
         int updated = update(peptide);
         if(updated > 0)
             return update(sqlMapNameSpace+".updateIdPickerPeptide", peptide);
         return 0;
     }
     
}
