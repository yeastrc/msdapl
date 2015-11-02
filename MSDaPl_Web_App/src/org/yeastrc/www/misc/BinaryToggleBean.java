/*
 * BinaryToggleBean.java
 *
 * Created on February 8, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.misc;

public class BinaryToggleBean {

	// What we're currently toggled to.
	private boolean currToggle = false;

	/**
	 * Get the next toggle, this will alternate between true and false
	 * @return true if false was last returned, false if otherwise
	 */
	public boolean getToggle() {
		if (currToggle)
			currToggle = false;
		else
			currToggle = true;
		
		return currToggle;
	}
}