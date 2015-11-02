/**
 * Ms2FileRunHeadersDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2HeaderDAOImpl extends BaseSqlMapDAO implements MS2HeaderDAO {

    public MS2HeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void save(MS2NameValuePair header, int runId) {
        MS2HeaderWrap headerDb = new MS2HeaderWrap(header, runId);
        save("MS2Header.insert", headerDb);
    }
    
    public List<MS2NameValuePair> loadHeadersForRun(int runId) {
        return queryForList("MS2Header.selectHeadersForRun", runId);
    }

    public void deleteHeadersForRunId(int runId) {
        delete("MS2Header.deleteByRunId", runId);
    }
}
