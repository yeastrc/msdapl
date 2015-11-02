/**
 * 
 */
package org.yeastrc.ms.dao.protinfer.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRunSummary;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ProteinferRunSummaryDAO.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public class ProteinferRunSummaryDAO extends BaseSqlMapDAO {

	private static final String sqlMapNameSpace = "ProteinferRunSummary";
    
    public ProteinferRunSummaryDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public void save(ProteinferRunSummary summary) {
    	super.save(sqlMapNameSpace+".insert", summary);
    }
    
    public ProteinferRunSummary load(int proteinferId) {
        return (ProteinferRunSummary) super.queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    public void update(ProteinferRunSummary summary) {
    	super.update(sqlMapNameSpace+".update", summary);
    }
    
    public void delete(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
}
