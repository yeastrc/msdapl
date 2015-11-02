/**
 * SQTRunSearchImpl.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile.impl;

import java.util.List;

import org.yeastrc.ms.domain.search.impl.MsRunSearchWrap;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;

/**
 * 
 */
public class SQTRunSearchWrap extends MsRunSearchWrap implements SQTRunSearch {

    private List<SQTHeaderItem> headers;
    
    public SQTRunSearchWrap(SQTRunSearchIn runSearch, int searchId, int runId) {
        super(runSearch, searchId, runId);
        this.headers = runSearch.getHeaders();
    }

    @Override
    public List<SQTHeaderItem> getHeaders() {
        return headers;
    }
}
