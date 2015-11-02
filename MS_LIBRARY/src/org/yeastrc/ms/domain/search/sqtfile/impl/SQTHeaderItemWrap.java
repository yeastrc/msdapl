package org.yeastrc.ms.domain.search.sqtfile.impl;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

public class SQTHeaderItemWrap implements SQTHeaderItem {

    private int runSearchId;
    private SQTHeaderItem headerItem;
    
    public SQTHeaderItemWrap(SQTHeaderItem headerItem, int runSearchId) {
        this.headerItem = headerItem;
        this.runSearchId = runSearchId;
    }
    
    public int getRunSearchId() {
        return runSearchId;
    }
    
    
    public String getName() {
        return headerItem.getName();
    }

    public String getValue() {
        return headerItem.getValue();
    }
}
