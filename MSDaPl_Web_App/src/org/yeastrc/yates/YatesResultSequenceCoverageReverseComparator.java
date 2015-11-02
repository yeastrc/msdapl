/*
 * YatesRunSequenceCoverageComparator.java
 * Created on Oct 8, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.util.Comparator;

/**
 * Compare two YatesResult objects based on Sequence Coverage
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 8, 2004
 */

public class YatesResultSequenceCoverageReverseComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		double coverage1 = ((YatesResult)arg0).getSequenceCoverage();
		double coverage2 = ((YatesResult)arg1).getSequenceCoverage();
		
		if (coverage1 > coverage2) { return -1; }
		if (coverage1 < coverage2) { return 1; }
		return 0;
	}

}
