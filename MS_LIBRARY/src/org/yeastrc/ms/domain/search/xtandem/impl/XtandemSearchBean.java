/**
 * XtandemSearchBean.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.impl.SearchBean;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearch;


/**
 * 
 */
public class XtandemSearchBean extends SearchBean implements XtandemSearch {

    private List<Param> paramList;
    
    public XtandemSearchBean() {
        paramList = new ArrayList<Param>();
    }
    
    @Override
    public List<Param> getXtandemParams() {
        return paramList;
    }
    
    public void setXtandemParams(List<Param> paramList) {
        this.paramList = paramList;
    }
}
