/**
 * SQTSearchHeaderDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sqtfile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTHeaderDAOImpl extends BaseSqlMapDAO implements SQTHeaderDAO {

    public SQTHeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    
    public List<SQTHeaderItem> loadSQTHeadersForRunSearch(int runSearchId) {
        return queryForList("SqtHeader.selectHeadersForRunSearch", runSearchId);
    }
    
    
    public void saveSQTHeader(SQTHeaderItem header) {
        save("SqtHeader.insertHeader",header);
    }
    
    
    public void deleteSQTHeadersForRunSearch(int runSearchId) {
        delete("SqtHeader.deleteHeadersForRunSearch", runSearchId);
    }
}
