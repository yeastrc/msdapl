/**
 * MsResultDynamicTerminalModDbImpl.java
 * @author Vagisha Sharma
 * Aug 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;

/**
 * 
 */
public class ResultTerminalModBean extends TerminalModification implements
        MsResultTerminalMod {
 
	public ResultTerminalModBean() {}
	
	public ResultTerminalModBean(Terminal modTerminal, char modSymbol, BigDecimal modMass) {
    	super(modTerminal, modSymbol, modMass);
    }
}
