package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;

import com.ibatis.sqlmap.client.SqlMapClient;

public class PercolatorParamsDAOImpl extends BaseSqlMapDAO implements PercolatorParamsDAO {

    private static final String namespace = "PercolatorParams";
        
    public PercolatorParamsDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    @Override
    public List<PercolatorParam> loadParams(int analysisId) {
        return queryForList(namespace+".selectParams", analysisId);
    }

    @Override
    public void saveParam(PercolatorParam param, int analysisId) {
        PercolatorParamWrap wrap = new PercolatorParamWrap(param, analysisId);
        save(namespace+".insertParam",wrap);
    }

}
