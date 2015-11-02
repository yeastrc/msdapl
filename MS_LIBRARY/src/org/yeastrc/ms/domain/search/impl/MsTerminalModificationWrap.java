/**
 * MsTerminalModificationDbImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;

/**
 * 
 */
public class MsTerminalModificationWrap implements MsTerminalModification {

    private int id;
    private int searchId;
    private MsTerminalModificationIn mod;
    
    public MsTerminalModificationWrap(MsTerminalModificationIn mod, int searchId) {
        this.mod = mod;
        this.searchId = searchId;
    }

    public int getId() {
        return id;
    }

    public int getSearchId() {
        return searchId;
    }

    public BigDecimal getModificationMass() {
        return mod.getModificationMass();
    }

    public char getModificationSymbol() {
        return mod.getModificationSymbol();
    }
    
    @Override
    public Terminal getModifiedTerminal() {
        return mod.getModifiedTerminal();
    }

    @Override
    public void setModifiedTerminal(Terminal modTerminal) {
        this.mod.setModifiedTerminal(modTerminal);
    }

    @Override
    public void setModificationMass(BigDecimal modMass) {
        this.mod.setModificationMass(modMass);
    }

    @Override
    public void setModificationSymbol(char modSymbol) {
        this.mod.setModificationSymbol(modSymbol);
    }
}
