package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptide;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerPeptideDAO extends AbstractIdPickerPeptideDAO<IdPickerPeptide> {

    private static final String sqlMapNameSpace = "IdPickerPeptide";
    

    public IdPickerPeptideDAO(SqlMapClient sqlMap, ProteinferPeptideDAO peptDao) {
        super(sqlMap, peptDao);
    }
    
    public IdPickerPeptide load(int pinferPeptideId) {
        return (IdPickerPeptide) super.queryForObject(sqlMapNameSpace+".select", pinferPeptideId);
    }
    
    public List<IdPickerPeptide> loadPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForProtein", pinferProteinId);
    }
    
    public List<IdPickerPeptide> loadIdPickerGroupPeptides(int pinferId, int peptideGroupLabel) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("peptideGroupLabel", peptideGroupLabel);
        return super.queryForList(sqlMapNameSpace+".selectPeptidesForGroup", map);
    }
    
    
    
//    public IdPickerPeptideGroup getIdPickerPeptideGroup(int pinferId, int groupId) {
//        List<IdPickerPeptide> grpPeptides = getIdPickerGroupPeptides(pinferId, groupId);
//        IdPickerPeptideGroup group = new IdPickerPeptideGroup(groupId);
//        group.setPeptides(grpPeptides);
//        List<Integer> matchingProtGrpIds = getMatchingProtGroupIds(pinferId, groupId);
//        group.setMatchingProteinGroupIds(matchingProtGrpIds);
//        return group;
//    }
}
