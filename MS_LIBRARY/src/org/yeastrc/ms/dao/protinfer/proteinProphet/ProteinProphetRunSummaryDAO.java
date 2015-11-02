/**
 * 
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRunSummary;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ProteinProphetRunSummaryDAO.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public class ProteinProphetRunSummaryDAO extends BaseSqlMapDAO {

	private static final String sqlMapNameSpace = "ProteinProphetRunSummary";
    
    public ProteinProphetRunSummaryDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public void save(ProteinProphetRunSummary summary) {
    	super.save(sqlMapNameSpace+".insert", summary);
    }
    
    public ProteinProphetRunSummary load(int proteinferId) {
        return (ProteinProphetRunSummary) super.queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    public void update(ProteinProphetRunSummary summary) {
    	super.update(sqlMapNameSpace+".update", summary);
    }
    
    public void delete(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
}
