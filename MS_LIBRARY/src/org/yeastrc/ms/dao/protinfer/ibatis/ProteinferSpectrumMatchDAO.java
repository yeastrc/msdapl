package org.yeastrc.ms.dao.protinfer.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferSpectrumMatchDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

import com.ibatis.sqlmap.client.SqlMapClient;

public class ProteinferSpectrumMatchDAO extends BaseSqlMapDAO implements GenericProteinferSpectrumMatchDAO<ProteinferSpectrumMatch> {

    private static final String sqlMapNameSpace = "ProteinferSpectrumMatch";

    public ProteinferSpectrumMatchDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveSpectrumMatch(ProteinferSpectrumMatch spectrumMatch) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", spectrumMatch);
    }
    
    public ProteinferSpectrumMatch loadSpectrumMatch(int psmId) {
        return (ProteinferSpectrumMatch) queryForObject(sqlMapNameSpace+".select", psmId);
    }
    
    @Override
    public List<ProteinferSpectrumMatch> loadSpectrumMatchesForIon(int pinferIonId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForIon", pinferIonId);
    }
    
    @Override
    public ProteinferSpectrumMatch loadBestSpectrumMatchForIon(int pinferIonId) {
        return (ProteinferSpectrumMatch) queryForObject(sqlMapNameSpace+".selectBestMatchForIon", pinferIonId);
    }
    
    public List<ProteinferSpectrumMatch> loadSpectrumMatchesForPeptide(int pinferPeptideId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForPeptide", pinferPeptideId);
    }
    
    public List<Integer> getSpectrumMatchIdsForPinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchIdsForPinferId", pinferId);
    }
    
    public int getSpectrumCountForPinferRun(int pinferId) {
        Integer count = (Integer)queryForObject(sqlMapNameSpace+".selectSpectrumCountForPinferId", pinferId);
        if(count != null)   return count;
        return 0;
    }
    
    public int getMaxSpectrumCountForPinferRunProtein(int pinferId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("pinferId", pinferId);
        map.put("max", 1);
        Integer count = (Integer)queryForObject(sqlMapNameSpace+".selectSpectrumCountForPinferIdProtein", map);
        if(count != null)   return count;
        return 0;
    }
    
    public int getMinSpectrumCountForPinferRunProtein(int pinferId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("pinferId", pinferId);
        Integer count = (Integer)queryForObject(sqlMapNameSpace+".selectSpectrumCountForPinferIdProtein", map);
        if(count != null)   return count;
        return 0;
    }
    
    public int update(ProteinferSpectrumMatch psm) {
       return update(sqlMapNameSpace+".update", psm);
    }
}
