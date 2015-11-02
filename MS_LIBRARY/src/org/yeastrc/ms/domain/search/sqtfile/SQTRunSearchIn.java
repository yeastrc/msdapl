package org.yeastrc.ms.domain.search.sqtfile;

import java.util.List;

import org.yeastrc.ms.domain.search.MsRunSearchIn;

public interface SQTRunSearchIn extends MsRunSearchIn {

    /**
     * Returns a list of headers associated with this SQT file
     * @return
     */
    public abstract List<SQTHeaderItem> getHeaders();

}