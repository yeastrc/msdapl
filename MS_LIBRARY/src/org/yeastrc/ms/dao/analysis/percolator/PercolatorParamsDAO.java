package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;

public interface PercolatorParamsDAO {

    public abstract List<PercolatorParam> loadParams(int analysisId);

    public abstract void saveParam(PercolatorParam param, int analysisId);
    
}
