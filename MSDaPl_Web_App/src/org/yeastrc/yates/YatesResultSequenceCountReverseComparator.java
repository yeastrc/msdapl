/*
 * YatesResultSequenceCountComparator.java
 * Created on Oct 8, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.util.Comparator;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 8, 2004
 */

public class YatesResultSequenceCountReverseComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		int count1 = ((YatesResult)arg0).getSequenceCount();
		int count2 = ((YatesResult)arg1).getSequenceCount();
		
		if (count1 > count2) { return -1; }
		if (count1 < count2) { return 1; }
		return 0;
	}

}