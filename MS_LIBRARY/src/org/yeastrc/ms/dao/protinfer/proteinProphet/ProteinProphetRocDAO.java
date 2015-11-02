/**
 * PeptideProphetRocDAO.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROCPoint;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetRocDAO extends BaseSqlMapDAO {

    private static final String namespace = "ProteinProphetRoc";
    
    public ProteinProphetRocDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public ProteinProphetROC loadRoc(int piRunId) {
        ProteinProphetROC roc = new ProteinProphetROC();
        roc.setProteinferId(piRunId);
        List<ProteinProphetROCPoint> points = super.queryForList(namespace+".select", piRunId);
        roc.setRocPoints(points);
        return roc;
    }

    public void saveRoc(ProteinProphetROC roc) {
        for(ProteinProphetROCPoint point: roc.getRocPoints()) {
            save(namespace+".insert",point);
        }
    }
}
