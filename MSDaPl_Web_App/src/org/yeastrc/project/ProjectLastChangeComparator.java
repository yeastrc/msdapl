/* ProjectLastChangeComparator.java
 * Created on May 12, 2004
 */
package org.yeastrc.project;

import java.util.Comparator;
import java.util.Date;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, May 12, 2004
 *
 */
public class ProjectLastChangeComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		Project p1 = (Project)arg0;
		Project p2 = (Project)arg1;
		
		Date d1 = p1.getLastChange();
		Date d2 = p2.getLastChange();
		
		if (d1 == null && d2 == null) return 0;
		if (d1 == null) return -1;
		if (d2 == null) return 1;
		
		return d1.compareTo(d2);
	}

}
