/**
 * ProlucidParamDbImpl.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;

/**
 * 
 */
public class ProlucidParamBean implements ProlucidParam {

    private String elName;
    private String elValue;
    private int parentElId;
    private int id;
    
    @Override
    public String getParamElementName() {
        return elName;
    }

    @Override
    public String getParamElementValue() {
        return elValue;
    }

    @Override
    public int getParentParamElementId() {
        return parentElId;
    }

    public void setParamElementName(String elName) {
        this.elName = elName;
    }

    public void setParamElementValue(String elValue) {
        this.elValue = elValue;
    }

    public void setParentParamElementId(int parentElId) {
        this.parentElId = parentElId;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
