/**
 * ProteinProphetProteinGroupDAO.java
 * @author Vagisha Sharma
 * Jul 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetProteinGroupDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinProphetProteinGroup";
    
    public ProteinProphetProteinGroupDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public int saveGroup(ProteinProphetGroup group) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", group);
    }
    
    public ProteinProphetGroup load(int proteinProphetGroupId) {
        return (ProteinProphetGroup) super.queryForObject(sqlMapNameSpace+".select", proteinProphetGroupId);
    }
    
    public List<Integer> loadProphetGroupIds(int pinferId) {
    	return queryForList(sqlMapNameSpace+".selectGroupIds", pinferId);
    }
    
    public int getProphetGroupCountForProbability(int pinferId, double prophetGroupProbability)
    {
    	Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("pinferId", pinferId);
        map.put("minProbability", prophetGroupProbability);
        return (Integer)queryForObject(sqlMapNameSpace+".prophetGroupCountByGroupProbability", map);
    }
}
