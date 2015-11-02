/**
 * SequestSearchDbImpl.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.impl.SearchBean;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;


/**
 * 
 */
public class SequestSearchBean extends SearchBean implements SequestSearch {

    private List<Param> paramList;
    
    public SequestSearchBean() {
        paramList = new ArrayList<Param>();
    }
    
    @Override
    public List<Param> getSequestParams() {
        return paramList;
    }
    
    public void setSequestParams(List<Param> paramList) {
        this.paramList = paramList;
    }
}
