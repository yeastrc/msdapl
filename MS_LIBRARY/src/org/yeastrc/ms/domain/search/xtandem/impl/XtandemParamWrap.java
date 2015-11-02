/**
 * XtandemParamWrap.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem.impl;

import org.yeastrc.ms.domain.search.Param;

/**
 * 
 */
public class XtandemParamWrap implements Param {

    
    private int searchId;
    private Param param;
    
    public XtandemParamWrap(Param param, int searchId) {
        this.param = param;
        this.searchId = searchId;
    }
   
    @Override
    public String getParamName() {
        return param.getParamName();
    }
    
    @Override
    public String getParamValue() {
        return param.getParamValue();
    }
    
    public int getSearchId() {
        return searchId;
    }
}
