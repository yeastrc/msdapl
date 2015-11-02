/*
 * YatesResultProteinListingComparator.java
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

public class YatesResultProteinListingComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		String listing1 = "";
		String listing2 = "";
		
		try {
			listing1 = ((YatesResult)arg0).getHitProtein().getListing();
			listing2 = ((YatesResult)arg1).getHitProtein().getListing();
		} catch (Exception e) { ; }
		
		return listing1.compareTo(listing2);
	}

}