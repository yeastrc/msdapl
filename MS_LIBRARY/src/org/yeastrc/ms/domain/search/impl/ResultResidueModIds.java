/**
 * ResultResidueModIdentifierImpl.java
 * @author Vagisha Sharma
 * Sep 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsResultResidueModIds;

/**
 * 
 */
public class ResultResidueModIds extends ResultTerminalModIds implements
        MsResultResidueModIds {


    private final int modifiedPosition;
    
    public ResultResidueModIds(int resultId, int modId, int modifiedPosition) {
        super(resultId, modId);
        this.modifiedPosition = modifiedPosition;
    }
    
    @Override
    public int getModifiedPosition() {
        return modifiedPosition;
    }

}
