/*
 * YatesMSRunFactory.java
 * Created on Aug 10, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import org.yeastrc.ms.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Aug 10, 2004
 */

public class YatesMSRunFactory implements IMSRunFactory {

	/**
	 * Get the Yates MS Run with the given run ID
	 */
	public IMSRun getRun(int id) throws Exception {
		YatesRun yr = new YatesRun();
		yr.load(id);
		return (IMSRun)yr;
	}
	
}
