package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptideBase;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerPeptideBaseDAO extends AbstractIdPickerPeptideDAO<IdPickerPeptideBase> {

    private static final String sqlMapNameSpace = "IdPickerPeptide";
    
    public IdPickerPeptideBaseDAO(SqlMapClient sqlMap,
            ProteinferPeptideDAO peptDao) {
        super(sqlMap, peptDao);
    }

    @Override
    public IdPickerPeptideBase load(int pinferPeptideId) {
        return (IdPickerPeptideBase) super.queryForObject(sqlMapNameSpace+".selectBasePeptide", pinferPeptideId);
    }
    
    @Override
    public List<IdPickerPeptideBase> loadPeptidesForProteinferProtein(int pinferProteinId) {
        return super.queryForList(sqlMapNameSpace+".selectBasePeptidesForProtein", pinferProteinId);
    }
    
    @Override
    public List<IdPickerPeptideBase> loadIdPickerGroupPeptides(int pinferId, int peptideGroupLabel) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("pinferId", pinferId);
        map.put("peptideGroupLabel", peptideGroupLabel);
        return super.queryForList(sqlMapNameSpace+".selectBasePeptidesForGroup", map);
    }
}
