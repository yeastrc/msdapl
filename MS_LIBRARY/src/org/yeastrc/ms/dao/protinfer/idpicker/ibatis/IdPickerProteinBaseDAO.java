package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerProteinBaseDAO extends AbstractIdPickerProteinDAO<IdPickerProteinBase> {

private static final String sqlMapNameSpace = "IdPickerProtein";
    
    public IdPickerProteinBaseDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao) {
        super(sqlMap, protDao);
    }

    public IdPickerProteinBase loadProtein(int pinferProteinId) {
        return (IdPickerProteinBase) super.queryForObject(sqlMapNameSpace+".selectBaseProtein", pinferProteinId);
    }
    
    public List<IdPickerProteinBase> loadProteins(int proteinferId) {
        return queryForList(sqlMapNameSpace+".selectBaseProteinsForProteinferRun", proteinferId);
    }
    
    public List<IdPickerProteinBase> loadIdPickerClusterProteins(int pinferId,int clusterLabel) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("clusterLabel", clusterLabel);
        return queryForList(sqlMapNameSpace+".selectBaseProteinsForCluster", map);
    }
    
    public List<IdPickerProteinBase> loadIdPickerGroupProteins(int pinferId,int proteinGroupLabel) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("proteinGroupLabel", proteinGroupLabel);
        return queryForList(sqlMapNameSpace+".selectBaseProteinsForGroup", map);
    }
}
