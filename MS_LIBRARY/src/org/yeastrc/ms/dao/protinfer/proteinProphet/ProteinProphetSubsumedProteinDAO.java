/**
 * ProteinProphetSubsumedProteinDAO.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.HashMap;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetSubsumedProteinDAO extends BaseSqlMapDAO {

    private static final String namespace = "ProteinProphetSubsumedProtein";
    
    public ProteinProphetSubsumedProteinDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public void saveSubsumedProtein(int subsumedProteinId, int subsumingProteinId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(6);
        map.put("subsumedProteinId", subsumedProteinId);
        map.put("subsumingProteinId", subsumingProteinId);
        super.save(namespace+".insert", map);
    }

}
