/* ProjectIDComparator.java
 * Created on May 19, 2004
 */
package org.yeastrc.project;

import java.util.Comparator;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, May 19, 2004
 *
 */
public class ProjectIDComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		Project p1 = (Project)arg0;
		Project p2 = (Project)arg1;
		
		int id1 = p1.getID();
		int id2 = p2.getID();
		
		if (id1 < id2) { return -1; }
		if (id1 > id2) { return 1; }
		return 0;
	}

}
