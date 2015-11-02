/**
 * Modification.java
 * @author Vagisha Sharma
 * Aug 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;

public class Modification {
    private int position;
    private BigDecimal mass;
    private Terminal terminus = null;
    
    public Modification(int pos, BigDecimal mass) {
        this.position = pos;
        this.mass = mass;
    }
    public Modification(BigDecimal mass, Terminal terminus) {
        this.mass = mass;
        this.terminus = terminus;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public BigDecimal getMass() {
        return mass;
    }
    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }
    public Terminal getTerminus() {
        return terminus;
    }
    public void setTerminus(Terminal terminus) {
        this.terminus = terminus;
    }
    public boolean isTerminalModification() {
        return this.terminus != null;
    }
}