/**
 * UsageBlockComparatorByProject.java
 * @author Vagisha Sharma
 * Jul 21, 2011
 */
package org.uwpr.www.instrumentlog;

import java.util.Comparator;

import org.uwpr.instrumentlog.UsageBlockBase;

/**
 * 
 */
public class UsageBlockComparatorByProject implements Comparator<UsageBlockBase> {

	@Override
	public int compare(UsageBlockBase o1, UsageBlockBase o2) {
		
		return Integer.valueOf(o1.getProjectID()).compareTo(o2.getProjectID());
	}

}
