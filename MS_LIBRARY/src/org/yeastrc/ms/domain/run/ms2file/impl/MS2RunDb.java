/**
 * MS2FileRun.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.run.impl.RunDb;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;

/**
 * 
 */
public class MS2RunDb extends RunDb implements MS2Run {

    private List<MS2NameValuePair> headers;
    
    public MS2RunDb() {
        headers = new ArrayList<MS2NameValuePair>();
    }
    
    public void setHeaderList(List<MS2NameValuePair> headers) {
        this.headers = headers;
    }
    
    public List<MS2NameValuePair> getHeaderList() {
        return headers;
    }

    @Override
    public boolean isGeneratedByBullseye() {
        List<MS2NameValuePair> headers = getHeaderList();
        for(MS2NameValuePair header: headers) {
            if(header.getName().toLowerCase().startsWith("bullseye")) {
                return true;
            }
        }
        return false;
    }
}
