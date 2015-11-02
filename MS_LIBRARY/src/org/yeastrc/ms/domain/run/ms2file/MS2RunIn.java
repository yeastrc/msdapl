/**
 * MS2Run.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.MsRunIn;

public interface MS2RunIn extends MsRunIn {

    /**
     * @return the list of headers for the MS2 run.
     */
    public abstract List<MS2NameValuePair> getHeaderList();

}