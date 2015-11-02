/**
 * InstrumentRateComparator.java
 * @author Vagisha Sharma
 * Jun 13, 2011
 */
package org.uwpr.costcenter;

import java.util.Comparator;

/**
 * 
 */
public class InstrumentRateComparator implements Comparator<InstrumentRate>{

	@Override
	public int compare(InstrumentRate o1, InstrumentRate o2) {
			int val = Integer.valueOf(o1.getInstrument().getID()).compareTo(o2.getInstrument().getID());
			if(val != 0)
				return val;
			val = Integer.valueOf(o1.getTimeBlock().getId()).compareTo(o2.getTimeBlock().getId());
			if(val != 0)
				return val;
			return Integer.valueOf(o1.getRateType().getId()).compareTo(o2.getRateType().getId());
//		}
	}

}
