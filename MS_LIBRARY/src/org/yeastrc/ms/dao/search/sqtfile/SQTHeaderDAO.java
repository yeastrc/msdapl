package org.yeastrc.ms.dao.search.sqtfile;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

public interface SQTHeaderDAO {

    public abstract List<SQTHeaderItem> loadSQTHeadersForRunSearch(int runSearchId);

    public abstract void saveSQTHeader(SQTHeaderItem headerItem);

    public abstract void deleteSQTHeadersForRunSearch(int runSearchId);

}