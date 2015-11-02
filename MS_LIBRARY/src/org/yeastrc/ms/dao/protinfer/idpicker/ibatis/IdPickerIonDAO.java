package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferIonDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerIon;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerIonDAO extends BaseSqlMapDAO implements GenericProteinferIonDAO<IdPickerIon>{

    private static final String sqlMapNameSpace = "IdPickerIon";
    
    private final  GenericProteinferIonDAO<?> ionDao;
    
    public IdPickerIonDAO(SqlMapClient sqlMap, GenericProteinferIonDAO<?> ionDao) {
        super(sqlMap);
        this.ionDao = ionDao;
    }

    @Override
    public int save(GenericProteinferIon<?> ion) {
        return ionDao.save(ion);
    }
    
    @Override
    public IdPickerIon load(int pinferIonId) {
        return (IdPickerIon) queryForObject(sqlMapNameSpace+".select", pinferIonId);
    }

    @Override
    public List<IdPickerIon> loadIonsForPeptide(int pinferPeptideId) {
        return queryForList(sqlMapNameSpace+".selectIonsForPeptide", pinferPeptideId);
    }
}
