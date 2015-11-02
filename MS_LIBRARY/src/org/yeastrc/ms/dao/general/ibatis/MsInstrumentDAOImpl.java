/**
 * InstrumentDao.java
 * @author Vagisha Sharma
 * Oct 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.general.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.general.MsInstrumentDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.general.MsInstrument;

import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * 
 */
public class MsInstrumentDAOImpl extends BaseSqlMapDAO implements MsInstrumentDAO {

    public MsInstrumentDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveInstrument(MsInstrument instrument) {
        return saveAndReturnId("MsInstrument.insert", instrument);
    }
    
    public void updateInstrument(MsInstrument instrument) {
        update("MsInstrument.update", instrument);
    }
    
    public void deleteInstrument(int instrumentId) {
        delete("MsInstrument.delete", instrumentId);
    }
    
    public MsInstrument load(int id) {
        return (MsInstrument) queryForObject("MsInstrument.select", id);
    }
    
    public List<MsInstrument> loadAllInstruments() throws SQLException {
        return queryForList("MsInstrument.selectAll");
    }
}
