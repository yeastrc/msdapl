/**
 * TerminalModificationBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsTerminalModification;

/**
 * 
 */
public class TerminalModificationBean extends TerminalModification implements
        MsTerminalModification {

    private int id;
    
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
