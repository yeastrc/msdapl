/**
 * MascotSearchBean.java
 * @author Vagisha Sharma
 * Oct 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.impl.SearchBean;
import org.yeastrc.ms.domain.search.mascot.MascotSearch;


/**
 * 
 */
public class MascotSearchBean extends SearchBean implements MascotSearch {

    private List<Param> paramList;
    
    public MascotSearchBean() {
        paramList = new ArrayList<Param>();
    }
    
    @Override
    public List<Param> getMascotParams() {
        return paramList;
    }
    
    public void setMascotParams(List<Param> paramList) {
        this.paramList = paramList;
    }
}
