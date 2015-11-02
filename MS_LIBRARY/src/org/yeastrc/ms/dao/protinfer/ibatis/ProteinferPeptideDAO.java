package org.yeastrc.ms.dao.protinfer.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferPeptideDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;

import com.ibatis.sqlmap.client.SqlMapClient;

public class ProteinferPeptideDAO extends BaseSqlMapDAO implements 
                    GenericProteinferPeptideDAO<ProteinferPeptide> {

    private static final String sqlMapNameSpace = "ProteinferPeptide";
    
    
    public ProteinferPeptideDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(GenericProteinferPeptide<?,?> peptide) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", peptide);
    }
    
    public List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProtein", pinferProteinId);
    }
    
    public List<Integer> getUniquePeptideIdsForProteinferProtein(
            int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectUniquePeptideIdsForProtein", pinferProteinId);
    }
    
    public List<ProteinferPeptide> loadPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }
    
    public List<Integer> getPeptideIdsForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptideIdsForProteinferRun", proteinferId);
    }
    
    public List<ProteinferPeptide> loadPeptidesForProteinferRun(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProteinferRun", proteinferId);
    }
    
    public ProteinferPeptide load(int pinferPeptideId) {
        return (ProteinferPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }
    
    @Override
    public ProteinferPeptide loadPeptide(int pinferId, String peptideSequence) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("pinferId", pinferId);
        map.put("sequence", peptideSequence);
        return (ProteinferPeptide) super.queryForObject(sqlMapNameSpace+".selectPeptideForSeq", map);
    }
    
    public void delete(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }

    @Override
    public int update(GenericProteinferPeptide<?,?> peptide) {
        return update(sqlMapNameSpace+".update", peptide);
    }

    @Override
    public int getUniquePeptideSequenceCountForRun(int proteinferId) {
        return (Integer)super.queryForObject(sqlMapNameSpace+".selectUniqPeptSeqForRun", proteinferId);
    }
    
    @Override
    public int getUniqueIonCountForRun(int proteinferId) {
        return (Integer)super.queryForObject(sqlMapNameSpace+".selectUniqIonsForRun", proteinferId);
    }
}
