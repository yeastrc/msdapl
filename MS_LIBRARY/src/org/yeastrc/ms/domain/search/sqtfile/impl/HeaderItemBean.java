/**
 * HeaderItemBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile.impl;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

/**
 * 
 */
public class HeaderItemBean implements SQTHeaderItem {

    private String name;
    private String value;
    
    public HeaderItemBean() {}
    
    public HeaderItemBean(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("H\t");
        buf.append(name);
        if (value != null) {
            buf.append("\t");
            buf.append(value);
        }
        return buf.toString();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
