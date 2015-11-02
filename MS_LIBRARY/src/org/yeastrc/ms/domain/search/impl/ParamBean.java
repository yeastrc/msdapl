/**
 * ParamBean.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.Param;

/**
 * 
 */
public class ParamBean implements Param {

    private String name;
    private String value;
    
    public ParamBean() {}
    
    public ParamBean(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String getParamName() {
        return name;
    }
    
    public void setParamName(String name) {
        this.name = name;
    }

    @Override
    public String getParamValue() {
        return value;
    }
    
    public void setParamValue(String value) {
        this.value = value;
    }

}
