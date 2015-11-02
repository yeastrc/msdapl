package org.yeastrc.ms.dao.search.sqtfile;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

public interface SQTSearchScanDAO {

    public abstract SQTSearchScan load(int runSearchId, int scanId, int charge, BigDecimal observedMass);
    
    public abstract void save(SQTSearchScan scanData);
    
    public abstract void saveAll(List<SQTSearchScan> scanDataList);

    public abstract void deleteForRunSearch(int runSearchId);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;

}