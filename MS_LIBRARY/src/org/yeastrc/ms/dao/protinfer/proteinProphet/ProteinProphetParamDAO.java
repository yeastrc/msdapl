/**
 * ProteinProphetParamDAO.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetParam;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetParamDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinProphetParam";
    
    public ProteinProphetParamDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveProteinProphetParam(ProteinProphetParam param) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", param);
    }
    
    public List<ProteinProphetParam> getParamsForProteinProphetRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectParamsForRun", pinferId);
    }
    
    public void deleteProteinProphetParam(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
