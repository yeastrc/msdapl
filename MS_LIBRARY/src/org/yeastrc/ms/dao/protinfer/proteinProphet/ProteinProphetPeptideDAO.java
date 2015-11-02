/**
 * ProteinProphetPeptideDAO.java
 * @author Vagisha Sharma
 * Aug 14, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptide;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetPeptideDAO extends BaseSqlMapDAO  implements 
    GenericProteinferPeptideDAO<ProteinProphetProteinPeptide> {

    private static final String sqlMapNameSpace = "ProteinProphetPeptide";
    
    private final ProteinferPeptideDAO peptDao;
    
    public ProteinProphetPeptideDAO(SqlMapClient sqlMap, ProteinferPeptideDAO peptDao) {
        super(sqlMap);
        this.peptDao = peptDao;
    }

    public List<ProteinProphetProteinPeptide> loadPeptidesForProteinferProtein(int pinferProteinId) {
        return queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }

    @Override
    public void delete(int id) {
        peptDao.delete(id);
    }

    @Override
    public List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId) {
        return peptDao.getPeptideIdsForProteinferProtein(pinferProteinId);
    }

    @Override
    public List<Integer> getPeptideIdsForProteinferRun(int proteinferId) {
        return peptDao.getPeptideIdsForProteinferRun(proteinferId);
    }

    @Override
    public List<Integer> getUniquePeptideIdsForProteinferProtein(
            int pinferProteinId) {
        return peptDao.getUniquePeptideIdsForProteinferProtein(pinferProteinId);
    }

    @Override
    public int getUniquePeptideSequenceCountForRun(int proteinferId) {
        return peptDao.getUniquePeptideSequenceCountForRun(proteinferId);
    }
    
    public int getUniquePeptideSequenceCountForProphetGroupProbability(int pinferId, double prophetGroupProbability) 
    {
    	Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("pinferId", pinferId);
        map.put("minProbability", prophetGroupProbability);
        return (Integer)queryForObject(sqlMapNameSpace+".peptideSequenceCountForProphetGroupProbability", map);
    }
    
    @Override
    public int getUniqueIonCountForRun(int proteinferId) {
        return peptDao.getUniqueIonCountForRun(proteinferId);
    }
    
    public int getIonCountForProphetGroupProbability(int pinferId, double prophetGroupProbability) 
    {
    	Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("pinferId", pinferId);
        map.put("minProbability", prophetGroupProbability);
        return (Integer)queryForObject(sqlMapNameSpace+".ionCountForProphetGroupProbability", map);
    }
    
    @Override
    public ProteinProphetProteinPeptide load(int pinferPeptideId) {
        return (ProteinProphetProteinPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }

    @Override
    public ProteinferPeptide loadPeptide(int pinferId, String peptideSequence) {
        return peptDao.loadPeptide(pinferId, peptideSequence);
    }

    @Override
    public int save(GenericProteinferPeptide<?, ?> peptide) {
        return peptDao.save(peptide);
    }

    @Override
    public int update(GenericProteinferPeptide<?, ?> peptide) {
        return peptDao.update(peptide);
    }
}
