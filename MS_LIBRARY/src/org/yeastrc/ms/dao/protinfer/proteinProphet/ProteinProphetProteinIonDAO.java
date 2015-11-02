/**
 * ProteinProphetProteinIonDAO.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetProteinIonDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinProphetProteinIon";
    
    public ProteinProphetProteinIonDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public void save(ProteinProphetProteinPeptideIon proteinIon) {
        super.save(sqlMapNameSpace+".insert", proteinIon);
    }
}
