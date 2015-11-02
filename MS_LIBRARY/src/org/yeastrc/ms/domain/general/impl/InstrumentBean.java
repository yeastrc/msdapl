/**
 * Instrument.java
 * @author Vagisha Sharma
 * Oct 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.general.impl;

import org.yeastrc.ms.domain.general.MsInstrument;

/**
 * 
 */
public class InstrumentBean implements MsInstrument {

    private int id;
    private String name;
    private String description;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
