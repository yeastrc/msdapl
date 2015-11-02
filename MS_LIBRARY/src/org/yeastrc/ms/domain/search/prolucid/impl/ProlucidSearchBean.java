/**
 * ProlucidSearchDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.impl.SearchBean;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearch;

/**
 * 
 */
public class ProlucidSearchBean extends SearchBean implements ProlucidSearch {

    private List<ProlucidParam> paramList;

    public ProlucidSearchBean() {
        paramList = new ArrayList<ProlucidParam>();
    }

    @Override
    public List<ProlucidParam> getProlucidParams() {
        return paramList;
    }

    public void setProlucidParams(List<ProlucidParam> paramList) {
        this.paramList = paramList;
    }

}
