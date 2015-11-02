package org.yeastrc.ms.dao.protinfer.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferIonDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;

import com.ibatis.sqlmap.client.SqlMapClient;

public class ProteinferIonDAO extends BaseSqlMapDAO implements GenericProteinferIonDAO<ProteinferIon> {

    private static final String sqlMapNameSpace = "ProteinferIon";
    
    public ProteinferIonDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public int save(GenericProteinferIon<?> ion) {
        return saveAndReturnId(sqlMapNameSpace+".insert", ion);
    }
    
    @Override
    public List<ProteinferIon> loadIonsForPeptide(int pinferPeptideId) {
        return queryForList(sqlMapNameSpace+".selectIonsForPeptide", pinferPeptideId);
    }

    @Override
    public ProteinferIon load(int pinferIonId) {
        return (ProteinferIon) queryForObject(sqlMapNameSpace+".select", pinferIonId);
    }
}
