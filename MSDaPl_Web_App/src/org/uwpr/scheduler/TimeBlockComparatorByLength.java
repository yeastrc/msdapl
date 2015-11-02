/**
 * 
 */
package org.uwpr.scheduler;

import java.util.Comparator;

import org.uwpr.costcenter.TimeBlock;

/**
 * TimeBlockComparatorByLength.java
 * @author Vagisha Sharma
 * Jul 19, 2011
 * 
 */
public class TimeBlockComparatorByLength implements Comparator<TimeBlock> {

	@Override
	public int compare(TimeBlock o1, TimeBlock o2) {
		
		return Integer.valueOf(o1.getNumHours()).compareTo(o2.getNumHours());
	}

}
