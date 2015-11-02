/* ProjectPIComparator.java
 * Created on May 12, 2004
 */
package org.yeastrc.project;

import java.util.Comparator;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, May 12, 2004
 *
 */
public class ProjectPIComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		Project p1 = (Project)arg0;
		Project p2 = (Project)arg1;
		
		Researcher r1 = p1.getPI();
		Researcher r2 = p2.getPI();
		
		if (r1 == null && r2 == null) return 0;
		if (r1 == null) return -1;
		if (r2 == null) return 1;
		
		return r1.compareTo(r2);
	}

}