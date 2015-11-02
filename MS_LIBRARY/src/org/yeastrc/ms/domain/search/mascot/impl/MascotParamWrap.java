/**
 * MascotParamWrap.java
 * @author Vagisha Sharma
 * Oct 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot.impl;

import org.yeastrc.ms.domain.search.Param;

/**
 * 
 */
public class MascotParamWrap implements Param {

    
    private int searchId;
    private Param param;
    
    public MascotParamWrap(Param param, int searchId) {
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
