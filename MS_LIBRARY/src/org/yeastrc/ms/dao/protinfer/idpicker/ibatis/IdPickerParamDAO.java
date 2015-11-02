package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam;

import com.ibatis.sqlmap.client.SqlMapClient;


public class IdPickerParamDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "IdPickerParam";
    
    public IdPickerParamDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveIdPickerParam(IdPickerParam param) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", param);
    }
    
    public List<IdPickerParam> getParamsForIdPickerRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectParamsForRun", pinferId);
    }
    
    public void deleteIdPickerParam(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
