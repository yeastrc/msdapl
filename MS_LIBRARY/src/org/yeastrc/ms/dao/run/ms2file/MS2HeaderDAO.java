package org.yeastrc.ms.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;

public interface MS2HeaderDAO {

    public abstract void save(MS2NameValuePair header, int runId);

    public abstract List<MS2NameValuePair> loadHeadersForRun(int runId);
    
    public abstract void deleteHeadersForRunId(int runId);
}