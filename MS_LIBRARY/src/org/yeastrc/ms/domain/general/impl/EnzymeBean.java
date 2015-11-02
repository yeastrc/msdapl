/**
 * MsEnzymeBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general.impl;

import org.yeastrc.ms.domain.general.MsEnzyme;

/**
 * 
 */
public class EnzymeBean extends Enzyme implements MsEnzyme {

    private int id; // id (database) from the enzyme
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

}
