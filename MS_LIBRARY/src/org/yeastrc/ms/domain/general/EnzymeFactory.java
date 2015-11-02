/**
 * EnzymeFactory.java
 * @author Vagisha Sharma
 * Mar 6, 2011
 */
package org.yeastrc.ms.domain.general;

import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.general.impl.EnzymeBean;

/**
 * 
 */
public class EnzymeFactory {

	private EnzymeFactory() {}
	
	
	public static MsEnzyme getTrypsin() {
		
		EnzymeBean enz = new EnzymeBean();
		enz.setName("trypsin");
        enz.setCut("KR");
        enz.setNocut("P");
        enz.setSense(Sense.CTERM);
        return enz;
	}
	
	public static MsEnzyme getEnzyme(String name) throws EnzymeFactoryException {
		if(name.equalsIgnoreCase("trypsin"))
			return getTrypsin();
		else
			throw new EnzymeFactoryException("Unrecognized enzyme: "+name);
	}
}
